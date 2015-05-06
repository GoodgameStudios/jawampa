package ws.wamp.jawampa.messages;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.WampError;
import ws.wamp.jawampa.messages.handling.MessageHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Sent by a Peer to abort the opening of a WAMP session. No response is
 * expected. [ABORT, Details|dict, Reason|uri]
 */
public class AbortMessage extends WampMessage {
    public final static int ID = 3;
    public final ObjectNode details;
    public final String reason;

    public AbortMessage(ObjectNode details, String reason) {
        this.details = details;
        this.reason = reason;
    }

    public JsonNode toObjectArray(ObjectMapper mapper) throws WampError {
        ArrayNode messageNode = mapper.createArrayNode();
        messageNode.add(ID);
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
            return new AbortMessage(details, reason);
        }
    }

    @Override
    public void onMessage( MessageHandler messageHandler ) {
        messageHandler.onAbort( this );
    }
}