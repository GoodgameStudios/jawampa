package ws.wamp.jawampa.messages;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.WampError;

import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Acknowledge sent by a Dealer to a Callee for successful unregistration.
 * [UNREGISTERED, UNREGISTER.Request|id]
 */
public class UnregisteredMessage extends RegisteredMessage {
    public final static int ID = 67;

    public UnregisteredMessage(long requestId) {
        super(requestId, -1);
    }

    static class Factory implements WampMessageFactory {
        @Override
        public WampMessage fromObjectArray(ArrayNode messageNode) throws WampError {
            if (messageNode.size() != 2
                    || !messageNode.get(1).canConvertToLong())
                throw new WampError(ApplicationError.INVALID_MESSAGE);

            long requestId = messageNode.get(1).asLong();

            return new UnregisteredMessage(requestId);
        }
    }
}