package ws.wamp.jawampa.connectionStates;

import ws.wamp.jawampa.WampClient.Status;
import ws.wamp.jawampa.WampClientImpl;

public class BaseState implements InternalConnectionState {
    protected final WampClientImpl impl;
    private final Status externalConnectionStatus;

    protected BaseState( WampClientImpl impl, Status externalConnectionStatus ) {
        this.impl = impl;
        this.externalConnectionStatus = externalConnectionStatus;
    }

    @Override
    public void open() {
        throw new IllegalStateException();
    }

    @Override
    public void close() {
        throw new IllegalStateException();
    }

    @Override
    public void connectionEstablished() {
        throw new IllegalStateException();
    }

    @Override
    public void connectionFailed() {
        throw new IllegalStateException();
    }

    @Override
    public void handshakeComplete() {
        throw new IllegalStateException();
    }

    @Override
    public void handshakeFailed() {
        throw new IllegalStateException();
    }

    @Override
    public void goodbyeReceived() {
        throw new IllegalStateException();
    }

    @Override
    public void connectionTerminated() {
        impl.setInternalConnectionState( new Disconnected( impl ) );
    }

    @Override
    public Status getExternalConnectionStatus() {
        return externalConnectionStatus;
    }
}
