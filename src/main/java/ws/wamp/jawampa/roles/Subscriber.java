package ws.wamp.jawampa.roles;

import java.util.HashMap;
import java.util.Map;

import rx.subjects.PublishSubject;
import ws.wamp.jawampa.PubSubData;
import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.ids.SubscriptionId;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.messages.ErrorMessage;
import ws.wamp.jawampa.messages.EventMessage;
import ws.wamp.jawampa.messages.SubscribeMessage;
import ws.wamp.jawampa.messages.SubscribedMessage;
import ws.wamp.jawampa.messages.UnsubscribedMessage;
import ws.wamp.jawampa.messages.WampMessage;
import ws.wamp.jawampa.messages.handling.BaseMessageHandler;
import ws.wamp.jawampa.roles.RequestTracker.MessageFactory;

public class Subscriber extends BaseMessageHandler {
    private final BaseClient baseClient;

    private final RequestTracker<PubSubData> registrationTracker;
    private final RequestTracker<Void> unregistrationTracker;

    private final Map<SubscriptionId, PublishSubject<PubSubData>> subscriptionId2PublishSubject = new HashMap<SubscriptionId, PublishSubject<PubSubData>>();

    public Subscriber( BaseClient baseClient ) {
        this.baseClient = baseClient;

        registrationTracker = new RequestTracker.Builder<PubSubData>( baseClient ).dontCompleteAsyncOnSuccess()
                                                                                  .build();
        unregistrationTracker = new RequestTracker.Builder<Void>( baseClient ).completeAsyncOnSuccess()
                                                                              .build();
    }

    public void subscribe( final String topic, PublishSubject<PubSubData> resultSubject ) {
        registrationTracker.sendRequest( resultSubject, new MessageFactory() {
            @Override
            public WampMessage fromRequestId( RequestId requestId ) {
                return new SubscribeMessage( requestId, null, topic );
            }
        } );
    }

    public void unsubscribe( String topic, PublishSubject<Void> resultSubject ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public void onSubscribed( SubscribedMessage msg ) {
        subscriptionId2PublishSubject.put( msg.subscriptionId, registrationTracker.onSuccess( msg.requestId, null ) );
    }

    @Override
    public void onSubscribeError( ErrorMessage msg ) {
        registrationTracker.onError( msg );
    }

    @Override
    public void onUnsubscribed( UnsubscribedMessage msg ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public void onUnsubscribeError( ErrorMessage msg ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public void onEvent( EventMessage msg ) {
        PublishSubject<PubSubData> async = subscriptionId2PublishSubject.get( msg.subscriptionId );
        async.onNext( new PubSubData( msg.arguments, msg.argumentsKw ) );
    }
}
