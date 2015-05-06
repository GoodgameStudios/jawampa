package ws.wamp.jawampa.messages;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.WampError;
import ws.wamp.jawampa.messages.handling.MessageHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Base class for all messages
 */
public abstract class WampMessage {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(WampMessage.class);

    public abstract JsonNode toObjectArray(ObjectMapper mapper)
            throws WampError;

    public static WampMessage fromObjectArray(ArrayNode messageNode)
            throws WampError {
        if (messageNode == null || messageNode.size() < 1
                || !messageNode.get(0).canConvertToInt())
            throw new WampError(ApplicationError.INVALID_MESSAGE);

        int messageType = messageNode.get(0).asInt();
        WampMessageFactory factory = messageFactories.get(messageType);
        if (factory == null)
            return null; // We can't find the message type, so we skip it

        return factory.fromObjectArray(messageNode);
    }

    public abstract void onMessage( MessageHandler messageHandler );

    // Register all possible message types

    /**
     * A map which associates all message types which factories which can
     * recreate them from received data.
     */
    final static Map<MessageCode, WampMessageFactory> messageFactories;
    static {
        HashMap<MessageCode, WampMessageFactory> map = new HashMap<MessageCode, WampMessageFactory>();
        map.put(HelloMessage.ID, new HelloMessage.Factory());
        map.put(WelcomeMessage.ID, new WelcomeMessage.Factory());
        map.put(AbortMessage.ID, new AbortMessage.Factory());
        map.put(ChallengeMessage.ID, new ChallengeMessage.Factory());
        map.put(AuthenticateMessage.ID, new AuthenticateMessage.Factory());
        map.put(GoodbyeMessage.ID, new GoodbyeMessage.Factory());
        // map.put(MessageType.ID, new HeartbeatMessage.Factory());
        map.put(ErrorMessage.ID, new ErrorMessage.Factory());
        map.put(PublishMessage.ID, new PublishMessage.Factory());
        map.put(PublishedMessage.ID, new PublishedMessage.Factory());
        map.put(SubscribeMessage.ID, new SubscribeMessage.Factory());
        map.put(SubscribedMessage.ID, new SubscribedMessage.Factory());
        map.put(UnsubscribeMessage.ID, new UnsubscribeMessage.Factory());
        map.put(UnsubscribedMessage.ID, new UnsubscribedMessage.Factory());
        map.put(EventMessage.ID, new EventMessage.Factory());
        map.put(CallMessage.ID, new CallMessage.Factory());
        // map.put(CancelMessage.ID, new CancelMessage.Factory());
        map.put(ResultMessage.ID, new ResultMessage.Factory());
        map.put(RegisterMessage.ID, new RegisterMessage.Factory());
        map.put(RegisteredMessage.ID, new RegisteredMessage.Factory());
        map.put(UnregisterMessage.ID, new UnregisterMessage.Factory());
        map.put(UnregisteredMessage.ID, new UnregisteredMessage.Factory());
        map.put(InvocationMessage.ID, new InvocationMessage.Factory());
        // map.put(InterruptMessage.ID, new InterruptMessage.Factory());
        map.put(YieldMessage.ID, new YieldMessage.Factory());
        messageFactories = Collections.unmodifiableMap(map);
    }
}