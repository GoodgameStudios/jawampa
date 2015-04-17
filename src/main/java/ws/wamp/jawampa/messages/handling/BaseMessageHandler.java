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
 * This implements all methods to throw MessageNotHandledHereExceptions.
 * 
 * Use this if you want to add your MessageHandler
 * and want to avoid implementing a lot of methods, for example if you just
 * want to handle a few methods since you're just a broker.
 * 
 * @author hkraemer@ggs-hh.net
 */
public class BaseMessageHandler implements MessageHandler {

    @Override
    public void onHello(HelloMessage msg) {
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onChallenge(ChallengeMessage msg) {
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onAuthenticate(AuthenticateMessage msg) {
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onWelcome(WelcomeMessage msg) {
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onAbort(AbortMessage msg) {
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onGoodbye(GoodbyeMessage msg) {
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onSubscribe(SubscribeMessage msg) {
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onSubscribed(SubscribedMessage msg) {
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onSubscribeError(ErrorMessage msg) {
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onUnsubscribe(UnsubscribeMessage msg) {
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onUnsubscribed(UnsubscribedMessage msg) {
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onUnsubscribeError(ErrorMessage msg) {
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onPublish(PublishMessage msg) {
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onPublished(PublishedMessage msg) {
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onEvent(EventMessage msg) {
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onPublishError(ErrorMessage msg) {
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onRegister(RegisterMessage msg) {
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onRegistered(RegisteredMessage msg) {
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onRegisterError(ErrorMessage msg) {
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onUnregister(UnregisterMessage msg) {
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onUnregistered(UnregisteredMessage msg) {
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onUnregisterError(ErrorMessage msg) {
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onCall(CallMessage msg) {
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onInvocation(InvocationMessage msg) {
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onYield(YieldMessage msg) {
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onResult(ResultMessage msg) {
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onInvocationError(ErrorMessage msg) {
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onCallError(ErrorMessage msg) {
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onError(ErrorMessage msg) {
        throw new MessageNotHandledHereException();
    }
}
