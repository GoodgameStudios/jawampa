package ws.wamp.jawampa.messages;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.WampError;
import ws.wamp.jawampa.messages.handling.MessageHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Sent by a Client to initiate opening of a WAMP session to a Router
 * attaching to a Realm. Format: [HELLO, Realm|uri, Details|dict]
 */
public class HelloMessage extends WampMessage {
    public final static int ID = 1;
    public final String realm;
    public final ObjectNode details;

    public HelloMessage(String realm, ObjectNode details) {
        this.realm = realm;
        this.details = details;
    }

    public JsonNode toObjectArray(ObjectMapper mapper) throws WampError {
        ArrayNode messageNode = mapper.createArrayNode();
        messageNode.add(ID);
        messageNode.add(realm.toString());
        if (details != null)
            messageNode.add(details);
        else
            messageNode.add(mapper.createObjectNode());
        return messageNode;
    }

    static class Factory implements WampMessageFactory {
        @Override
        public WampMessage fromObjectArray(ArrayNode messageNode) throws WampError {
            if (messageNode.size() != 3 || !messageNode.get(1).isTextual()
                    || !messageNode.get(2).isObject())
                throw new WampError(ApplicationError.INVALID_MESSAGE);

            String realm = messageNode.get(1).asText();
            ObjectNode details = (ObjectNode) messageNode.get(2);
            return new HelloMessage(realm, details);
        }
    }

    @Override
    public void onMessage( MessageHandler messageHandler ) {
        messageHandler.onHello( this );
    }
}