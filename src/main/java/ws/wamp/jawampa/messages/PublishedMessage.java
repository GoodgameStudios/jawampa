package ws.wamp.jawampa.messages;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.WampError;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Acknowledge sent by a Broker to a Publisher for acknowledged
 * publications. [PUBLISHED, PUBLISH.Request|id, Publication|id]
 */
public class PublishedMessage extends WampMessage {
    public final static int ID = 17;
    public final long requestId;
    public final long publicationId;

    public PublishedMessage(long requestId, long publicationId) {
        this.requestId = requestId;
        this.publicationId = publicationId;
    }

    public JsonNode toObjectArray(ObjectMapper mapper) throws WampError {
        ArrayNode messageNode = mapper.createArrayNode();
        messageNode.add(ID);
        messageNode.add(requestId);
        messageNode.add(publicationId);
        return messageNode;
    }

    static class Factory implements WampMessageFactory {
        @Override
        public WampMessage fromObjectArray(ArrayNode messageNode) throws WampError {
            if (messageNode.size() != 3
                    || !messageNode.get(1).canConvertToLong()
                    || !messageNode.get(2).canConvertToLong())
                throw new WampError(ApplicationError.INVALID_MESSAGE);

            long requestId = messageNode.get(1).asLong();
            long publicationId = messageNode.get(2).asLong();

            return new PublishedMessage(requestId, publicationId);
        }
    }
}