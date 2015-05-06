package ws.wamp.jawampa.roles;

import rx.subjects.PublishSubject;
import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.messages.ErrorMessage;
import ws.wamp.jawampa.messages.PublishMessage;
import ws.wamp.jawampa.messages.PublishedMessage;
import ws.wamp.jawampa.messages.WampMessage;
import ws.wamp.jawampa.messages.handling.BaseMessageHandler;
import ws.wamp.jawampa.roles.RequestTracker.MessageFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Publisher extends BaseMessageHandler {
    private final ObjectMapper mapper;
    private final RequestTracker<Void> requestTracker;

    public Publisher( BaseClient baseClient, ObjectMapper mapper ) {
        this.mapper = mapper;
        this.requestTracker = new RequestTracker.Builder<Void>( baseClient )
                                                .completeAsyncOnSuccess()
                                                .build();
    }

    public void publish( final String topic, final ArrayNode arguments, final ObjectNode argumentsKw, PublishSubject<Void> resultSubject ) {
        requestTracker.sendRequest( resultSubject, new MessageFactory() {
            @Override
            public WampMessage fromRequestId( RequestId requestId ) {
                return new PublishMessage( requestId,
                                           mapper.createObjectNode().put( "acknowledge", true ),
                                           topic,
                                           arguments,
                                           argumentsKw );
            }
        });
    }

    @Override
    public void onPublished( PublishedMessage msg ) {
        requestTracker.onSuccess( msg.requestId, null );
    }

    @Override
    public void onPublishError( ErrorMessage msg ) {
        requestTracker.onError( msg );
    }
}
