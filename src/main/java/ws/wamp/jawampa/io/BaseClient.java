package ws.wamp.jawampa.io;

import ws.wamp.jawampa.WampClient;
import ws.wamp.jawampa.messages.WampMessage;

/**
 * @author hkraemer@ggs-hh.net
 */
public interface BaseClient {
    void scheduleMessageToRouter( WampMessage message );
    RequestId getNewRequestId();
    WampClient.Status connectionState();
    void setConnectionState( WampClient.Status status );
}
