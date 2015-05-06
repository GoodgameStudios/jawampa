package ws.wamp.jawampa.roles;

import java.util.HashMap;
import java.util.Map;

import rx.subjects.AsyncSubject;
import ws.wamp.jawampa.PubSubData;
import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.messages.ErrorMessage;
import ws.wamp.jawampa.messages.EventMessage;
import ws.wamp.jawampa.messages.SubscribeMessage;
import ws.wamp.jawampa.messages.SubscribedMessage;
import ws.wamp.jawampa.messages.UnsubscribedMessage;
import ws.wamp.jawampa.messages.handling.BaseMessageHandler;

public class Subscriber extends BaseMessageHandler {
    private final BaseClient baseClient;

    private final Map<RequestId, AsyncSubject<PubSubData>> registrationRequestId2AsyncSubject = new HashMap<RequestId, AsyncSubject<PubSubData>>();
    private final Map<RequestId, AsyncSubject<Void>> unregistrationRequestId2AsyncSubject = new HashMap<RequestId, AsyncSubject<Void>>();

    public Subscriber( BaseClient baseClient ) {
        this.baseClient = baseClient;
    }

    public void subscribe( String topic, AsyncSubject<PubSubData> resultSubject ) {
        RequestId requestId = baseClient.getNewRequestId();
        baseClient.scheduleMessageToRouter( new SubscribeMessage( requestId, null, topic ) );
    }

    public void unsubscribe( String topic, AsyncSubject<Void> resultSubject ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public void onSubscribed( SubscribedMessage msg ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public void onSubscribeError( ErrorMessage msg ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }
}
