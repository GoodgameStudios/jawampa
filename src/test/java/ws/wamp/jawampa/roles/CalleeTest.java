package ws.wamp.jawampa.roles;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;

import rx.Observer;
import rx.subjects.PublishSubject;
import ws.wamp.jawampa.Request;
import ws.wamp.jawampa.ids.RegistrationId;
import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.messages.RegisterMessage;
import ws.wamp.jawampa.messages.SubscribeMessage;
import ws.wamp.jawampa.messages.WampMessage;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CalleeTest {
    private static final RequestId REQUEST_ID = RequestId.of( 42L );
    private static final RequestId REQUEST_ID2 = RequestId.of( 57L );
    private static final RegistrationId REGISTRATION_ID = RegistrationId.of( 23L );

    @Mock private BaseClient baseClient;

    private String procedure = "some_procedure";
    @Mock private ArrayNode arguments;
    @Mock private ObjectNode kwArguments;
    @Mock private ArrayNode replyArguments;
    @Mock private ObjectNode replyKwArguments;

    private Callee subject;
    private PublishSubject<Request> resultSubject;
    @Mock private Observer<Request> resultObserver;
    private PublishSubject<Void> unsubscribeSubject;
    @Mock private Observer<Void> unsubscriptionObserver;

    @Before
    public void setup() {
        initMocks( this );

        subject = new Callee( baseClient );

        resultSubject = PublishSubject.create();
        resultSubject.subscribe( resultObserver );

        unsubscribeSubject = PublishSubject.create();
        unsubscribeSubject.subscribe( unsubscriptionObserver );

        when( baseClient.getNewRequestId() ).thenReturn( REQUEST_ID ).thenReturn( REQUEST_ID2 );
    }

    @Test
    public void testRegisterSendsRegisterMessage() {
        subject.register( procedure, resultSubject );

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
}
