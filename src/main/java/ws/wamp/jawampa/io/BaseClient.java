package ws.wamp.jawampa.io;

import ws.wamp.jawampa.WampClient;
import ws.wamp.jawampa.messages.WampMessage;

import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * @author hkraemer@ggs-hh.net
 */
public interface BaseClient {
    void scheduleMessageToRouter( WampMessage message );
    long getNewRequestId();
    WampClient.Status connectionState();
    void setConnectionState( WampClient.Status status );
}
