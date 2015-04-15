package ws.wamp.jawampa.messages;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.WampError;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * A Callees request to unregister a previsouly established registration.
 * [UNREGISTER, Request|id, REGISTERED.Registration|id]
 * 
 */
public class UnregisterMessage extends WampMessage {
    public final static int ID = 66;
    public final long requestId;
    public final long registrationId;

    public UnregisterMessage(long requestId, long registrationId) {
        this.requestId = requestId;
        this.registrationId = registrationId;
    }

    public JsonNode toObjectArray(ObjectMapper mapper) throws WampError {
        ArrayNode messageNode = mapper.createArrayNode();
        messageNode.add(ID);
        messageNode.add(requestId);
        messageNode.add(registrationId);
        return messageNode;
    }

    static class Factory implements WampMessageFactory {
        @Override
        public WampMessage fromObjectArray(ArrayNode messageNode)
                throws WampError {
            if (messageNode.size() != 3
                    || !messageNode.get(1).canConvertToLong()
                    || !messageNode.get(2).canConvertToLong())
                throw new WampError(ApplicationError.INVALID_MESSAGE);

            long requestId = messageNode.get(1).asLong();
            long registrationId = messageNode.get(2).asLong();

            return new UnregisterMessage(requestId, registrationId);
        }
    }
}