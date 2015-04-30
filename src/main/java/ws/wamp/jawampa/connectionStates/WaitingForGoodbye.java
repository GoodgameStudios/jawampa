package ws.wamp.jawampa.connectionStates;

import ws.wamp.jawampa.WampClientImpl;
import ws.wamp.jawampa.WampClient.Status;

public class WaitingForGoodbye extends BaseState {
    public WaitingForGoodbye( WampClientImpl impl ) {
        super( impl, Status.CONNECTED );
    }

    @Override
    public void goodbyeReceived() {
        impl.setInternalConnectionState( new NettyDisconnecting( impl ) );
        impl.getConnection().disconnect();
    }
}
