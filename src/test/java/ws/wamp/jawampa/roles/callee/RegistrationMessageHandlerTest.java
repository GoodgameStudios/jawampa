package ws.wamp.jawampa.roles.callee;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Objects;

import org.junit.Test;
import org.mockito.ArgumentMatcher;

import rx.subjects.PublishSubject;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.io.RequestId;
import ws.wamp.jawampa.messages.ErrorMessage;
import ws.wamp.jawampa.messages.RegisterMessage;
import ws.wamp.jawampa.messages.RegisteredMessage;
import ws.wamp.jawampa.messages.WampMessage;

public class RegistrationMessageHandlerTest {
    private static final String SOME_ERROR = "some error";
    private static final String TEST_URI = "arbitrary_uri";

    class MockRegistrationCallback implements RegistrationStateWatcher {
        public RegistrationId registrationId;
        public String uri;
        public String failedUri;
        public String failedReason;
        @Override
        public void registrationComplete( RegistrationId registrationId, String uri ) {
            if ( this.uri != null ) throw new IllegalStateException( "registrationComplete was called twice!" );
            if ( this.failedUri != null ) throw new IllegalStateException( "registrationFailed was already called!" );
            this.registrationId = Objects.requireNonNull( registrationId );
            this.uri = Objects.requireNonNull( uri );
        }

        @Override
        public void registrationFailed( String uri, String reason ) {
            if ( this.uri != null ) throw new IllegalStateException( "registrationComplete was already called!" );
            if ( this.failedUri != null ) throw new IllegalStateException( "registrationFailed was called twice!" );
            this.failedUri = Objects.requireNonNull( uri );
            this.failedReason = Objects.requireNonNull( reason );
        }

        @Override
        public void unregistrationComplete( RegistrationId registrationId ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void unregistrationFailed( RegistrationId registrationId, String reason ) {
            throw new UnsupportedOperationException();
        }
    }

    @Test
    public void testOnJavaFunctionRegistration() {
        PublishSubject<PendingRegistration> magicAsyncQueue = PublishSubject.create();
        BaseClient baseClient = mock(BaseClient.class);
        when( baseClient.getNewRequestId() ).thenReturn( RequestId.of( 42L ) );

        RegistrationMessageHandler subject = new RegistrationMessageHandler( baseClient, magicAsyncQueue );

        MockRegistrationCallback handleRegistrationIdCallback = new MockRegistrationCallback();
        magicAsyncQueue.onNext( new PendingRegistration( TEST_URI, handleRegistrationIdCallback) );

        ArgumentMatcher<WampMessage> messageMatcher = new ArgumentMatcher<WampMessage>() {
            @Override
            public boolean matches( Object argument ) {
                RegisterMessage message = (RegisterMessage)argument;
                if ( !message.requestId.equals( RequestId.of( 42L ) ) ) return false;
                if ( !message.procedure.equals( TEST_URI ) ) return false;
                return true;
            }
        };
        verify( baseClient ).scheduleMessageToRouter( argThat( messageMatcher ) );

        RegisteredMessage answerFromRouter = new RegisteredMessage( RequestId.of( 42L ), RegistrationId.of( 23L ) );
        subject.onRegistered( answerFromRouter );

        assertEquals( RegistrationId.of( 23L ), handleRegistrationIdCallback.registrationId );
        assertEquals( TEST_URI, handleRegistrationIdCallback.uri );
    }

    @Test
    public void testErrorHandling() {
        PublishSubject<PendingRegistration> magicAsyncQueue = PublishSubject.create();
        BaseClient baseClient = mock(BaseClient.class);
        when( baseClient.getNewRequestId() ).thenReturn( RequestId.of( 42L ) );

        RegistrationMessageHandler subject = new RegistrationMessageHandler( baseClient, magicAsyncQueue );

        MockRegistrationCallback handleRegistrationIdCallback = new MockRegistrationCallback();
        magicAsyncQueue.onNext( new PendingRegistration( TEST_URI, handleRegistrationIdCallback) );

        ErrorMessage answerFromRouter = new ErrorMessage( RegisterMessage.ID, RequestId.of( 42L ), null, SOME_ERROR, null, null );
        subject.onRegisterError( answerFromRouter );

        assertEquals( TEST_URI, handleRegistrationIdCallback.failedUri );
        assertEquals( SOME_ERROR, handleRegistrationIdCallback.failedReason );
    }
}
