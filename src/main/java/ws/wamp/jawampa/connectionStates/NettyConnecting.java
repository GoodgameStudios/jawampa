package ws.wamp.jawampa.connectionStates;

import ws.wamp.jawampa.WampClientImpl;
import ws.wamp.jawampa.WampClient.Status;

public class NettyConnecting extends BaseState {
    public NettyConnecting( WampClientImpl impl ) {
        super( impl, Status.DISCONNECTED );
    }

    @Override
    public void connectionEstablished() {
        impl.setMessageHandler( impl.getPreWelcomeMessageHandler() );
        impl.setInternalConnectionState( new WampHandshaking( impl ) );
        impl.getClientConnection().sendHello();
    }

    @Override
    public void connectionFailed() {
        impl.setInternalConnectionState( new Disconnected( impl ) );
    }
}
