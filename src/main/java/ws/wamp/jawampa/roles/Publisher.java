package ws.wamp.jawampa.roles;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.subjects.AsyncSubject;
import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.messages.ErrorMessage;
import ws.wamp.jawampa.messages.PublishMessage;
import ws.wamp.jawampa.messages.PublishedMessage;
import ws.wamp.jawampa.messages.handling.BaseMessageHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Publisher extends BaseMessageHandler {
    private final BaseClient baseClient;
    private final ObjectMapper mapper;

    private final Map<RequestId, AsyncSubject<Void>> requestId2AsyncSubject = new HashMap<RequestId, AsyncSubject<Void>>();

    public Publisher( BaseClient baseClient, ObjectMapper mapper ) {
        this.baseClient = baseClient;
        this.mapper = mapper;
    }

    public void publish( final String topic, final ArrayNode arguments, final ObjectNode argumentsKw, AsyncSubject<Void> resultSubject ) {
        RequestId requestId = baseClient.getNewRequestId();
        requestId2AsyncSubject.put( requestId, resultSubject );

        baseClient.scheduleMessageToRouter( new PublishMessage( requestId,
                                                                mapper.createObjectNode().put( "acknowledge", true ),
                                                                topic,
                                                                arguments,
                                                                argumentsKw ) );
    }

    @Override
    public void onPublished( PublishedMessage msg ) {
        RequestId requestId = msg.requestId;
        requestId2AsyncSubject.get( requestId ).onCompleted();
        requestId2AsyncSubject.remove( requestId );
    }

    @Override
    public void onPublishError( ErrorMessage msg ) {
        RequestId requestId = msg.requestId;
        requestId2AsyncSubject.get( requestId ).onError( new ApplicationError( msg.error, msg.arguments, msg.argumentsKw ) );
        requestId2AsyncSubject.remove( requestId );
    }
}
