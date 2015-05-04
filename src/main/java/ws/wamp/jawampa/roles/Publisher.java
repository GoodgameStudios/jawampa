package ws.wamp.jawampa.roles;

import rx.Observable;
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

    public Publisher( BaseClient baseClient, ObjectMapper mapper ) {
        this.baseClient = baseClient;
        this.mapper = mapper;
    }

    public Observable<Void> publish( final String topic, final ArrayNode arguments, final ObjectNode argumentsKw ) {
        baseClient.scheduleMessageToRouter( new PublishMessage( baseClient.getNewRequestId(),
                                                                mapper.createObjectNode().put( "acknowledge", true ),
                                                                topic,
                                                                arguments,
                                                                argumentsKw ) );
        return null;
    }

    @Override
    public void onPublished( PublishedMessage msg ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onPublishError( ErrorMessage msg ) {
        throw new UnsupportedOperationException();
    }
}
