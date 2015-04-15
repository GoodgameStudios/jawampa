package ws.wamp.jawampa.messages;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.WampError;

import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Acknowledge sent by a Dealer to a Callee for successful registration.
 * [REGISTERED, REGISTER.Request|id, Registration|id]
 */
public class RegisteredMessage extends RequestedMessage{
    public final static int ID = 65;

    public RegisteredMessage(long requestId, long registrationId) {
        super(ID, requestId, registrationId);
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

            return new RegisteredMessage(requestId, registrationId);
        }
    }
}