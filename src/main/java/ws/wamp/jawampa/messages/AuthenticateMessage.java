package ws.wamp.jawampa.messages;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.WampError;
import ws.wamp.jawampa.messages.handling.MessageHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AuthenticateMessage extends WampMessage {
    public final static int ID = 5;

    public final String signature;
    public final ObjectNode extra;

    public AuthenticateMessage( String signature, ObjectNode extra ) {
        this.signature = signature;
        this.extra = extra;
    }

    @Override
    public JsonNode toObjectArray( ObjectMapper mapper ) throws WampError {
        ArrayNode messageNode = mapper.createArrayNode();
        messageNode.add(ID);
        messageNode.add(signature);
        messageNode.add(extra);
        return messageNode;
    }

    static class Factory implements WampMessageFactory {
        @Override
        public WampMessage fromObjectArray(ArrayNode messageNode)
                throws WampError {
            if (messageNode.size() != 3 || !messageNode.get(1).isTextual()
                    || !messageNode.get(2).isObject())
                throw new WampError(ApplicationError.INVALID_MESSAGE);

            String signature = messageNode.get(1).asText();
            ObjectNode extra = (ObjectNode) messageNode.get(2);
            return new AuthenticateMessage(signature, extra);
        }
    }

    @Override
    public void onMessage( MessageHandler messageHandler ) {
        messageHandler.onAuthenticate( this );
    }
}
