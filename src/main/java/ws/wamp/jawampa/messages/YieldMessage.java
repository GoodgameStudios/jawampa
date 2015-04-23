package ws.wamp.jawampa.messages;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.WampError;
import ws.wamp.jawampa.io.RequestId;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Actual yield from an endpoint send by a Callee to Dealer. [YIELD,
 * INVOCATION.Request|id, Options|dict] [YIELD, INVOCATION.Request|id,
 * Options|dict, Arguments|list] [YIELD, INVOCATION.Request|id,
 * Options|dict, Arguments|list, ArgumentsKw|dict]
 */
public class YieldMessage extends WampMessage {
    public final static int ID = 70;
    public final RequestId requestId;
    public final ObjectNode options;
    public final ArrayNode arguments;
    public final ObjectNode argumentsKw;

    public YieldMessage(RequestId requestId, ObjectNode options,
            ArrayNode arguments, ObjectNode argumentsKw) {
        this.requestId = requestId;
        this.options = options;
        this.arguments = arguments;
        this.argumentsKw = argumentsKw;
    }

    public JsonNode toObjectArray(ObjectMapper mapper) throws WampError {
        ArrayNode messageNode = mapper.createArrayNode();
        messageNode.add(ID);
        messageNode.add(requestId.getValue());
        if (options != null)
            messageNode.add(options);
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
            if (messageNode.size() < 3 || messageNode.size() > 5
                    || !messageNode.get(1).canConvertToLong()
                    || !messageNode.get(2).isObject())
                throw new WampError(ApplicationError.INVALID_MESSAGE);

            long requestId = messageNode.get(1).asLong();
            ObjectNode options = (ObjectNode) messageNode.get(2);
            ArrayNode arguments = null;
            ObjectNode argumentsKw = null;

            if (messageNode.size() >= 4) {
                if (!messageNode.get(3).isArray())
                    throw new WampError(ApplicationError.INVALID_MESSAGE);
                arguments = (ArrayNode) messageNode.get(3);
                if (messageNode.size() >= 5) {
                    if (!messageNode.get(4).isObject())
                        throw new WampError(ApplicationError.INVALID_MESSAGE);
                    argumentsKw = (ObjectNode) messageNode.get(4);
                }
            }

            return new YieldMessage(RequestId.of( requestId ), options, arguments,
                    argumentsKw);
        }
    }
}