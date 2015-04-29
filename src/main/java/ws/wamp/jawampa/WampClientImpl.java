package ws.wamp.jawampa;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;
import rx.functions.Action1;
import ws.wamp.jawampa.auth.client.ClientSideAuthentication;
import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.ids.SessionScopeIdGenerator;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.io.NettyConnection;
import ws.wamp.jawampa.messages.WampMessage;
import ws.wamp.jawampa.messages.handling.MessageHandler;
import ws.wamp.jawampa.messages.handling.WampPeerBuilder;
import ws.wamp.jawampa.roles.Callee;
import ws.wamp.jawampa.roles.ClientConnection;
import ws.wamp.jawampa.roles.callee.RPCImplementation;
import ws.wamp.jawampa.transport.WampClientChannelFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/*
 * TODO:
 * - missing roles
 * - reconnect
 */

public class WampClientImpl implements WampClient, BaseClient {
    private static final Logger log = LoggerFactory.getLogger( WampClientImpl.class );
    private final NettyConnection connection;
    private final MessageHandler messageHandler;
    private final ObjectMapper mapper = new ObjectMapper();
    private final SessionScopeIdGenerator sessionScopeIdGenerator = new SessionScopeIdGenerator();

    private final Callee callee;
    private final ClientConnection clientConnection;

    public WampClientImpl( URI routerUri, String realm, Set<WampRoles> roles, boolean closeOnErrors, WampClientChannelFactory channelFactory,
            int nrReconnects, int reconnectInterval, String authId, List<ClientSideAuthentication> authMethods ) {
        // TODO Auto-generated constructor stub
        connection = new NettyConnection( channelFactory );

        callee = new Callee( this );
        clientConnection = new ClientConnection( this, realm, roles, authId, authMethods, mapper );

        messageHandler = new WampPeerBuilder().withCallee( callee )
                                              .withHandshakingClient( clientConnection )
                                              .build();
        connection.getStatusObservable().subscribe( new Action1<Status>() {
            @Override
            public void call( Status status ) {
                if ( status == Status.SESSION_ESTABLISHING ) {
                    clientConnection.sendHello();
                }
            }
        });
        connection.getMessageObservable().subscribe( new Action1<WampMessage>() {
            @Override
            public void call( WampMessage message ) {
                message.onMessage( messageHandler );
            }
        });
    }

    @Override
    public void open() {
        connection.connect();
    }

    @Override
    public void close() {
        connection.disconnect();
    }

    @Override
    public Observable<Status> statusChanged() {
        return connection.getStatusObservable();
    }

    @Override
    public Observable<Long> publish( String topic, Object... args ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<Long> publish( String topic, PubSubData event ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<Long> publish( String topic, ArrayNode arguments, ObjectNode argumentsKw ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public void registerProcedure( String topic, RPCImplementation implementation ) {
        callee.register( topic, implementation );
    }

    @Override
    public <T> Observable<T> makeSubscription( String topic, Class<T> eventClass ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<PubSubData> makeSubscription( String topic ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<Reply> call( String procedure, ArrayNode arguments, ObjectNode argumentsKw ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<Reply> call( String procedure, Object... args ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Observable<T> call( String procedure, Class<T> returnValueClass, Object... args ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<Void> getTerminationFuture() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public void scheduleMessageToRouter( WampMessage message ) {
        connection.sendMessage( message );
    }

    @Override
    public RequestId getNewRequestId() {
        return RequestId.of( sessionScopeIdGenerator.nextId() );
    }

    @Override
    public Status connectionState() {
        return connection.getStatusObservable().getValue();
    }
}
