package ws.wamp.jawampa.roles;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;

import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.messages.PublishMessage;
import ws.wamp.jawampa.messages.WampMessage;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PublisherTest {
    @Mock private BaseClient baseClient;

    private String topic = "some_topic";
    @Mock private ArrayNode arguments;
    @Mock private ObjectNode kwArguments;

    @Before
    public void setup() {
        initMocks( this );
    }

    @Test
    public void testPublishSendsPublishMessage() {
        Publisher subject = new Publisher( baseClient );

        when( baseClient.getNewRequestId() ).thenReturn( RequestId.of( 42L ) );

        subject.publish( topic, arguments, kwArguments );

        ArgumentMatcher<WampMessage> messageMatcher = new ArgumentMatcher<WampMessage>() {
            @Override
            public boolean matches( Object argument ) {
                PublishMessage message = (PublishMessage)argument;
                if ( !message.requestId.equals( RequestId.of( 42L ) ) ) return false;
                if ( message.topic != topic ) return false;
                if ( message.arguments != arguments ) return false;
                if ( message.argumentsKw != kwArguments ) return false;
                return true;
            }
        };
        verify( baseClient ).scheduleMessageToRouter( argThat( messageMatcher ) );
    }
}
