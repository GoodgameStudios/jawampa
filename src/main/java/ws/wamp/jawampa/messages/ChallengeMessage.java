package ws.wamp.jawampa.messages;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.WampError;
import ws.wamp.jawampa.messages.handling.MessageHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ChallengeMessage extends WampMessage {
    public final static int ID = 4;

    public final String authMethod;
    public final ObjectNode extra;

    public ChallengeMessage( String authMethod, ObjectNode extra ) {
        this.authMethod = authMethod;
        this.extra = extra;
    }

    @Override
    public JsonNode toObjectArray( ObjectMapper mapper ) throws WampError {
        ArrayNode messageNode = mapper.createArrayNode();
        messageNode.add(ID);
        messageNode.add(authMethod);
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

            String authMethod = messageNode.get(1).asText();
            ObjectNode extra = (ObjectNode) messageNode.get(2);
            return new ChallengeMessage(authMethod, extra);
        }
    }

    @Override
    public void onMessage( MessageHandler messageHandler ) {
        messageHandler.onChallenge( this );
    }
}
