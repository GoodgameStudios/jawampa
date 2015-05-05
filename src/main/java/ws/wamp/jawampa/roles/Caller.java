package ws.wamp.jawampa.roles;

import java.util.HashMap;
import java.util.Map;

import rx.subjects.AsyncSubject;
import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.Reply;
import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.messages.CallMessage;
import ws.wamp.jawampa.messages.ErrorMessage;
import ws.wamp.jawampa.messages.ResultMessage;
import ws.wamp.jawampa.messages.handling.BaseMessageHandler;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Caller extends BaseMessageHandler {
    private final BaseClient baseClient;

    private final Map<RequestId, AsyncSubject<Reply>> requestId2AsyncSubject = new HashMap<RequestId, AsyncSubject<Reply>>();

    public Caller( BaseClient baseClient ) {
        this.baseClient = baseClient;
    }

    public void call( String procedure, ArrayNode arguments, ObjectNode kwArguments, AsyncSubject<Reply> resultSubject ) {
        RequestId requestId = baseClient.getNewRequestId();
        requestId2AsyncSubject.put( requestId, resultSubject );

        baseClient.scheduleMessageToRouter( new CallMessage( requestId,
                                                             null,
                                                             procedure,
                                                             arguments,
                                                             kwArguments ) );
    }

    @Override
    public void onResult( ResultMessage msg ) {
        RequestId requestId = msg.requestId;
        requestId2AsyncSubject.get( requestId ).onNext( new Reply( msg.arguments, msg.argumentsKw ) );
        requestId2AsyncSubject.get( requestId ).onCompleted();
        requestId2AsyncSubject.remove( requestId );
    }

    @Override
    public void onCallError( ErrorMessage msg ) {
        RequestId requestId = msg.requestId;
        requestId2AsyncSubject.get( requestId ).onError( new ApplicationError( msg.error, msg.arguments, msg.argumentsKw ) );
        requestId2AsyncSubject.remove( requestId );
    }
}
