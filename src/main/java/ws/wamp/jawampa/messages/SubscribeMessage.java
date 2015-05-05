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
 * Subscribe request sent by a Subscriber to a Broker to subscribe to a
 * topic. [SUBSCRIBE, Request|id, Options|dict, Topic|uri]
 */
public class SubscribeMessage extends WampMessage {
    public final static int ID = 32;
    public final RequestId requestId;
    public final ObjectNode options;
    public final String topic;

    public SubscribeMessage(RequestId requestId, ObjectNode options, String topic) {
        this.requestId = requestId;
        this.options = options;
        this.topic = topic;
    }

    public JsonNode toObjectArray(ObjectMapper mapper) throws WampError {
        ArrayNode messageNode = mapper.createArrayNode();
        messageNode.add(ID);
        messageNode.add(requestId.getValue());
        if (options != null)
            messageNode.add(options);
        else
            messageNode.add(mapper.createObjectNode());
        messageNode.add(topic.toString());
        return messageNode;
    }

    static class Factory implements WampMessageFactory {
        @Override
        public WampMessage fromObjectArray(ArrayNode messageNode) throws WampError {
            if (messageNode.size() != 4
                    || !messageNode.get(1).canConvertToLong()
                    || !messageNode.get(2).isObject()
                    || !messageNode.get(3).isTextual())
                throw new WampError(ApplicationError.INVALID_MESSAGE);

            RequestId requestId = RequestId.of( messageNode.get(1).asLong() );
            ObjectNode options = (ObjectNode) messageNode.get(2);
            String topic = messageNode.get(3).asText();

            return new SubscribeMessage(requestId, options, topic);
        }
    }

    @Override
    public void onMessage( MessageHandler messageHandler ) {
        messageHandler.onSubscribe( this );
    }
}