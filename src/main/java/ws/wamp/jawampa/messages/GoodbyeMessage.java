package ws.wamp.jawampa.messages;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.WampError;
import ws.wamp.jawampa.messages.handling.MessageHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Sent by a Peer to close a previously opened WAMP session. Must be echo'ed
 * by the receiving Peer. Format: [GOODBYE, Details|dict, Reason|uri]
 */
public class GoodbyeMessage extends WampMessage {
    public static final MessageCode ID = MessageCode.GOODBYE;

    public final ObjectNode details;
    public final String reason;

    public GoodbyeMessage(ObjectNode details, String reason) {
        this.details = details;
        this.reason = reason;
    }

    public JsonNode toObjectArray(ObjectMapper mapper) throws WampError {
        ArrayNode messageNode = mapper.createArrayNode();
        messageNode.add( ID.getValue() );
        if (details != null)
            messageNode.add(details);
        else
            messageNode.add(mapper.createObjectNode());
        messageNode.add(reason.toString());
        return messageNode;
    }

    static class Factory implements WampMessageFactory {
        @Override
        public WampMessage fromObjectArray(ArrayNode messageNode)
                throws WampError {
            if (messageNode.size() != 3 || !messageNode.get(1).isObject()
                    || !messageNode.get(2).isTextual())
                throw new WampError(ApplicationError.INVALID_MESSAGE);

            ObjectNode details = (ObjectNode) messageNode.get(1);
            String reason = messageNode.get(2).asText();
            return new GoodbyeMessage(details, reason);
        }
    }

    @Override
    public void onMessage( MessageHandler messageHandler ) {
        messageHandler.onGoodbye( this );
    }
}