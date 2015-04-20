package ws.wamp.jawampa.io;

import com.fasterxml.jackson.databind.node.ArrayNode;

import rx.Observable;
import rx.subjects.AsyncSubject;
import ws.wamp.jawampa.WampClient;
import ws.wamp.jawampa.messages.WampMessage;

/**
 * @author hkraemer@ggs-hh.net
 */
public interface BaseClient {
    void scheduleMessageToRouter( WampMessage message );
    long getNewRequestId();
    void scheduleAsync( Runnable runnable );
    WampClient.Status connectionState();
    void setConnectionState( WampClient.Status status );
    <T> Observable<T> observeOnScheduler( AsyncSubject<T> subject );
    ArrayNode buildArgumentsArray(Object... args);
}
