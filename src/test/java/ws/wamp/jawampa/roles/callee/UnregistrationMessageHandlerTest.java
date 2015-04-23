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
import ws.wamp.jawampa.messages.UnregisterMessage;
import ws.wamp.jawampa.messages.UnregisteredMessage;
import ws.wamp.jawampa.messages.WampMessage;

public class UnregistrationMessageHandlerTest {
    private static final String SOME_ERROR = "some error";
    private static final String TEST_URI = "arbitrary_uri";

    class MockRegistrationCallback implements RegistrationStateWatcher {
        public RegistrationId unregisteredId;
        public RegistrationId failedUnregisteredId;
        public String failedReason;
        @Override
        public void registrationComplete( RegistrationId registrationId, String uri ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void registrationFailed( String uri, String reason ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void unregistrationComplete( RegistrationId registrationId ) {
            if ( this.unregisteredId != null ) throw new IllegalStateException( "unregistrationComplete was called twice!" );
            if ( this.failedUnregisteredId != null ) throw new IllegalStateException( "unregistrationFailed was already called!" );
            this.unregisteredId = Objects.requireNonNull( registrationId );
        }

        @Override
        public void unregistrationFailed( RegistrationId registrationId, String reason ) {
            if ( this.unregisteredId != null ) throw new IllegalStateException( "unregistrationComplete was already called!" );
            if ( this.failedUnregisteredId != null ) throw new IllegalStateException( "unregistrationFailed was called twice!" );
            this.failedUnregisteredId = Objects.requireNonNull( registrationId );
            this.failedReason = Objects.requireNonNull( reason );
        }
    }

    @Test
    public void testOnJavaFunctionRegistration() {
        PublishSubject<PendingUnregistration> magicAsyncQueue = PublishSubject.create();
        BaseClient baseClient = mock(BaseClient.class);
        when( baseClient.getNewRequestId() ).thenReturn( RequestId.of( 42L ) );

        UnregistrationMessageHandler subject = new UnregistrationMessageHandler( baseClient, magicAsyncQueue );

        MockRegistrationCallback handleRegistrationIdCallback = new MockRegistrationCallback();
        magicAsyncQueue.onNext( new PendingUnregistration( TEST_URI, handleRegistrationIdCallback) );

        ArgumentMatcher<WampMessage> messageMatcher = new ArgumentMatcher<WampMessage>() {
            @Override
            public boolean matches( Object argument ) {
                UnregisterMessage message = (UnregisterMessage)argument;
                if ( !message.requestId.equals( RequestId.of( 42L ) ) ) return false;
                if ( !message.registrationId.equals( RequestId.of( 23L ) ) ) return false;
                return true;
            }
        };
        verify( baseClient ).scheduleMessageToRouter( argThat( messageMatcher ) );

        UnregisteredMessage answerFromRouter = new UnregisteredMessage( RequestId.of( 42L ) );
        subject.onUnregistered( answerFromRouter );

        assertEquals( RegistrationId.of( 23L ), handleRegistrationIdCallback.unregisteredId );
    }

    @Test
    public void testErrorHandling() {
        PublishSubject<PendingUnregistration> magicAsyncQueue = PublishSubject.create();
        BaseClient baseClient = mock(BaseClient.class);
        when( baseClient.getNewRequestId() ).thenReturn( RequestId.of( 42L ) );

        UnregistrationMessageHandler subject = new UnregistrationMessageHandler( baseClient, magicAsyncQueue );

        MockRegistrationCallback handleRegistrationIdCallback = new MockRegistrationCallback();
        magicAsyncQueue.onNext( new PendingUnregistration( TEST_URI, handleRegistrationIdCallback) );

        ErrorMessage answerFromRouter = new ErrorMessage( RegisterMessage.ID, RequestId.of( 42L ), null, SOME_ERROR, null, null );
        subject.onUnregisterError( answerFromRouter );

        assertEquals( SOME_ERROR, handleRegistrationIdCallback.failedReason );
    }
}
