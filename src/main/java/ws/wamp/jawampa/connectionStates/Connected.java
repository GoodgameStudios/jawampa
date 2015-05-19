package ws.wamp.jawampa.connectionStates;

import ws.wamp.jawampa.WampClient.Status;
import ws.wamp.jawampa.WampClientImpl;

public class Connected extends BaseState {
    public Connected( WampClientImpl impl ) {
        super( impl, Status.CONNECTED );
    }

    @Override
    public void close() {
        impl.setInternalConnectionState( new WaitingForGoodbye( impl ) );
        impl.getClientConnection().sendGoodbye();
    }

    @Override
    public void goodbyeReceived() {
        impl.getClientConnection().replyToGoodbye();

        impl.setInternalConnectionState( new NettyDisconnecting( impl ) );
        impl.getConnection().disconnect();
    }
}
