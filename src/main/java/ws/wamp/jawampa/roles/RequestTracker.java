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
    private final boolean completeAsyncOnSuccess;
    private final Map<RequestId, PublishSubject<ReplyT>> requestId2PublishSubject = new HashMap<RequestId, PublishSubject<ReplyT>>();

    public static class Builder<ReplyT> {
        private final BaseClient baseClient;
        private boolean completeAsyncOnSuccess;
        private boolean completeAsyncOnSuccessSet = false;

        public Builder( BaseClient baseClient ) {
            this.baseClient = baseClient;
        }

        public Builder<ReplyT> completeAsyncOnSuccess() {
            this.completeAsyncOnSuccess = true;
            this.completeAsyncOnSuccessSet = true;
            return this;
        }

        public Builder<ReplyT> dontCompleteAsyncOnSuccess() {
            this.completeAsyncOnSuccess = false;
            this.completeAsyncOnSuccessSet = true;
            return this;
        }

        public RequestTracker<ReplyT> build() {
            if ( !completeAsyncOnSuccessSet ) {
                throw new IllegalStateException( "You have to call either completeAsyncOnSuccess() or dontCompleteAsyncOnSuccess()" );
            }

            return new RequestTracker<ReplyT>( baseClient, completeAsyncOnSuccess );
        }
    }

    private RequestTracker( BaseClient baseClient, boolean completeAsyncOnSuccess ) {
        this.baseClient = baseClient;
        this.completeAsyncOnSuccess = completeAsyncOnSuccess;
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
        if ( completeAsyncOnSuccess ) {
            requestId2PublishSubject.get( requestId ).onCompleted();
        }
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
