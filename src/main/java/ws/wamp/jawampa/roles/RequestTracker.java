package ws.wamp.jawampa.roles;

import java.util.HashMap;
import java.util.Map;

import rx.subjects.AsyncSubject;
import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.messages.ErrorMessage;
import ws.wamp.jawampa.messages.WampMessage;

public class RequestTracker<ReplyT> {
    private final BaseClient baseClient;
    private final Map<RequestId, AsyncSubject<ReplyT>> requestId2AsyncSubject = new HashMap<RequestId, AsyncSubject<ReplyT>>();

    public RequestTracker( BaseClient baseClient ) {
        this.baseClient = baseClient;
    }

    public void sendRequest( AsyncSubject<ReplyT> resultSubject, MessageFactory factory ) {
        RequestId requestId = baseClient.getNewRequestId();
        requestId2AsyncSubject.put( requestId, resultSubject );
        baseClient.scheduleMessageToRouter( factory.fromRequestId( requestId ) );
    }

    public void onSuccess( RequestId requestId, ReplyT reply ) {
        if ( reply != null ) {
            requestId2AsyncSubject.get( requestId ).onNext( reply );
        }
        requestId2AsyncSubject.get( requestId ).onCompleted();
        requestId2AsyncSubject.remove( requestId );
    }

    public void onError( ErrorMessage msg ) {
        RequestId requestId = msg.requestId;
        requestId2AsyncSubject.get( requestId ).onError( new ApplicationError( msg.error, msg.arguments, msg.argumentsKw ) );
        requestId2AsyncSubject.remove( requestId );
    }

    public interface MessageFactory {
        public WampMessage fromRequestId( RequestId requestId );
    }
}
