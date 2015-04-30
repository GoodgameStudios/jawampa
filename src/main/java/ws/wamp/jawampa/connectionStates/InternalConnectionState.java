package ws.wamp.jawampa.connectionStates;

import ws.wamp.jawampa.WampClient.Status;

public interface InternalConnectionState {
    public void open();
    public void close();
    public void connectionEstablished();
    public void connectionFailed();
    public void handshakeComplete();
    public void handshakeFailed();
    public void goodbyeReceived();
    public void connectionTerminated();
    public Status getExternalConnectionStatus();
}
