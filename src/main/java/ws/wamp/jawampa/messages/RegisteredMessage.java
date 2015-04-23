package ws.wamp.jawampa.messages;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.WampError;
import ws.wamp.jawampa.io.RequestId;
import ws.wamp.jawampa.roles.callee.RegistrationId;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Acknowledge sent by a Dealer to a Callee for successful registration.
 * [REGISTERED, REGISTER.Request|id, Registration|id]
 */
public class RegisteredMessage extends WampMessage {
    public final static int ID = 65;
    public final RequestId requestId;
    public final RegistrationId registrationId;

    public RegisteredMessage(RequestId requestId, RegistrationId registrationId) {
        this.requestId = requestId;
        this.registrationId = registrationId;
    }
    
    public JsonNode toObjectArray(ObjectMapper mapper) throws WampError {
        ArrayNode messageNode = mapper.createArrayNode();
        messageNode.add(ID);
        messageNode.add(requestId.getValue());
        return messageNode;
    }

    static class Factory implements WampMessageFactory {
        @Override
        public WampMessage fromObjectArray(ArrayNode messageNode) throws WampError {
            if (messageNode.size() != 3
                    || !messageNode.get(1).canConvertToLong()
                    || !messageNode.get(2).canConvertToLong())
                throw new WampError(ApplicationError.INVALID_MESSAGE);

            long requestId = messageNode.get(1).asLong();
            long registrationId = messageNode.get(2).asLong();

            return new RegisteredMessage(RequestId.of(requestId), RegistrationId.of(registrationId));
        }
    }
}