package ws.wamp.jawampa.messages;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.WampClient;
import ws.wamp.jawampa.WampError;
import ws.wamp.jawampa.WampRoles;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Sent by a Router to accept a Client. The WAMP session is now open.
 * Format: [WELCOME, Session|id, Details|dict]
 */
public class WelcomeMessage extends WampMessage {
    public final static int ID = 2;
    public final long sessionId;
    public final ObjectNode details;

    public WelcomeMessage(long sessionId, ObjectNode details) {
        this.sessionId = sessionId;
        this.details = details;
    }

    public JsonNode toObjectArray(ObjectMapper mapper) throws WampError {
        ArrayNode messageNode = mapper.createArrayNode();
        messageNode.add(ID);
        messageNode.add(sessionId);
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
            return new WelcomeMessage(sessionId, details);
        }
    }

    @Override
    public void onMessageBeforeWelcome( WampClient client ) {
        // Receive a welcome. Now the session is established!

        // Extract the roles of the remote side
        JsonNode roleNode = details.get("roles");
        if (roleNode == null || !roleNode.isObject()) {
            client.onProtocolError();
            return;
        }

        Set<WampRoles> rroles = new HashSet<WampRoles>();
        Iterator<String> roleKeys = roleNode.fieldNames();
        while (roleKeys.hasNext()) {
            WampRoles role = WampRoles.fromString(roleKeys.next());
            if (role != null) rroles.add(role);
        }
        WampRoles[] routerRoles = new WampRoles[rroles.size()];
        int i = 0;
        for (WampRoles r : rroles) {
            routerRoles[i] = r;
            i++;
        }

        client.onConnectionEstablished(details, sessionId, routerRoles);
    }

    @Override
    public void onMessage( WampClient client ) {
        client.onProtocolError();
    }
}