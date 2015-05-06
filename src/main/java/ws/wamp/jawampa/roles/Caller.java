package ws.wamp.jawampa.roles;

import rx.subjects.PublishSubject;
import ws.wamp.jawampa.Reply;
import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.messages.CallMessage;
import ws.wamp.jawampa.messages.ErrorMessage;
import ws.wamp.jawampa.messages.ResultMessage;
import ws.wamp.jawampa.messages.WampMessage;
import ws.wamp.jawampa.messages.handling.BaseMessageHandler;
import ws.wamp.jawampa.roles.RequestTracker.MessageFactory;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Caller extends BaseMessageHandler {
    private final RequestTracker<Reply> requestTracker;

    public Caller( BaseClient baseClient ) {
        this.requestTracker = new RequestTracker.Builder<Reply>( baseClient )
                                                .completeAsyncOnSuccess()
                                                .build();
    }

    public void call( final String procedure, final ArrayNode arguments, final ObjectNode kwArguments, PublishSubject<Reply> resultSubject ) {
        requestTracker.sendRequest( resultSubject, new MessageFactory() {
            @Override
            public WampMessage fromRequestId( RequestId requestId ) {
                return new CallMessage( requestId,
                                        null,
                                        procedure,
                                        arguments,
                                        kwArguments );
            }
        });
    }

    @Override
    public void onResult( ResultMessage msg ) {
        requestTracker.onSuccess( msg.requestId, new Reply( msg.arguments, msg.argumentsKw ) );
    }

    @Override
    public void onCallError( ErrorMessage msg ) {
        requestTracker.onError( msg );
    }
}
