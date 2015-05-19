package ws.wamp.jawampa.messages;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.WampError;
import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.ids.SubscriptionId;
import ws.wamp.jawampa.messages.handling.MessageHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Unsubscribe request sent by a Subscriber to a Broker to unsubscribe a
 * subscription. [UNSUBSCRIBE, Request|id, SUBSCRIBED.Subscription|id]
 */
public class UnsubscribeMessage extends WampMessage {
    public static final MessageCode ID = MessageCode.UNSUBSCRIBE;

    public final RequestId requestId;
    public final SubscriptionId subscriptionId;

    public UnsubscribeMessage(RequestId requestId, SubscriptionId subsriptionId) {
        this.requestId = requestId;
        this.subscriptionId = subsriptionId;
    }

    public ArrayNode toObjectArray(ObjectMapper mapper) throws WampError {
        return new MessageNodeBuilder( mapper, ID )
                .add( requestId )
                .add( subscriptionId )
                .build();
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

            return new UnsubscribeMessage(requestId, subscriptionId);
        }
    }

    @Override
    public void onMessage( MessageHandler messageHandler ) {
        messageHandler.onUnsubscribe( this );
    }
}