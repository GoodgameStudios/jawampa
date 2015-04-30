package ws.wamp.jawampa;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;
import rx.functions.Action1;
import rx.subjects.BehaviorSubject;
import ws.wamp.jawampa.WampClient.Status;
import ws.wamp.jawampa.auth.client.ClientSideAuthentication;
import ws.wamp.jawampa.connectionStates.InternalConnectionState;
import ws.wamp.jawampa.connectionStates.Disconnected;
import ws.wamp.jawampa.connectionStates.HasConnectionState;
import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.ids.SessionScopeIdGenerator;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.io.NettyConnection;
import ws.wamp.jawampa.messages.WampMessage;
import ws.wamp.jawampa.messages.handling.LoggingMessageHandler;
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
 * - check connection state ( messages during session establishment vs messages with established session )
 */

public class WampClientImpl implements WampClient, BaseClient, HasConnectionState {
    private static final Logger log = LoggerFactory.getLogger( WampClientImpl.class );
    private final NettyConnection connection;
    private final ObjectMapper mapper = new ObjectMapper();
    private final SessionScopeIdGenerator sessionScopeIdGenerator = new SessionScopeIdGenerator();

    private final Callee callee;
    private final ClientConnection clientConnection;

    private final BehaviorSubject<Status> externalStatusObservable;
    private InternalConnectionState connectionState;

    private final MessageHandler preWelcomeMessageHandler;
    private final MessageHandler postWelcomeMessageHandler;
    private MessageHandler messageHandler;

    public WampClientImpl( URI routerUri, String realm, Set<WampRoles> roles, boolean closeOnErrors, WampClientChannelFactory channelFactory,
            int nrReconnects, int reconnectInterval, String authId, List<ClientSideAuthentication> authMethods ) {
        // TODO Auto-generated constructor stub
        connection = new NettyConnection( channelFactory, this );

        callee = new Callee( this );
        clientConnection = new ClientConnection( this, realm, roles, authId, authMethods, mapper, this );

        preWelcomeMessageHandler = new LoggingMessageHandler(
                new WampPeerBuilder().withHandshakingClient( clientConnection )
                                     .build() );

        postWelcomeMessageHandler = new LoggingMessageHandler(
                new WampPeerBuilder().withCallee( callee )
                                     .build() );
        connectionState = new Disconnected( this );
        externalStatusObservable = BehaviorSubject.create ( connectionState.getExternalConnectionStatus() );

        connection.getMessageObservable().subscribe( new Action1<WampMessage>() {
            @Override
            public void call( WampMessage message ) {
                message.onMessage( messageHandler );
            }
        });
    }

    @Override
    public void open() {
        connectionState.open();
    }

    @Override
    public void close() {
        connection.executor().execute( new Runnable() {
            @Override
            public void run() {
                connectionState.close();
            }
        });
    }

    @Override
    public Observable<Status> statusChanged() {
        return externalStatusObservable;
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
    public void registerProcedure( final String topic, final RPCImplementation implementation ) {
        connection.executor().execute( new Runnable() {
            @Override
            public void run() {
                callee.register( topic, implementation );
            }
        });
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
    public void scheduleMessageToRouter( final WampMessage message ) {
        connection.executor().execute( new Runnable() {
            @Override
            public void run() {
                connection.sendMessage( message );
            }
        });
    }

    /* ========================= end of WampClient public interface ============================ */

    /* ========================= begin BaseClient ============================ */

    @Override
    public RequestId getNewRequestId() {
        return RequestId.of( sessionScopeIdGenerator.nextId() );
    }

    @Override
    public Status connectionState() {
        return externalStatusObservable.getValue();
    }

    @Override
    public void onProtocolError() {
        connection.disconnect();
    }

    /* ========================= end of BaseClient ============================ */

    public NettyConnection getConnection() {
        return connection;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    @Override
    public InternalConnectionState getInternalConnectionState() {
        return connectionState;
    }

    public void setInternalConnectionState( InternalConnectionState connectionState ) {
        log.debug( "New connection state: " + connectionState.getClass().getSimpleName() );
        this.connectionState = connectionState;

        if ( externalStatusObservable.getValue() != connectionState.getExternalConnectionStatus() ) {
            externalStatusObservable.onNext( connectionState.getExternalConnectionStatus() );
        }
    }

    public ClientConnection getClientConnection() {
        return clientConnection;
    }

    public MessageHandler getPreWelcomeMessageHandler() {
        return preWelcomeMessageHandler;
    }

    public MessageHandler getPostWelcomeMessageHandler() {
        return postWelcomeMessageHandler;
    }

    public void setMessageHandler( MessageHandler messageHandler ) {
        this.messageHandler = messageHandler;
    }
}
