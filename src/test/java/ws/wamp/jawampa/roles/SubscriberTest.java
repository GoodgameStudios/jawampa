package ws.wamp.jawampa.roles;

import static org.junit.Assert.*;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import rx.Observer;
import rx.subjects.PublishSubject;
import ws.wamp.jawampa.PubSubData;
import ws.wamp.jawampa.Reply;
import ws.wamp.jawampa.ids.PublicationId;
import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.ids.SubscriptionId;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.messages.CallMessage;
import ws.wamp.jawampa.messages.EventMessage;
import ws.wamp.jawampa.messages.SubscribeMessage;
import ws.wamp.jawampa.messages.SubscribedMessage;
import ws.wamp.jawampa.messages.UnsubscribeMessage;
import ws.wamp.jawampa.messages.WampMessage;

public class SubscriberTest {
    @Mock private BaseClient baseClient;

    private String topic = "some_topic";
    @Mock private ArrayNode arguments;
    @Mock private ObjectNode kwArguments;

    @Before
    public void setup() {
        initMocks( this );
    }

    @Test
    public void testSubscribeSendsSubscribeMessage() {
        Subscriber subject = new Subscriber( baseClient );
        PublishSubject<PubSubData> resultSubject = PublishSubject.create();

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

    @Test
    public void testEventIsDeliveredAfterSubscription() {
        Subscriber subject = new Subscriber( baseClient );

        PublishSubject<PubSubData> resultSubject = PublishSubject.create();
        @SuppressWarnings( "unchecked" )
        Observer<PubSubData> publicationObserver = mock(Observer.class);
        resultSubject.subscribe( publicationObserver );

        when( baseClient.getNewRequestId() ).thenReturn( RequestId.of( 42L ) );

        subject.subscribe( topic, resultSubject );

        subject.onSubscribed( new SubscribedMessage( RequestId.of( 42L ), SubscriptionId.of( 23L ) ) );
        subject.onEvent( new EventMessage( SubscriptionId.of( 23L ), PublicationId.of( 17L ), null, arguments, kwArguments ) );

        ArgumentMatcher<PubSubData> dataMatcher = new ArgumentMatcher<PubSubData>() {
            @Override
            public boolean matches( Object argument ) {
                PubSubData data = (PubSubData)argument;
                if ( data.arguments() != arguments ) return false;
                if ( data.keywordArguments() != kwArguments ) return false;
                return true;
            }
        };
        verify( publicationObserver ).onNext( argThat( dataMatcher ) );
    }
}
