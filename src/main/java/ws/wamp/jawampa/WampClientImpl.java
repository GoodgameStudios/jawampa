package ws.wamp.jawampa;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;
import rx.functions.Action1;
import rx.subjects.AsyncSubject;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import ws.wamp.jawampa.auth.client.ClientSideAuthentication;
import ws.wamp.jawampa.connectionStates.Disconnected;
import ws.wamp.jawampa.connectionStates.HasConnectionState;
import ws.wamp.jawampa.connectionStates.InternalConnectionState;
import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.ids.SessionScopeIdGenerator;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.io.NettyConnection;
import ws.wamp.jawampa.messages.WampMessage;
import ws.wamp.jawampa.messages.handling.LoggingMessageHandler;
import ws.wamp.jawampa.messages.handling.MessageHandler;
import ws.wamp.jawampa.messages.handling.WampPeerBuilder;
import ws.wamp.jawampa.registrations.Procedure;
import ws.wamp.jawampa.roles.Callee;
import ws.wamp.jawampa.roles.Caller;
import ws.wamp.jawampa.roles.ClientConnection;
import ws.wamp.jawampa.roles.Publisher;
import ws.wamp.jawampa.roles.Subscriber;
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
    private final Publisher publisher;
    private final Caller caller;
    private final Subscriber subscriber;
    private final ClientConnection clientConnection;

    private final BehaviorSubject<Status> externalStatusObservable;
    private InternalConnectionState connectionState;

    private final MessageHandler preWelcomeMessageHandler;
    private final MessageHandler postWelcomeMessageHandler;
    private MessageHandler messageHandler;

    public WampClientImpl( String realm, Set<WampRoles> roles, boolean closeOnErrors, WampClientChannelFactory channelFactory,
            int nrReconnects, int reconnectInterval, String authId, List<ClientSideAuthentication> authMethods ) {
        // TODO Auto-generated constructor stub
        connection = new NettyConnection( channelFactory, this );

        callee = roles.contains( WampRoles.Callee ) ? new Callee( this ) : null;
        publisher = roles.contains( WampRoles.Publisher ) ? new Publisher( this, mapper ) : null;
        caller = roles.contains( WampRoles.Caller ) ? new Caller( this ) : null;
        subscriber = roles.contains( WampRoles.Subscriber ) ? new Subscriber( this ) : null;
        clientConnection = new ClientConnection( this, realm, roles, authId, authMethods, mapper, this );

        preWelcomeMessageHandler = new LoggingMessageHandler(
                new WampPeerBuilder().withHandshakingClient( clientConnection )
                                     .build() );

        postWelcomeMessageHandler = new LoggingMessageHandler(
                new WampPeerBuilder().withCallee( callee )
                                     .withPublisher( publisher )
                                     .withCaller( caller )
                                     .withSubscriber( subscriber )
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
    public Observable<Void> publish( String topic, Object... args ) {
        return publish(topic, buildArgumentsArray(args), null);
    }

    @Override
    public Observable<Void> publish( final String topic, final ArrayNode arguments, final ObjectNode argumentsKw ) {
        final AsyncSubject<Void> resultSubject = AsyncSubject.create();

        connection.executor().execute( new Runnable() {
            @Override
            public void run() {
                publisher.publish( topic, arguments, argumentsKw, resultSubject );
            }
        });

        return resultSubject;
    }

    @Override
    public Procedure.Builder startRegisteringProcedure( final String procedure ) {
        return new Procedure.Builder( connection.executor(), callee, procedure );
    }

    @Override
    public Observable<PubSubData> makeSubscription( final String topic ) {
        final PublishSubject<PubSubData> resultSubject = PublishSubject.create();

        connection.executor().execute( new Runnable() {
            @Override
            public void run() {
                subscriber.subscribe( topic, resultSubject );
            }
        });

        return resultSubject;
    }

    @Override
    public Observable<Reply> call( final String procedure, final ArrayNode arguments, final ObjectNode argumentsKw ) {
        final AsyncSubject<Reply> resultSubject = AsyncSubject.create();

        connection.executor().execute( new Runnable() {
            @Override
            public void run() {
                caller.call( procedure, arguments, argumentsKw, resultSubject );
            }
        });

        return resultSubject;
    }

    @Override
    public ObjectMapper getMapper() {
        return mapper;
    }

    /* ========================= end of WampClient public interface ============================ */

    /* ========================= begin BaseClient ============================ */

    @Override
    public void scheduleMessageToRouter( final WampMessage message ) {
        connection.executor().execute( new Runnable() {
            @Override
            public void run() {
                connection.sendMessage( message );
            }
        });
    }

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

    /**
     * Builds an ArrayNode from all positional arguments in a WAMP message.<br>
     * If there are no positional arguments then null will be returned, as
     * WAMP requires no empty arguments list to be transmitted.
     * @param args All positional arguments
     * @return An ArrayNode containing positional arguments or null
     */
    private ArrayNode buildArgumentsArray(Object... args) {
        if (args.length == 0) return null;
        // Build the arguments array and serialize the arguments
        final ArrayNode argArray = mapper.createArrayNode();
        for (Object arg : args) {
            argArray.addPOJO(arg);
        }
        return argArray;
    }

    public NettyConnection getConnection() {
        return connection;
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
