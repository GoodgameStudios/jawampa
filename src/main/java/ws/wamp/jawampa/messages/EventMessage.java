package ws.wamp.jawampa.messages;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.WampError;
import ws.wamp.jawampa.ids.PublicationId;
import ws.wamp.jawampa.ids.SubscriptionId;
import ws.wamp.jawampa.messages.handling.MessageHandler;

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
    public static final MessageCode ID = MessageCode.EVENT;

    public final SubscriptionId subscriptionId;
    public final PublicationId publicationId;
    public final ObjectNode details;
    public final ArrayNode arguments;
    public final ObjectNode argumentsKw;

    public EventMessage(SubscriptionId subscriptionId, PublicationId publicationId,
            ObjectNode details, ArrayNode arguments, ObjectNode argumentsKw) {
        this.subscriptionId = subscriptionId;
        this.publicationId = publicationId;
        this.details = details;
        this.arguments = arguments;
        this.argumentsKw = argumentsKw;
    }

    public JsonNode toObjectArray(ObjectMapper mapper) throws WampError {
        ArrayNode messageNode = mapper.createArrayNode();
        messageNode.add( ID.getValue() );
        messageNode.add(subscriptionId.getValue());
        messageNode.add(publicationId.getValue());
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

            SubscriptionId subscriptionId = SubscriptionId.of( messageNode.get(1).asLong() );
            PublicationId publicationId = PublicationId.of( messageNode.get(2).asLong() );
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
    public void onMessage( MessageHandler messageHandler ) {
        messageHandler.onEvent( this );
    }
}