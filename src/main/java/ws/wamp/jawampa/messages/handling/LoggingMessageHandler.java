package ws.wamp.jawampa.messages.handling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

public class LoggingMessageHandler implements MessageHandler {
    private static final Logger log = LoggerFactory.getLogger( LoggingMessageHandler.class );
    private final MessageHandler child;

    public LoggingMessageHandler( MessageHandler child ) {
        log.debug( "LoggingMessageHandler created! " );
        this.child = child;
    }

    @Override
    public void onHello( HelloMessage msg ) {
        log.debug( "onHello " + msg.toString() );
        child.onHello( msg );
    }

    @Override
    public void onChallenge( ChallengeMessage msg ) {
        log.debug( "onChallenge " + msg.toString() );
        child.onChallenge( msg );
    }

    @Override
    public void onAuthenticate( AuthenticateMessage msg ) {
        log.debug( "onAuthenticate " + msg.toString() );
        child.onAuthenticate( msg );
    }

    @Override
    public void onWelcome( WelcomeMessage msg ) {
        log.debug( "onWelcome " + msg.toString() );
        child.onWelcome( msg );
    }

    @Override
    public void onAbort( AbortMessage msg ) {
        log.debug( "onAbort " + msg.toString() );
        child.onAbort( msg );
    }

    @Override
    public void onGoodbye( GoodbyeMessage msg ) {
        log.debug( "onGoodbye " + msg.toString() );
        child.onGoodbye( msg );
    }

    @Override
    public void onSubscribe( SubscribeMessage msg ) {
        log.debug( "onSubscribe " + msg.toString() );
        child.onSubscribe( msg );
    }

    @Override
    public void onSubscribed( SubscribedMessage msg ) {
        log.debug( "onSubscribed " + msg.toString() );
        child.onSubscribed( msg );
    }

    @Override
    public void onSubscribeError( ErrorMessage msg ) {
        log.debug( "onSubscribeError " + msg.toString() );
        child.onSubscribeError( msg );
    }

    @Override
    public void onUnsubscribe( UnsubscribeMessage msg ) {
        log.debug( "onUnsubscribe " + msg.toString() );
        child.onUnsubscribe( msg );
    }

    @Override
    public void onUnsubscribed( UnsubscribedMessage msg ) {
        log.debug( "onUnsubscribed " + msg.toString() );
        child.onUnsubscribed( msg );
    }

    @Override
    public void onUnsubscribeError( ErrorMessage msg ) {
        log.debug( "onUnsubscribeError " + msg.toString() );
        child.onUnsubscribeError( msg );
    }

    @Override
    public void onPublish( PublishMessage msg ) {
        log.debug( "onPublish " + msg.toString() );
        child.onPublish( msg );
    }

    @Override
    public void onPublished( PublishedMessage msg ) {
        log.debug( "onPublished " + msg.toString() );
        child.onPublished( msg );
    }

    @Override
    public void onEvent( EventMessage msg ) {
        log.debug( "onEvent " + msg.toString() );
        child.onEvent( msg );
    }

    @Override
    public void onPublishError( ErrorMessage msg ) {
        log.debug( "onPublishError " + msg.toString() );
        child.onPublishError( msg );
    }

    @Override
    public void onRegister( RegisterMessage msg ) {
        log.debug( "onRegister " + msg.toString() );
        child.onRegister( msg );
    }

    @Override
    public void onRegistered( RegisteredMessage msg ) {
        log.debug( "onRegistered " + msg.toString() );
        child.onRegistered( msg );
    }

    @Override
    public void onRegisterError( ErrorMessage msg ) {
        log.debug( "onRegisterError " + msg.toString() );
        child.onRegisterError( msg );
    }

    @Override
    public void onUnregister( UnregisterMessage msg ) {
        log.debug( "onUnregister " + msg.toString() );
        child.onUnregister( msg );
    }

    @Override
    public void onUnregistered( UnregisteredMessage msg ) {
        log.debug( "onUnregistered " + msg.toString() );
        child.onUnregistered( msg );
    }

    @Override
    public void onUnregisterError( ErrorMessage msg ) {
        log.debug( "onUnregisterError " + msg.toString() );
        child.onUnregisterError( msg );
    }

    @Override
    public void onCall( CallMessage msg ) {
        log.debug( "onCall " + msg.toString() );
        child.onCall( msg );
    }

    @Override
    public void onInvocation( InvocationMessage msg ) {
        log.debug( "onInvocation " + msg.toString() );
        child.onInvocation( msg );
    }

    @Override
    public void onYield( YieldMessage msg ) {
        log.debug( "onYield " + msg.toString() );
        child.onYield( msg );
    }

    @Override
    public void onResult( ResultMessage msg ) {
        log.debug( "onResult " + msg.toString() );
        child.onResult( msg );
    }

    @Override
    public void onInvocationError( ErrorMessage msg ) {
        log.debug( "onInvocationError " + msg.toString() );
        child.onInvocationError( msg );
    }

    @Override
    public void onCallError( ErrorMessage msg ) {
        log.debug( "onCallError " + msg.toString() );
        child.onCallError( msg );
    }

    @Override
    public void onError( ErrorMessage msg ) {
        log.debug( "onError " + msg.toString() );
        child.onError( msg );
    }
}
