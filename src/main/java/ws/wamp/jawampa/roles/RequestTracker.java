package ws.wamp.jawampa.roles;

import java.util.HashMap;
import java.util.Map;

import rx.subjects.PublishSubject;
import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.messages.ErrorMessage;
import ws.wamp.jawampa.messages.WampMessage;

public class RequestTracker<ReplyT> {
    private final BaseClient baseClient;
    private final Map<RequestId, PublishSubject<ReplyT>> requestId2PublishSubject = new HashMap<RequestId, PublishSubject<ReplyT>>();

    public RequestTracker( BaseClient baseClient ) {
        this.baseClient = baseClient;
    }

    public void sendRequest( PublishSubject<ReplyT> resultSubject, MessageFactory factory ) {
        RequestId requestId = baseClient.getNewRequestId();
        requestId2PublishSubject.put( requestId, resultSubject );
        baseClient.scheduleMessageToRouter( factory.fromRequestId( requestId ) );
    }

    public PublishSubject<ReplyT> onSuccess( RequestId requestId, ReplyT reply ) {
        if ( reply != null ) {
            requestId2PublishSubject.get( requestId ).onNext( reply );
        }
        requestId2PublishSubject.get( requestId ).onCompleted();
        return requestId2PublishSubject.remove( requestId );
    }

    public PublishSubject<ReplyT> onError( ErrorMessage msg ) {
        RequestId requestId = msg.requestId;
        requestId2PublishSubject.get( requestId ).onError( new ApplicationError( msg.error, msg.arguments, msg.argumentsKw ) );
        return requestId2PublishSubject.remove( requestId );
    }

    public interface MessageFactory {
        public WampMessage fromRequestId( RequestId requestId );
    }
}
