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
 * Acknowledge sent by a Dealer to a Callee for successful registration.
 * [REGISTERED, REGISTER.Request|id, Registration|id]
 */
public class RegisteredMessage extends WampMessage {
    public final static int ID = 65;
    public final long requestId;
    public final long registrationId;

    public RegisteredMessage(long requestId, long registrationId) {
        this.requestId = requestId;
        this.registrationId = registrationId;
    }

    public JsonNode toObjectArray(ObjectMapper mapper) throws WampError {
        ArrayNode messageNode = mapper.createArrayNode();
        messageNode.add(ID);
        messageNode.add(requestId);
        messageNode.add(registrationId);
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
            long registrationId = messageNode.get(2).asLong();

            return new RegisteredMessage(requestId, registrationId);
        }
    }

    @Override
    public void onMessage( WampClient client ) {
        RequestMapEntry requestInfo = client.requestMap.get(requestId);
        if (requestInfo == null) return; // Ignore the result
        if (requestInfo.requestType != RegisterMessage.ID) {
            client.onProtocolError();
            return;
        }
        client.requestMap.remove(requestId);
        @SuppressWarnings("unchecked")
        AsyncSubject<Long> subject = (AsyncSubject<Long>)requestInfo.resultSubject;
        subject.onNext(registrationId);
        subject.onCompleted();
    }
}