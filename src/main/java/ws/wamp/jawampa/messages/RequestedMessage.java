package ws.wamp.jawampa.messages;

import ws.wamp.jawampa.WampClient;
import ws.wamp.jawampa.WampError;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public abstract class RequestedMessage extends WampMessage {
    public final long ID;
    public final long requestId;
    public final long newResourceId; // is -1 in Un*Message

    public RequestedMessage(long ID, long requestId, long newResourceId) {
        this.ID = ID;
        this.requestId = requestId;
        this.newResourceId = newResourceId;
    }

    public JsonNode toObjectArray(ObjectMapper mapper) throws WampError {
        ArrayNode messageNode = mapper.createArrayNode();
        messageNode.add(ID);
        messageNode.add(requestId);
        if (newResourceId != -1) messageNode.add(newResourceId);
        return messageNode;
    }

    @Override
    public void onMessage( WampClient client ) {
        if (newResourceId == -1) {
            client.onSuccessfulReply( requestId, ID, null );
        } else {
            client.onSuccessfulReply( requestId, ID, newResourceId );
        }
    }
}
