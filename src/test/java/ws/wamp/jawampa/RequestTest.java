package ws.wamp.jawampa;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;

import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.messages.YieldMessage;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class RequestTest {
    private Request subject;

    @Mock private BaseClient baseClient;

    private static final RequestId REQUEST_ID = RequestId.of( 42L );
    @Mock private ArrayNode arguments;
    @Mock private ObjectNode kwArguments;

    @Before
    public void setUp() {
        initMocks( this );

        subject = new Request( baseClient, REQUEST_ID, arguments, kwArguments );
    }

    @Test
    public void testReplySendsYieldMessage() {
        subject.reply( arguments, kwArguments );

        ArgumentMatcher<YieldMessage> messageMatcher = new ArgumentMatcher<YieldMessage>() {
            @Override
            public boolean matches( Object argument ) {
                YieldMessage message = (YieldMessage)argument;
                if ( !message.requestId.equals( REQUEST_ID ) ) return false;
                if ( message.arguments != arguments ) return false;
                if ( message.argumentsKw != kwArguments ) return false;
                return true;
            }
        };
        verify( baseClient ).scheduleMessageToRouter( argThat( messageMatcher ) );
    }
}
