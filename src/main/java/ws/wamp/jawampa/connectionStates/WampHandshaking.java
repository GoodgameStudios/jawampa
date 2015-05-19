package ws.wamp.jawampa.connectionStates;

import ws.wamp.jawampa.WampClient.Status;
import ws.wamp.jawampa.WampClientImpl;

public class WampHandshaking extends BaseState {
    public WampHandshaking( WampClientImpl impl ) {
        super( impl, Status.DISCONNECTED );
    }

    @Override
    public void handshakeComplete() {
        impl.setMessageHandler( impl.getPostWelcomeMessageHandler() );
        impl.setInternalConnectionState( new Connected( impl ) );
    }

    @Override
    public void handshakeFailed() {
        // TODO Auto-generated method stub
        super.handshakeFailed();
    }
}
