package ws.wamp.jawampa;

import java.net.URI;
import java.util.List;
import java.util.concurrent.Future;

import rx.Observable;
import ws.wamp.jawampa.auth.client.ClientSideAuthentication;
import ws.wamp.jawampa.roles.callee.Response;
import ws.wamp.jawampa.transport.WampClientChannelFactory;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class WampClientImpl implements WampClient {

    public WampClientImpl( URI routerUri, String realm, WampRoles[] rolesArray, boolean closeOnErrors, WampClientChannelFactory channelFactory,
            int nrReconnects, int reconnectInterval, String authId, List<ClientSideAuthentication> authMethods ) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void open() {
        // TODO Auto-generated method stub

    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

    @Override
    public Observable<Status> statusChanged() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Observable<Long> publish( String topic, Object... args ) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Observable<Long> publish( String topic, PubSubData event ) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Observable<Long> publish( String topic, ArrayNode arguments, ObjectNode argumentsKw ) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Observable<Response> registerProcedure( String topic ) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> Observable<T> makeSubscription( String topic, Class<T> eventClass ) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Observable<PubSubData> makeSubscription( String topic ) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Observable<Reply> call( String procedure, ArrayNode arguments, ObjectNode argumentsKw ) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Observable<Reply> call( String procedure, Object... args ) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> Observable<T> call( String procedure, Class<T> returnValueClass, Object... args ) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Future<Void> getTerminationFuture() {
        // TODO Auto-generated method stub
        return null;
    }

}
