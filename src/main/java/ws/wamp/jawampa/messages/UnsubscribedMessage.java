package ws.wamp.jawampa.messages;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.WampError;
import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.messages.handling.MessageHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Acknowledge sent by a Broker to a Subscriber to acknowledge
 * unsubscription. [UNSUBSCRIBED, UNSUBSCRIBE.Request|id]
 */
public class UnsubscribedMessage extends WampMessage {
    public static final MessageCode ID = MessageCode.UNSUBSCRIBED;

    public final RequestId requestId;

    public UnsubscribedMessage(RequestId requestId) {
        this.requestId = requestId;
    }

    public JsonNode toObjectArray(ObjectMapper mapper) throws WampError {
        ArrayNode messageNode = mapper.createArrayNode();
        messageNode.add( ID.getValue() );
        messageNode.add(requestId.getValue());
        return messageNode;
    }

    static class Factory implements WampMessageFactory {
        @Override
        public WampMessage fromObjectArray(ArrayNode messageNode) throws WampError {
            if (messageNode.size() != 2
                    || !messageNode.get(1).canConvertToLong())
                throw new WampError(ApplicationError.INVALID_MESSAGE);

            RequestId requestId = RequestId.of( messageNode.get(1).asLong() );

            return new UnsubscribedMessage(requestId);
        }
    }

    @Override
    public void onMessage( MessageHandler messageHandler ) {
        messageHandler.onUnsubscribed( this );
    }
}