package ws.wamp.jawampa.messages;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.WampError;
import ws.wamp.jawampa.ids.SessionId;
import ws.wamp.jawampa.messages.handling.MessageHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Sent by a Router to accept a Client. The WAMP session is now open.
 * Format: [WELCOME, Session|id, Details|dict]
 */
public class WelcomeMessage extends WampMessage {
    public static final MessageCode ID = MessageCode.WELCOME;

    public final SessionId sessionId;
    public final ObjectNode details;

    public WelcomeMessage(SessionId sessionId, ObjectNode details) {
        this.sessionId = sessionId;
        this.details = details;
    }

    public JsonNode toObjectArray(ObjectMapper mapper) throws WampError {
        ArrayNode messageNode = mapper.createArrayNode();
        messageNode.add( ID.getValue() );
        messageNode.add(sessionId.getValue());
        if (details != null)
            messageNode.add(details);
        else
            messageNode.add(mapper.createObjectNode());
        return messageNode;
    }

    static class Factory implements WampMessageFactory {
        @Override
        public WampMessage fromObjectArray(ArrayNode messageNode) throws WampError {
            if (messageNode.size() != 3
                    || !messageNode.get(1).canConvertToLong()
                    || !messageNode.get(2).isObject())
                throw new WampError(ApplicationError.INVALID_MESSAGE);

            long sessionId = messageNode.get(1).asLong();
            ObjectNode details = (ObjectNode) messageNode.get(2);
            return new WelcomeMessage(SessionId.of( sessionId ), details);
        }
    }

    @Override
    public void onMessage( MessageHandler messageHandler ) {
        messageHandler.onWelcome( this );
    }
}