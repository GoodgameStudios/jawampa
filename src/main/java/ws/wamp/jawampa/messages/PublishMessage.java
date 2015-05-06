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
 * Sent by a Publisher to a Broker to publish an event. Possible formats:
 * [PUBLISH, Request|id, Options|dict, Topic|uri] [PUBLISH, Request|id,
 * Options|dict, Topic|uri, Arguments|list] [PUBLISH, Request|id,
 * Options|dict, Topic|uri, Arguments|list, ArgumentsKw|dict]
 */
public class PublishMessage extends WampMessage {
    public static final MessageCode ID = MessageCode.PUBLISH;

    public final RequestId requestId;
    public final ObjectNode options;
    public final String topic;
    public final ArrayNode arguments;
    public final ObjectNode argumentsKw;

    public PublishMessage(RequestId requestId, ObjectNode options, String topic,
            ArrayNode arguments, ObjectNode argumentsKw) {
        this.requestId = requestId;
        this.options = options;
        this.topic = topic;
        this.arguments = arguments;
        this.argumentsKw = argumentsKw;
    }

    public JsonNode toObjectArray(ObjectMapper mapper) throws WampError {
        ArrayNode messageNode = mapper.createArrayNode();
        messageNode.add( ID.getValue() );
        messageNode.add(requestId.getValue());
        if (options != null)
            messageNode.add(options);
        else
            messageNode.add(mapper.createObjectNode());
        messageNode.add(topic.toString());
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

            RequestId requestId = RequestId.of( messageNode.get(1).asLong() );
            ObjectNode options = (ObjectNode) messageNode.get(2);
            String topic = messageNode.get(3).asText();
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

            return new PublishMessage(requestId, options, topic, arguments,
                    argumentsKw);
        }
    }

    @Override
    public void onMessage( MessageHandler messageHandler ) {
        messageHandler.onPublish( this );
    }
}