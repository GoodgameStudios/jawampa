package ws.wamp.jawampa.messages;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.WampError;
import ws.wamp.jawampa.ids.PublicationId;
import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.messages.handling.MessageHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Acknowledge sent by a Broker to a Publisher for acknowledged
 * publications. [PUBLISHED, PUBLISH.Request|id, Publication|id]
 */
public class PublishedMessage extends WampMessage {
    public static final MessageCode ID = MessageCode.PUBLISHED;

    public final RequestId requestId;
    public final PublicationId publicationId;

    public PublishedMessage(RequestId requestId, PublicationId publicationId) {
        this.requestId = requestId;
        this.publicationId = publicationId;
    }

    public JsonNode toObjectArray(ObjectMapper mapper) throws WampError {
        ArrayNode messageNode = mapper.createArrayNode();
        messageNode.add( ID.getValue() );
        messageNode.add(requestId.getValue());
        messageNode.add(publicationId.getValue());
        return messageNode;
    }

    static class Factory implements WampMessageFactory {
        @Override
        public WampMessage fromObjectArray(ArrayNode messageNode) throws WampError {
            if (messageNode.size() != 3
                    || !messageNode.get(1).canConvertToLong()
                    || !messageNode.get(2).canConvertToLong())
                throw new WampError(ApplicationError.INVALID_MESSAGE);

            RequestId requestId = RequestId.of( messageNode.get(1).asLong() );
            PublicationId publicationId = PublicationId.of( messageNode.get(2).asLong() );

            return new PublishedMessage(requestId, publicationId);
        }
    }

    @Override
    public void onMessage( MessageHandler messageHandler ) {
        messageHandler.onPublished( this );
    }
}