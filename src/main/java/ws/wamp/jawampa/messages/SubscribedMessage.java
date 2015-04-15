package ws.wamp.jawampa.messages;

import rx.subjects.AsyncSubject;
import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.WampClient;
import ws.wamp.jawampa.WampError;
import ws.wamp.jawampa.WampClient.RequestMapEntry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Acknowledge sent by a Broker to a Subscriber to acknowledge a
 * subscription. [SUBSCRIBED, SUBSCRIBE.Request|id, Subscription|id]
 */
public class SubscribedMessage extends WampMessage {
    public final static int ID = 33;
    public final long requestId;
    public final long subscriptionId;

    public SubscribedMessage(long requestId, long subscriptionId) {
        this.requestId = requestId;
        this.subscriptionId = subscriptionId;
    }

    public JsonNode toObjectArray(ObjectMapper mapper) throws WampError {
        ArrayNode messageNode = mapper.createArrayNode();
        messageNode.add(ID);
        messageNode.add(requestId);
        messageNode.add(subscriptionId);
        return messageNode;
    }

    static class Factory implements WampMessageFactory {
        @Override
        public WampMessage fromObjectArray(ArrayNode messageNode) throws WampError {
            if (messageNode.size() != 3
                    || !messageNode.get(1).canConvertToLong()
                    || !messageNode.get(2).canConvertToLong())
                throw new WampError(ApplicationError.INVALID_MESSAGE);

            long requestId = messageNode.get(1).asLong();
            long subscriptionId = messageNode.get(2).asLong();

            return new SubscribedMessage(requestId, subscriptionId);
        }
    }

    @Override
    public void onMessage( WampClient client ) {
        RequestMapEntry requestInfo = client.requestMap.get(requestId);
        if (requestInfo == null) return; // Ignore the result
        if (requestInfo.requestType != SubscribeMessage.ID) {
            client.onProtocolError();
            return;
        }
        client.requestMap.remove(requestId);
        @SuppressWarnings("unchecked")
        AsyncSubject<Long> subject = (AsyncSubject<Long>)requestInfo.resultSubject;
        subject.onNext(subscriptionId);
        subject.onCompleted();
    }
}