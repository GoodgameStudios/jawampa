package ws.wamp.jawampa.messages;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.WampError;
import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.messages.handling.MessageHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Acknowledge sent by a Dealer to a Callee for successful unregistration.
 * [UNREGISTERED, UNREGISTER.Request|id]
 */
public class UnregisteredMessage extends WampMessage {
    public final static int ID = 67;
    public final RequestId requestId;

    public UnregisteredMessage(RequestId requestId) {
        this.requestId = requestId;
    }

    public JsonNode toObjectArray(ObjectMapper mapper) throws WampError {
        ArrayNode messageNode = mapper.createArrayNode();
        messageNode.add(ID);
        messageNode.add(requestId.getValue());
        return messageNode;
    }

    static class Factory implements WampMessageFactory {
        @Override
        public WampMessage fromObjectArray(ArrayNode messageNode) throws WampError {
            if (messageNode.size() != 2
                    || !messageNode.get(1).canConvertToLong())
                throw new WampError(ApplicationError.INVALID_MESSAGE);

            long requestId = messageNode.get(1).asLong();

            return new UnregisteredMessage(RequestId.of(requestId));
        }
    }

    @Override
    public void onMessage( MessageHandler messageHandler ) {
        messageHandler.onUnregistered( this );
    }
}