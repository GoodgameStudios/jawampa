package ws.wamp.jawampa.messages;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.WampError;
import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.messages.handling.MessageHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Result of a call as returned by Dealer to Caller. [RESULT,
 * CALL.Request|id, Details|dict] [RESULT, CALL.Request|id, Details|dict,
 * YIELD.Arguments|list] [RESULT, CALL.Request|id, Details|dict,
 * YIELD.Arguments|list, YIELD.ArgumentsKw|dict]
 */
public class ResultMessage extends WampMessage {
    public static final MessageCode ID = MessageCode.RESULT;

    public final RequestId requestId;
    public final ObjectNode details;
    public final ArrayNode arguments;
    public final ObjectNode argumentsKw;

    public ResultMessage(RequestId requestId, ObjectNode details,
            ArrayNode arguments, ObjectNode argumentsKw) {
        this.requestId = requestId;
        this.details = details;
        this.arguments = arguments;
        this.argumentsKw = argumentsKw;
    }

    public JsonNode toObjectArray(ObjectMapper mapper) throws WampError {
        ArrayNode messageNode = mapper.createArrayNode();
        messageNode.add( ID.getValue() );
        messageNode.add(requestId.getValue());
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
            if (messageNode.size() < 3 || messageNode.size() > 5
                    || !messageNode.get(1).canConvertToLong()
                    || !messageNode.get(2).isObject())
                throw new WampError(ApplicationError.INVALID_MESSAGE);

            long requestId = messageNode.get(1).asLong();
            ObjectNode details = (ObjectNode) messageNode.get(2);
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

            return new ResultMessage(RequestId.of( requestId ), details, arguments,
                    argumentsKw);
        }
    }

    @Override
    public void onMessage( MessageHandler messageHandler ) {
        messageHandler.onResult( this );
    }
}