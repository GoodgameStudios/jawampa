package ws.wamp.jawampa.roles;

import static org.junit.Assert.*;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;

import rx.subjects.AsyncSubject;
import ws.wamp.jawampa.PubSubData;
import ws.wamp.jawampa.Reply;
import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.messages.CallMessage;
import ws.wamp.jawampa.messages.SubscribeMessage;
import ws.wamp.jawampa.messages.WampMessage;

public class SubscriberTest {
    @Mock private BaseClient baseClient;

    private String topic = "some_topic";

    @Before
    public void setup() {
        initMocks( this );
    }

    @Test
    public void testSubscribeSendsSubscribeMessage() {
        Subscriber subject = new Subscriber( baseClient );
        AsyncSubject<PubSubData> resultSubject = AsyncSubject.create();

        when( baseClient.getNewRequestId() ).thenReturn( RequestId.of( 42L ) );

        subject.subscribe( topic, resultSubject );

        ArgumentMatcher<WampMessage> messageMatcher = new ArgumentMatcher<WampMessage>() {
            @Override
            public boolean matches( Object argument ) {
                SubscribeMessage message = (SubscribeMessage)argument;
                if ( !message.requestId.equals( RequestId.of( 42L ) ) ) return false;
                if ( message.topic != topic ) return false;
                return true;
            }
        };
        verify( baseClient ).scheduleMessageToRouter( argThat( messageMatcher ) );
    }
}
