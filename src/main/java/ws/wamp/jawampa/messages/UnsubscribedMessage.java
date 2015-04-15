package ws.wamp.jawampa.messages;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.WampError;

import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Acknowledge sent by a Broker to a Subscriber to acknowledge
 * unsubscription. [UNSUBSCRIBED, UNSUBSCRIBE.Request|id]
 */
public class UnsubscribedMessage extends RegisteredMessage {
    public final static int ID = 35;

    public UnsubscribedMessage(long requestId) {
        super(ID, requestId);
    }

    static class Factory implements WampMessageFactory {
        @Override
        public WampMessage fromObjectArray(ArrayNode messageNode) throws WampError {
            if (messageNode.size() != 2
                    || !messageNode.get(1).canConvertToLong())
                throw new WampError(ApplicationError.INVALID_MESSAGE);

            long requestId = messageNode.get(1).asLong();

            return new UnsubscribedMessage(requestId);
        }
    }
}