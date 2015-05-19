package ws.wamp.jawampa.connectionStates;

import ws.wamp.jawampa.WampClient.Status;
import ws.wamp.jawampa.WampClientImpl;

public class Disconnected extends BaseState {
    public Disconnected( WampClientImpl impl ) {
        super( impl, Status.DISCONNECTED );
    }

    @Override
    public void open() {
        impl.setInternalConnectionState( new NettyConnecting( impl ) );
        impl.getConnection().connect();
    }
}
