package ws.wamp.jawampa.messages.handling;

import ws.wamp.jawampa.messages.AbortMessage;
import ws.wamp.jawampa.messages.AuthenticateMessage;
import ws.wamp.jawampa.messages.CallMessage;
import ws.wamp.jawampa.messages.ChallengeMessage;
import ws.wamp.jawampa.messages.ErrorMessage;
import ws.wamp.jawampa.messages.EventMessage;
import ws.wamp.jawampa.messages.GoodbyeMessage;
import ws.wamp.jawampa.messages.HelloMessage;
import ws.wamp.jawampa.messages.InvocationMessage;
import ws.wamp.jawampa.messages.PublishMessage;
import ws.wamp.jawampa.messages.PublishedMessage;
import ws.wamp.jawampa.messages.RegisterMessage;
import ws.wamp.jawampa.messages.RegisteredMessage;
import ws.wamp.jawampa.messages.ResultMessage;
import ws.wamp.jawampa.messages.SubscribeMessage;
import ws.wamp.jawampa.messages.SubscribedMessage;
import ws.wamp.jawampa.messages.UnregisterMessage;
import ws.wamp.jawampa.messages.UnregisteredMessage;
import ws.wamp.jawampa.messages.UnsubscribeMessage;
import ws.wamp.jawampa.messages.UnsubscribedMessage;
import ws.wamp.jawampa.messages.WelcomeMessage;
import ws.wamp.jawampa.messages.YieldMessage;

/**
 *
 * @author hkraemer
 */
public class WampPeer implements MessageHandler {
    private final MessageHandler broker;
    private final MessageHandler dealer;
    
    private final MessageHandler caller;
    private final MessageHandler callee;
    
    private final MessageHandler subscriber;
    private final MessageHandler publisher;
    
    private final MessageHandler handshakingClient;
    private final MessageHandler handshakingRouter;
    
    WampPeer( MessageHandler broker, MessageHandler dealer,
                     MessageHandler caller, MessageHandler callee,
                     MessageHandler subscriber, MessageHandler publisher,
                     MessageHandler handshakingClient, MessageHandler handshakingRouter) {
        this.broker = broker;
        this.dealer = dealer;
        this.caller = caller;
        this.callee = callee;
        this.subscriber = subscriber;
        this.publisher = publisher;
        this.handshakingClient = handshakingClient;
        this.handshakingRouter = handshakingRouter;
    }
    
    private MessageHandler nonNull( MessageHandler source ) {
        if ( source == null ) {
            throw new MessageNotHandledHereException();
        } else {
            return source;
        }
    }
    @Override
    public void onHello(HelloMessage msg) {
        nonNull( handshakingRouter ).onHello( msg );
    }

    @Override
    public void onChallenge(ChallengeMessage msg) {
        nonNull( handshakingClient ).onChallenge( msg );
    }

    @Override
    public void onAuthenticate(AuthenticateMessage msg) {
        nonNull( handshakingRouter ).onAuthenticate( msg );
    }

    @Override
    public void onWelcome(WelcomeMessage msg) {
        nonNull( handshakingClient ).onWelcome( msg );
    }

    @Override
    public void onAbort(AbortMessage msg) {
        nonNull( handshakingClient ).onAbort( msg );
    }

    @Override
    public void onGoodbye(GoodbyeMessage msg) {
        // usually only one of these should be non-null, so this
        // does the right thing. In other cases... that's a good
        // question
        if ( handshakingClient != null ) handshakingClient.onGoodbye( msg );
        if ( handshakingRouter != null ) handshakingRouter.onGoodbye( msg );
    }

    @Override
    public void onSubscribe(SubscribeMessage msg) {
        nonNull( broker ).onSubscribe( msg );
    }

    @Override
    public void onSubscribed(SubscribedMessage msg) {
        nonNull( subscriber ).onSubscribed( msg );
    }

    @Override
    public void onSubscribeError(ErrorMessage msg) {
        nonNull( subscriber ).onSubscribeError( msg );
    }

    @Override
    public void onUnsubscribe(UnsubscribeMessage msg) {
        nonNull( broker ).onUnsubscribe( msg );
    }

    @Override
    public void onUnsubscribed(UnsubscribedMessage msg) {
        nonNull( subscriber ).onUnsubscribed( msg );
    }

    @Override
    public void onUnsubscribeError(ErrorMessage msg) {
        nonNull( subscriber ).onUnsubscribeError( msg );
    }

    @Override
    public void onPublish(PublishMessage msg) {
        nonNull( broker ).onPublish( msg );
    }

    @Override
    public void onPublished(PublishedMessage msg) {
        nonNull( publisher ).onPublished( msg );
    }

    @Override
    public void onEvent(EventMessage msg) {
        nonNull( subscriber ).onEvent( msg );
    }

    @Override
    public void onPublishError(ErrorMessage msg) {
        nonNull( publisher ).onPublishError( msg );
    }

    @Override
    public void onRegister(RegisterMessage msg) {
        nonNull( dealer ).onRegister( msg );
    }

    @Override
    public void onRegistered(RegisteredMessage msg) {
        nonNull( callee ).onRegistered( msg );
    }

    @Override
    public void onRegisterError(ErrorMessage msg) {
        nonNull( callee ).onRegisterError( msg );
    }

    @Override
    public void onUnregister(UnregisterMessage msg) {
        nonNull( dealer ).onUnregister( msg );
    }

    @Override
    public void onUnregistered(UnregisteredMessage msg) {
        nonNull( callee ).onUnregistered( msg );
    }

    @Override
    public void onUnregisterError(ErrorMessage msg) {
        nonNull( callee ).onUnregisterError( msg );
    }

    @Override
    public void onCall(CallMessage msg) {
        nonNull( dealer ).onCall( msg );
    }

    @Override
    public void onInvocation(InvocationMessage msg) {
        nonNull( callee ).onInvocation( msg );
    }

    @Override
    public void onYield(YieldMessage msg) {
        nonNull( dealer ).onYield( msg );
    }

    @Override
    public void onResult(ResultMessage msg) {
        nonNull( caller ).onResult( msg );
    }

    @Override
    public void onInvocationError(ErrorMessage msg) {
        nonNull( dealer ).onInvocationError( msg );
    }

    @Override
    public void onCallError(ErrorMessage msg) {
        nonNull( caller ).onCallError( msg );
    }

    @Override
    public void onError(ErrorMessage msg) {
        if ( broker != null ) broker.onError( msg );
        if ( dealer != null ) dealer.onError( msg );
        if ( callee != null ) callee.onError( msg );
        if ( subscriber != null ) subscriber.onError( msg );
        if ( publisher != null ) publisher.onError( msg );
        if ( handshakingClient != null ) handshakingClient.onError( msg );
        if ( handshakingRouter != null ) handshakingRouter.onError( msg );
    }
}
