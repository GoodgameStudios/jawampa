package ws.wamp.jawampa.roles;

import ws.wamp.jawampa.messages.AbortMessage;
import ws.wamp.jawampa.messages.ChallengeMessage;
import ws.wamp.jawampa.messages.GoodbyeMessage;
import ws.wamp.jawampa.messages.WelcomeMessage;
import ws.wamp.jawampa.messages.handling.BaseMessageHandler;

public class ClientConnection extends BaseMessageHandler {
    @Override
    public void onChallenge( ChallengeMessage msg ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public void onWelcome( WelcomeMessage msg ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public void onAbort( AbortMessage msg ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public void onGoodbye( GoodbyeMessage msg ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }
}
