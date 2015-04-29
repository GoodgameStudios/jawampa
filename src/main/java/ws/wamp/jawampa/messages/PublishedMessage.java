package ws.wamp.jawampa.messages;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.WampError;
import ws.wamp.jawampa.messages.handling.MessageHandler;

import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Acknowledge sent by a Broker to a Publisher for acknowledged
 * publications. [PUBLISHED, PUBLISH.Request|id, Publication|id]
 */
public class PublishedMessage extends RequestedMessage {
    public final static int ID = 17;

    public PublishedMessage(long requestId, long publicationId) {
        super(ID, requestId, publicationId);
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

    @Override
    public void onMessage( MessageHandler messageHandler ) {
        messageHandler.onPublished( this );
    }
}