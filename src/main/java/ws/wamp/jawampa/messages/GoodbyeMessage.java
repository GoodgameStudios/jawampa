package ws.wamp.jawampa.messages;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.WampClient;
import ws.wamp.jawampa.WampError;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Sent by a Peer to close a previously opened WAMP session. Must be echo'ed
 * by the receiving Peer. Format: [GOODBYE, Details|dict, Reason|uri]
 */
public class GoodbyeMessage extends WampMessage {
    public final static int ID = 6;
    public final ObjectNode details;
    public final String reason;

    public GoodbyeMessage(ObjectNode details, String reason) {
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
            return new GoodbyeMessage(details, reason);
        }
    }

    @Override
    public void onMessage( WampClient client ) {
        // Reply the goodbye
        client.scheduleMessage(new GoodbyeMessage(null, ApplicationError.GOODBYE_AND_OUT));
        // We could also use the reason from the msg, but this would be harder
        // to determinate from a "real" error
        client.onSessionError(new ApplicationError(ApplicationError.GOODBYE_AND_OUT));
    }
}