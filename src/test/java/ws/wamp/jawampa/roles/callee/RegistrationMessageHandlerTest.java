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
import ws.wamp.jawampa.messages.RegisterMessage;
import ws.wamp.jawampa.messages.RegisteredMessage;
import ws.wamp.jawampa.messages.WampMessage;

public class RegistrationMessageHandlerTest {
    private static final String TEST_URI = "arbitrary_uri";

    @Test
    public void testOnJavaFunctionRegistration() {
        class MockRegistrationCallback implements RegistrationCallback {
            public long registrationId;
            public String uri;
            @Override
            public void registrationComplete( long registrationId, String uri ) {
                if ( this.uri != null ) throw new IllegalStateException( "registrationComplete was called twice!" );
                this.registrationId = registrationId;
                this.uri = Objects.requireNonNull( uri );
            }
        }

        PublishSubject<PendingRegistration> magicAsyncQueue = PublishSubject.create();
        BaseClient baseClient = mock(BaseClient.class);
        when( baseClient.getNewRequestId() ).thenReturn( 42L );

        RegistrationMessageHandler subject = new RegistrationMessageHandler( baseClient, magicAsyncQueue );

        MockRegistrationCallback handleRegistrationIdCallback = new MockRegistrationCallback();
        magicAsyncQueue.onNext( new PendingRegistration( TEST_URI, handleRegistrationIdCallback) );

        ArgumentMatcher<WampMessage> messageMatcher = new ArgumentMatcher<WampMessage>() {
            @Override
            public boolean matches( Object argument ) {
                RegisterMessage message = (RegisterMessage)argument;
                if ( message.requestId != 42 ) return false;
                if ( !message.procedure.equals( TEST_URI ) ) return false;
                return true;
            }
        };
        verify( baseClient ).scheduleMessageToRouter( argThat( messageMatcher ) );

        RegisteredMessage answerFromRouter = new RegisteredMessage( 42L, 23L );
        subject.onRegistered( answerFromRouter );

        assertEquals( 23L, handleRegistrationIdCallback.registrationId );
        assertEquals( TEST_URI, handleRegistrationIdCallback.uri );
    }
}
