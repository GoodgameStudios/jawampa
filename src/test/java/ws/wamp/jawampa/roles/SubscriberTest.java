package ws.wamp.jawampa.roles;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;

import rx.Observer;
import rx.subjects.PublishSubject;
import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.PubSubData;
import ws.wamp.jawampa.ids.PublicationId;
import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.ids.SubscriptionId;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.messages.ErrorMessage;
import ws.wamp.jawampa.messages.EventMessage;
import ws.wamp.jawampa.messages.SubscribeMessage;
import ws.wamp.jawampa.messages.SubscribedMessage;
import ws.wamp.jawampa.messages.WampMessage;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class SubscriberTest {
    private static final RequestId REQUEST_ID = RequestId.of( 42L );
    private static final SubscriptionId SUBSCRIPTION_ID = SubscriptionId.of( 23L );
    private static final PublicationId PUBLICATION_ID = PublicationId.of( 17L );

    @Mock private BaseClient baseClient;

    private String topic = "some_topic";
    @Mock private ArrayNode arguments;
    @Mock private ObjectNode kwArguments;

    private Subscriber subject;
    private PublishSubject<PubSubData> resultSubject;
    @Mock private Observer<PubSubData> publicationObserver;

    @Before
    public void setup() {
        initMocks( this );

        subject = new Subscriber( baseClient );

        resultSubject = PublishSubject.create();
        resultSubject.subscribe( publicationObserver );

        when( baseClient.getNewRequestId() ).thenReturn( REQUEST_ID );
    }

    @Test
    public void testSubscribeSendsSubscribeMessage() {
        subject.subscribe( topic, resultSubject );

        ArgumentMatcher<WampMessage> messageMatcher = new ArgumentMatcher<WampMessage>() {
            @Override
            public boolean matches( Object argument ) {
                SubscribeMessage message = (SubscribeMessage)argument;
                if ( !message.requestId.equals( REQUEST_ID ) ) return false;
                if ( message.topic != topic ) return false;
                return true;
            }
        };
        verify( baseClient ).scheduleMessageToRouter( argThat( messageMatcher ) );
    }

    @Test
    public void testEventIsDeliveredAfterSubscription() {
        subject.subscribe( topic, resultSubject );

        subject.onSubscribed( new SubscribedMessage( REQUEST_ID, SUBSCRIPTION_ID ) );
        subject.onEvent( new EventMessage( SUBSCRIPTION_ID, PUBLICATION_ID, null, arguments, kwArguments ) );

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
        verify( publicationObserver, never()).onCompleted();
        verify( publicationObserver, never()).onError( any( Throwable.class ) );
    }

    @Test
    public void testSubscriptionErrorIsDeliveredToClient() {
        subject.subscribe( topic, resultSubject );

        subject.onSubscribeError( new ErrorMessage( SubscribeMessage.ID,
                                                    REQUEST_ID,
                                                    null,
                                                    ApplicationError.INVALID_ARGUMENT,
                                                    null,
                                                    null ) );
        verify( publicationObserver, never() ).onNext( any( PubSubData.class ) );
        verify( publicationObserver, never()).onCompleted();
        verify( publicationObserver).onError( any( Throwable.class ) );
    }
}
