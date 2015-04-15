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
 * Acknowledge sent by a Broker to a Subscriber to acknowledge
 * unsubscription. [UNSUBSCRIBED, UNSUBSCRIBE.Request|id]
 */
public class UnsubscribedMessage extends WampMessage {
    public final static int ID = 35;
    public final long requestId;

    public UnsubscribedMessage(long requestId) {
        this.requestId = requestId;
    }

    public JsonNode toObjectArray(ObjectMapper mapper) throws WampError {
        ArrayNode messageNode = mapper.createArrayNode();
        messageNode.add(ID);
        messageNode.add(requestId);
        return messageNode;
    }

    static class Factory implements WampMessageFactory {
        @Override
        public WampMessage fromObjectArray(ArrayNode messageNode) throws WampError {
            if (messageNode.size() != 2
                    || !messageNode.get(1).canConvertToLong())
                throw new WampError(ApplicationError.INVALID_MESSAGE);

            long requestId = messageNode.get(1).asLong();

            return new UnsubscribedMessage(requestId);
        }
    }

    @Override
    public void onMessage( WampClient client ) {
        RequestMapEntry requestInfo = client.requestMap.get(requestId);
        if (requestInfo == null) return; // Ignore the result
        if (requestInfo.requestType != UnsubscribeMessage.ID) {
            client.onProtocolError();
            return;
        }
        client.requestMap.remove(requestId);
        @SuppressWarnings("unchecked")
        AsyncSubject<Void> subject = (AsyncSubject<Void>)requestInfo.resultSubject;
        subject.onNext(null);
        subject.onCompleted();
    }
}