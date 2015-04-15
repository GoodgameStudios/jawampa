package ws.wamp.jawampa.messages;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.WampClient;
import ws.wamp.jawampa.WampError;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Error reply sent by a Peer as an error response to different kinds of
 * requests. Possible formats: [ERROR, REQUEST.Type|int, REQUEST.Request|id,
 * Details|dict, Error|uri] [ERROR, REQUEST.Type|int, REQUEST.Request|id,
 * Details|dict, Error|uri, Arguments|list] [ERROR, REQUEST.Type|int,
 * REQUEST.Request|id, Details|dict, Error|uri, Arguments|list,
 * ArgumentsKw|dict]
 */
public class ErrorMessage extends WampMessage {
    public final static int ID = 8;
    public final int requestType;
    public final long requestId;
    public final ObjectNode details;
    public final String error;
    public final ArrayNode arguments;
    public final ObjectNode argumentsKw;

    public ErrorMessage(int requestType, long requestId,
            ObjectNode details, String error, ArrayNode arguments,
            ObjectNode argumentsKw) {
        this.requestType = requestType;
        this.requestId = requestId;
        this.details = details;
        this.error = error;
        this.arguments = arguments;
        this.argumentsKw = argumentsKw;
    }

    public JsonNode toObjectArray(ObjectMapper mapper) throws WampError {
        ArrayNode messageNode = mapper.createArrayNode();
        messageNode.add(ID);
        messageNode.add(requestType);
        messageNode.add(requestId);
        if (details != null)
            messageNode.add(details);
        else
            messageNode.add(mapper.createObjectNode());
        messageNode.add(error.toString());
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
            if (messageNode.size() < 5 || messageNode.size() > 7
                    || !messageNode.get(1).canConvertToInt()
                    || !messageNode.get(2).canConvertToLong()
                    || !messageNode.get(3).isObject()
                    || !messageNode.get(4).isTextual())
                throw new WampError(ApplicationError.INVALID_MESSAGE);

            int requestType = messageNode.get(1).asInt();
            long requestId = messageNode.get(2).asLong();
            ObjectNode details = (ObjectNode) messageNode.get(3);
            String error = messageNode.get(4).asText();
            ArrayNode arguments = null;
            ObjectNode argumentsKw = null;

            if (messageNode.size() >= 6) {
                if (!messageNode.get(5).isArray())
                    throw new WampError(ApplicationError.INVALID_MESSAGE);
                arguments = (ArrayNode) messageNode.get(5);
                if (messageNode.size() >= 7) {
                    if (!messageNode.get(6).isObject())
                        throw new WampError(ApplicationError.INVALID_MESSAGE);
                    argumentsKw = (ObjectNode) messageNode.get(6);
                }
            }

            return new ErrorMessage(requestType, requestId, details, error,
                    arguments, argumentsKw);
        }
    }

    @Override
    public void onMessage( WampClient client ) {
        if (requestType == CallMessage.ID
                || requestType == SubscribeMessage.ID
                || requestType == UnsubscribeMessage.ID
                || requestType == PublishMessage.ID
                || requestType == RegisterMessage.ID
                || requestType == UnregisterMessage.ID) {
            client.onErrorReply( requestId, requestType, new ApplicationError(error, arguments, argumentsKw) );
        }
    }
}