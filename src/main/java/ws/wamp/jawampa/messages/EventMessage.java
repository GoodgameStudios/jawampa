package ws.wamp.jawampa.messages;

import rx.Subscriber;
import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.PubSubData;
import ws.wamp.jawampa.WampClient;
import ws.wamp.jawampa.WampClient.PubSubState;
import ws.wamp.jawampa.WampError;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Event dispatched by Broker to Subscribers for subscription the event was
 * matching. [EVENT, SUBSCRIBED.Subscription|id, PUBLISHED.Publication|id,
 * Details|dict] [EVENT, SUBSCRIBED.Subscription|id,
 * PUBLISHED.Publication|id, Details|dict, PUBLISH.Arguments|list] [EVENT,
 * SUBSCRIBED.Subscription|id, PUBLISHED.Publication|id, Details|dict,
 * PUBLISH.Arguments|list, PUBLISH.ArgumentsKw|dict]
 */
public class EventMessage extends WampMessage {
    public final static int ID = 36;
    public final long subscriptionId;
    public final long publicationId;
    public final ObjectNode details;
    public final ArrayNode arguments;
    public final ObjectNode argumentsKw;

    public EventMessage(long subscriptionId, long publicationId,
            ObjectNode details, ArrayNode arguments, ObjectNode argumentsKw) {
        this.subscriptionId = subscriptionId;
        this.publicationId = publicationId;
        this.details = details;
        this.arguments = arguments;
        this.argumentsKw = argumentsKw;
    }

    public JsonNode toObjectArray(ObjectMapper mapper) throws WampError {
        ArrayNode messageNode = mapper.createArrayNode();
        messageNode.add(ID);
        messageNode.add(subscriptionId);
        messageNode.add(publicationId);
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

            long subscriptionId = messageNode.get(1).asLong();
            long publicationId = messageNode.get(2).asLong();
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

            return new EventMessage(subscriptionId, publicationId, details,
                    arguments, argumentsKw);
        }
    }

    @Override
    public void onMessage( WampClient client ) {
        WampClient.SubscriptionMapEntry entry = client.subscriptionsBySubscriptionId.get(subscriptionId);
        if (entry == null || entry.state != PubSubState.Subscribed) return; // Ignore the result
        PubSubData evResult = new PubSubData(arguments, argumentsKw);
        // publish the event
        for (Subscriber<? super PubSubData> s : entry.subscribers) {
            s.onNext(evResult);
        }
    }
}