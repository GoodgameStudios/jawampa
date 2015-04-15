package ws.wamp.jawampa.messages;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.WampError;

import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Acknowledge sent by a Broker to a Subscriber to acknowledge a
 * subscription. [SUBSCRIBED, SUBSCRIBE.Request|id, Subscription|id]
 */
public class SubscribedMessage extends RequestedMessage {
    public final static int ID = 33;

    public SubscribedMessage(long requestId, long subscriptionId) {
        super(ID, requestId, subscriptionId);
    }

    static class Factory implements WampMessageFactory {
        @Override
        public WampMessage fromObjectArray(ArrayNode messageNode) throws WampError {
            if (messageNode.size() != 3
                    || !messageNode.get(1).canConvertToLong()
                    || !messageNode.get(2).canConvertToLong())
                throw new WampError(ApplicationError.INVALID_MESSAGE);

            long requestId = messageNode.get(1).asLong();
            long subscriptionId = messageNode.get(2).asLong();

            return new SubscribedMessage(requestId, subscriptionId);
        }
    }
}