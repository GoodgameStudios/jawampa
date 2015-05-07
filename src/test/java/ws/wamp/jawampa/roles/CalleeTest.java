package ws.wamp.jawampa.roles;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;
import org.mockito.Mock;

import rx.Observer;
import rx.subjects.PublishSubject;
import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.Request;
import ws.wamp.jawampa.ids.RegistrationId;
import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.messages.ErrorMessage;
import ws.wamp.jawampa.messages.EventMessage;
import ws.wamp.jawampa.messages.InvocationMessage;
import ws.wamp.jawampa.messages.RegisterMessage;
import ws.wamp.jawampa.messages.RegisteredMessage;
import ws.wamp.jawampa.messages.SubscribeMessage;
import ws.wamp.jawampa.messages.SubscribedMessage;
import ws.wamp.jawampa.messages.UnregisterMessage;
import ws.wamp.jawampa.messages.UnregisteredMessage;
import ws.wamp.jawampa.messages.UnsubscribedMessage;
import ws.wamp.jawampa.messages.WampMessage;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CalleeTest {
    private static final RequestId REQUEST_ID = RequestId.of( 42L );
    private static final RequestId REQUEST_ID2 = RequestId.of( 57L );
    private static final RequestId REQUEST_ID3 = RequestId.of( 97L );
    private static final RegistrationId REGISTRATION_ID = RegistrationId.of( 23L );

    @Mock private BaseClient baseClient;

    private String procedure = "some_procedure";
    @Mock private ArrayNode arguments;
    @Mock private ObjectNode kwArguments;
    @Mock private ArrayNode replyArguments;
    @Mock private ObjectNode replyKwArguments;

    private Callee subject;
    private PublishSubject<Request> callSubject;
    @Mock private Observer<Request> callObserver;
    private PublishSubject<Void> unsubscribeSubject;
    @Mock private Observer<Void> unsubscriptionObserver;

    @Before
    public void setup() {
        initMocks( this );

        subject = new Callee( baseClient );

        callSubject = PublishSubject.create();
        callSubject.subscribe( callObserver );

        unsubscribeSubject = PublishSubject.create();
        unsubscribeSubject.subscribe( unsubscriptionObserver );

        when( baseClient.getNewRequestId() ).thenReturn( REQUEST_ID ).thenReturn( REQUEST_ID2 );
    }

    @Test
    public void testRegisterSendsRegisterMessage() {
        subject.register( procedure, callSubject );

        ArgumentMatcher<WampMessage> messageMatcher = new ArgumentMatcher<WampMessage>() {
            @Override
            public boolean matches( Object argument ) {
                RegisterMessage message = (RegisterMessage)argument;
                if ( !message.requestId.equals( REQUEST_ID ) ) return false;
                if ( message.procedure != procedure ) return false;
                return true;
            }
        };
        verify( baseClient ).scheduleMessageToRouter( argThat( messageMatcher ) );
    }

    @Test
    public void testMethodIsCalledAfterRegistration() {
        subject.register( procedure, callSubject );
        subject.onRegistered( new RegisteredMessage( REQUEST_ID, REGISTRATION_ID ) );

        subject.onInvocation( new InvocationMessage( REQUEST_ID2, REGISTRATION_ID, null, arguments, kwArguments ) );

        ArgumentMatcher<Request> requestMatcher = new ArgumentMatcher<Request>() {
            @Override
            public boolean matches( Object argument ) {
                Request data = (Request)argument;
                if ( data.arguments() != arguments ) return false;
                if ( data.keywordArguments() != kwArguments ) return false;
                return true;
            }
        };
        verify( callObserver ).onNext( argThat( requestMatcher ) );
        verify( callObserver, never()).onCompleted();
        verify( callObserver, never()).onError( any( Throwable.class ) );
    }

    @Test
    public void testSubscriptionErrorIsDeliveredToClient() {
        subject.register( procedure, callSubject );

        subject.onRegisterError( new ErrorMessage( RegisterMessage.ID,
                                                   REQUEST_ID,
                                                   null,
                                                   ApplicationError.INVALID_ARGUMENT,
                                                   null,
                                                   null ) );
        verify( callObserver, never() ).onNext( any( Request.class ) );
        verify( callObserver, never()).onCompleted();
        verify( callObserver).onError( any( Throwable.class ) );
    }

    @Test
    public void testUnsubscribeSendsUnsubscribeMessage() {
        subject.register( procedure, callSubject );
        subject.onRegistered( new RegisteredMessage( REQUEST_ID, REGISTRATION_ID ) );

        subject.unregister( procedure, unsubscribeSubject );

        ArgumentMatcher<WampMessage> messageMatcher = new ArgumentMatcher<WampMessage>() {
            @Override
            public boolean matches( Object argument ) {
                UnregisterMessage message = (UnregisterMessage)argument;
                if ( !message.requestId.equals( REQUEST_ID2 ) ) return false;
                if ( !message.registrationId.equals( REGISTRATION_ID ) ) return false;
                return true;
            }
        };
        InOrder inOrder = inOrder( baseClient );
        inOrder.verify( baseClient ).scheduleMessageToRouter( any( WampMessage.class ) );
        inOrder.verify( baseClient ).scheduleMessageToRouter( argThat( messageMatcher ) );
    }

    @Test
    public void testSuccessfulUnsubscribeIsDeliveredToClient() {
        subject.register( procedure, callSubject );
        subject.onRegistered( new RegisteredMessage( REQUEST_ID, REGISTRATION_ID ) );
        subject.unregister( procedure, unsubscribeSubject );

        subject.onUnregistered( new UnregisteredMessage( REQUEST_ID2 ) );

        verify( unsubscriptionObserver, never() ).onNext( any( Void.class ) );
        verify( unsubscriptionObserver ).onCompleted();
        verify( unsubscriptionObserver, never() ).onError( any( Throwable.class ) );
    }

    @Test
    public void testUnsubscribeErrorIsDeliveredToClient() {
        subject.register( procedure, callSubject );
        subject.onRegistered( new RegisteredMessage( REQUEST_ID, REGISTRATION_ID ) );
        subject.unregister( procedure, unsubscribeSubject );

        subject.onUnregisterError( new ErrorMessage( SubscribeMessage.ID,
                                                     REQUEST_ID2,
                                                     null,
                                                     ApplicationError.INVALID_ARGUMENT,
                                                     null,
                                                     null ) );

        verify( unsubscriptionObserver, never() ).onNext( any( Void.class ) );
        verify( unsubscriptionObserver, never() ).onCompleted();
        verify( unsubscriptionObserver ).onError( any( ApplicationError.class ) );
    }

    @Test
    public void testInvokeAfterUnregisteredIsAnError() {
        subject.register( procedure, callSubject );
        subject.onRegistered( new RegisteredMessage( REQUEST_ID, REGISTRATION_ID ) );
        subject.unregister( procedure, unsubscribeSubject );
        subject.onUnregistered( new UnregisteredMessage( REQUEST_ID2 ) );

        subject.onInvocation( new InvocationMessage( REQUEST_ID3, REGISTRATION_ID, null, arguments, kwArguments ) );

        verify( baseClient ).onProtocolError();
    }

    @Test
    public void testInvokeDuringUnregisteringMustStillWork() {
        subject.register( procedure, callSubject );
        subject.onRegistered( new RegisteredMessage( REQUEST_ID, REGISTRATION_ID ) );
        subject.unregister( procedure, unsubscribeSubject );

        subject.onInvocation( new InvocationMessage( REQUEST_ID3, REGISTRATION_ID, null, arguments, kwArguments ) );

        ArgumentMatcher<Request> requestMatcher = new ArgumentMatcher<Request>() {
            @Override
            public boolean matches( Object argument ) {
                Request data = (Request)argument;
                if ( data.arguments() != arguments ) return false;
                if ( data.keywordArguments() != kwArguments ) return false;
                return true;
            }
        };
        verify( callObserver ).onNext( argThat( requestMatcher ) );
        verify( callObserver, never()).onCompleted();
        verify( callObserver, never()).onError( any( Throwable.class ) );
    }
}
