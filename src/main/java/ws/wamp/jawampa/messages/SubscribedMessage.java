package ws.wamp.jawampa.messages;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.WampError;
import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.ids.SubscriptionId;
import ws.wamp.jawampa.messages.handling.MessageHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Acknowledge sent by a Broker to a Subscriber to acknowledge a
 * subscription. [SUBSCRIBED, SUBSCRIBE.Request|id, Subscription|id]
 */
public class SubscribedMessage extends WampMessage {
    public static final MessageCode ID = MessageCode.SUBSCRIBED;

    public final RequestId requestId;
    public final SubscriptionId subscriptionId;

    public SubscribedMessage(RequestId requestId, SubscriptionId subscriptionId) {
        this.requestId = requestId;
        this.subscriptionId = subscriptionId;
    }

    public JsonNode toObjectArray(ObjectMapper mapper) throws WampError {
        ArrayNode messageNode = mapper.createArrayNode();
        messageNode.add( ID.getValue() );
        messageNode.add(requestId.getValue());
        messageNode.add(subscriptionId.getValue());
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
            SubscriptionId subscriptionId = SubscriptionId.of( messageNode.get(2).asLong() );

            return new SubscribedMessage(requestId, subscriptionId);
        }
    }

    @Override
    public void onMessage( MessageHandler messageHandler ) {
        messageHandler.onSubscribed( this );
    }
}