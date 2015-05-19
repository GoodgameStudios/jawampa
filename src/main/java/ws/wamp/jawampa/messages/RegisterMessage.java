package ws.wamp.jawampa.messages;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.WampError;
import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.messages.handling.MessageHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * A Callees request to register an endpoint at a Dealer. [REGISTER,
 * Request|id, Options|dict, Procedure|uri]
 */
public class RegisterMessage extends WampMessage {
    public static final MessageCode ID = MessageCode.REGISTER;

    public final RequestId requestId;
    public final ObjectNode options;
    public final String procedure;

    public RegisterMessage(RequestId requestId, ObjectNode options, String procedure) {
        this.requestId = requestId;
        this.options = options;
        this.procedure = procedure;
    }

    public ArrayNode toObjectArray(ObjectMapper mapper) throws WampError {
        return new MessageNodeBuilder( mapper, ID )
                .add( requestId )
                .add( options )
                .add( procedure )
                .build();
    }

    static class Factory implements WampMessageFactory {
        @Override
        public WampMessage fromObjectArray(ArrayNode messageNode) throws WampError {
            if (messageNode.size() != 4
                    || !messageNode.get(1).canConvertToLong()
                    || !messageNode.get(2).isObject()
                    || !messageNode.get(3).isTextual())
                throw new WampError(ApplicationError.INVALID_MESSAGE);

            long requestId = messageNode.get(1).asLong();
            ObjectNode options = (ObjectNode) messageNode.get(2);
            String procedure = messageNode.get(3).asText();

            return new RegisterMessage(RequestId.of(requestId), options, procedure);
        }
    }

    @Override
    public void onMessage( MessageHandler messageHandler ) {
        messageHandler.onRegister( this );
    }
}