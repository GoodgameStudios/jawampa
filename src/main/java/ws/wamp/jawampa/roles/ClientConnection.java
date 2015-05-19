package ws.wamp.jawampa.roles;

import java.util.List;
import java.util.Set;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.WampRoles;
import ws.wamp.jawampa.auth.client.ClientSideAuthentication;
import ws.wamp.jawampa.connectionStates.HasConnectionState;
import ws.wamp.jawampa.ids.SessionId;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.messages.AbortMessage;
import ws.wamp.jawampa.messages.AuthenticateMessage;
import ws.wamp.jawampa.messages.ChallengeMessage;
import ws.wamp.jawampa.messages.GoodbyeMessage;
import ws.wamp.jawampa.messages.HelloMessage;
import ws.wamp.jawampa.messages.WelcomeMessage;
import ws.wamp.jawampa.messages.handling.BaseMessageHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ClientConnection extends BaseMessageHandler {
    private final BaseClient baseClient;
    private final String realm;
    private final Set<WampRoles> roles;
    private final String authId;
    private final List<ClientSideAuthentication> authMethods;
    private final ObjectMapper mapper;
    private final HasConnectionState stateHolder;

    private SessionId sessionId;

    public ClientConnection( BaseClient baseClient,
                             String realm,
                             Set<WampRoles> roles,
                             String authId,
                             List<ClientSideAuthentication> authMethods,
                             ObjectMapper mapper,
                             HasConnectionState stateHolder ) {
        this.baseClient = baseClient;
        this.realm = realm;
        this.roles = roles;
        this.authId = authId;
        this.authMethods = authMethods;
        this.mapper = mapper;
        this.stateHolder = stateHolder;
    }

    @Override
    public void onChallenge( ChallengeMessage msg ) {
        for ( ClientSideAuthentication authMethod : authMethods ) {
            if (authMethod.authMethod().equals( msg.authMethod )) {
                AuthenticateMessage reply = authMethod.handleChallenge( msg, mapper );
                if ( reply == null ) {
                    baseClient.onProtocolError();
                } else {
                    baseClient.scheduleMessageToRouter( reply );
                }
                return;
            }
        }
        baseClient.onProtocolError();
    }

    @Override
    public void onWelcome( WelcomeMessage msg ) {
        // FIXME: Save stuff from welcome message
        sessionId = msg.sessionId;
        stateHolder.getInternalConnectionState().handshakeComplete();
    }

    @Override
    public void onAbort( AbortMessage msg ) {
        stateHolder.getInternalConnectionState().handshakeFailed();
    }

    @Override
    public void onGoodbye( GoodbyeMessage msg ) {
        stateHolder.getInternalConnectionState().goodbyeReceived();
    }

    public void sendHello() {
        ObjectNode detailsNode = mapper.createObjectNode();
        ObjectNode rolesNode = detailsNode.putObject("roles");
        for ( WampRoles role : roles ) {
            rolesNode.putObject( role.toString() );
        }
        if (authId != null) {
            detailsNode.put( "authid", authId );
        }
        ArrayNode authMethodsNode = mapper.createArrayNode();
        for( ClientSideAuthentication authMethod : authMethods ) {
            authMethodsNode.add( authMethod.authMethod() );
        }
        detailsNode.set( "authmethods", authMethodsNode );
        baseClient.scheduleMessageToRouter( new HelloMessage( realm, detailsNode ) );
    }

    public void sendGoodbye( ) {
        baseClient.scheduleMessageToRouter( new GoodbyeMessage( null, ApplicationError.SYSTEM_SHUTDOWN ) );
    }

    public void replyToGoodbye() {
        baseClient.scheduleMessageToRouter( new GoodbyeMessage( null, ApplicationError.GOODBYE_AND_OUT ) );
    }
}
