package ws.wamp.jawampa.connectionStates;

import ws.wamp.jawampa.WampClientImpl;
import ws.wamp.jawampa.WampClient.Status;

public class NettyDisconnecting extends BaseState {
    public NettyDisconnecting( WampClientImpl impl ) {
        super( impl, Status.CONNECTED );
    }
}
