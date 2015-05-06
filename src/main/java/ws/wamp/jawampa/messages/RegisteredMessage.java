package ws.wamp.jawampa.messages;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.WampError;
import ws.wamp.jawampa.ids.RegistrationId;
import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.messages.handling.MessageHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Acknowledge sent by a Dealer to a Callee for successful registration.
 * [REGISTERED, REGISTER.Request|id, Registration|id]
 */
public class RegisteredMessage extends WampMessage {
    public static final MessageCode ID = MessageCode.REGISTERED;

    public final RequestId requestId;
    public final RegistrationId registrationId;

    public RegisteredMessage(RequestId requestId, RegistrationId registrationId) {
        this.requestId = requestId;
        this.registrationId = registrationId;
    }
    
    public JsonNode toObjectArray(ObjectMapper mapper) throws WampError {
        ArrayNode messageNode = mapper.createArrayNode();
        messageNode.add( ID.getValue() );
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

    @Override
    public void onMessage( MessageHandler messageHandler ) {
        messageHandler.onRegistered( this );
    }
}