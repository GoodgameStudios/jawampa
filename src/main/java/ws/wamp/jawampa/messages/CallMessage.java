package ws.wamp.jawampa.messages;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.WampError;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Call as originally issued by the Caller to the Dealer. [CALL, Request|id,
 * Options|dict, Procedure|uri] [CALL, Request|id, Options|dict,
 * Procedure|uri, Arguments|list] [CALL, Request|id, Options|dict,
 * Procedure|uri, Arguments|list, ArgumentsKw|dict]
 */
public class CallMessage extends WampMessage {
    public final static int ID = 48;
    public final long requestId;
    public final ObjectNode options;
    public final String procedure;
    public final ArrayNode arguments;
    public final ObjectNode argumentsKw;

    public CallMessage(long requestId, ObjectNode options, String procedure,
            ArrayNode arguments, ObjectNode argumentsKw) {
        this.requestId = requestId;
        this.options = options;
        this.procedure = procedure;
        this.arguments = arguments;
        this.argumentsKw = argumentsKw;
    }

    public JsonNode toObjectArray(ObjectMapper mapper) throws WampError {
        ArrayNode messageNode = mapper.createArrayNode();
        messageNode.add(ID);
        messageNode.add(requestId);
        if (options != null)
            messageNode.add(options);
        else
            messageNode.add(mapper.createObjectNode());
        messageNode.add(procedure.toString());
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
                    || !messageNode.get(2).isObject()
                    || !messageNode.get(3).isTextual())
                throw new WampError(ApplicationError.INVALID_MESSAGE);

            long requestId = messageNode.get(1).asLong();
            ObjectNode options = (ObjectNode) messageNode.get(2);
            String procedure = messageNode.get(3).asText();
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

            return new CallMessage(requestId, options, procedure,
                    arguments, argumentsKw);
        }
    }
}