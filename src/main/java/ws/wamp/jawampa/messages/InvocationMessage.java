package ws.wamp.jawampa.messages;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.WampError;
import ws.wamp.jawampa.ids.RegistrationId;
import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.messages.handling.MessageHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Actual invocation of an endpoint sent by Dealer to a Callee. [INVOCATION,
 * Request|id, REGISTERED.Registration|id, Details|dict] [INVOCATION,
 * Request|id, REGISTERED.Registration|id, Details|dict,
 * CALL.Arguments|list] [INVOCATION, Request|id, REGISTERED.Registration|id,
 * Details|dict, CALL.Arguments|list, CALL.ArgumentsKw|dict]
 */
public class InvocationMessage extends WampMessage {
    public static final MessageCode ID = MessageCode.INVOCATION;

    public final RequestId requestId;
    public final RegistrationId registrationId;
    public final ObjectNode details;
    public final ArrayNode arguments;
    public final ObjectNode argumentsKw;

    public InvocationMessage(RequestId requestId, RegistrationId registrationId,
            ObjectNode details, ArrayNode arguments, ObjectNode argumentsKw) {
        this.requestId = requestId;
        this.registrationId = registrationId;
        this.details = details;
        this.arguments = arguments;
        this.argumentsKw = argumentsKw;
    }

    public JsonNode toObjectArray(ObjectMapper mapper) throws WampError {
        ArrayNode messageNode = mapper.createArrayNode();
        messageNode.add( ID.getValue() );
        messageNode.add(requestId.getValue());
        messageNode.add(registrationId.getValue());
        if (details != null)
            messageNode.add(details);
        else
            messageNode.add(mapper.createObjectNode());
        if (arguments != null)
            messageNode.add(arguments);
        else if (argumentsKw != null)
            messageNode.add(mapper.createArrayNode());
        if (argumentsKw != null)
            messageNode.add(argumentsKw);
        return messageNode;
    }

    static class Factory implements WampMessageFactory {
        @Override
        public WampMessage fromObjectArray(ArrayNode messageNode) throws WampError {
            if (messageNode.size() < 4 || messageNode.size() > 6
                    || !messageNode.get(1).canConvertToLong()
                    || !messageNode.get(2).canConvertToLong()
                    || !messageNode.get(3).isObject())
                throw new WampError(ApplicationError.INVALID_MESSAGE);

            long requestId = messageNode.get(1).asLong();
            long registrationId = messageNode.get(2).asLong();
            ObjectNode details = (ObjectNode) messageNode.get(3);
            ArrayNode arguments = null;
            ObjectNode argumentsKw = null;

            if (messageNode.size() >= 5) {
                if (!messageNode.get(4).isArray())
                    throw new WampError(ApplicationError.INVALID_MESSAGE);
                arguments = (ArrayNode) messageNode.get(4);
                if (messageNode.size() >= 6) {
                    if (!messageNode.get(5).isObject())
                        throw new WampError(ApplicationError.INVALID_MESSAGE);
                    argumentsKw = (ObjectNode) messageNode.get(5);
                }
            }

            return new InvocationMessage(RequestId.of( requestId ), RegistrationId.of( registrationId ),
                    details, arguments, argumentsKw);
        }
    }

    @Override
    public void onMessage( MessageHandler messageHandler ) {
        messageHandler.onInvocation( this );
    }
}