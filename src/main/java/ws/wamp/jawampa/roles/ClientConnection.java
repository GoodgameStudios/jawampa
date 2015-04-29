package ws.wamp.jawampa.roles;

import java.util.List;
import java.util.Set;

import ws.wamp.jawampa.WampRoles;
import ws.wamp.jawampa.auth.client.ClientSideAuthentication;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.messages.AbortMessage;
import ws.wamp.jawampa.messages.ChallengeMessage;
import ws.wamp.jawampa.messages.GoodbyeMessage;
import ws.wamp.jawampa.messages.HelloMessage;
import ws.wamp.jawampa.messages.WelcomeMessage;
import ws.wamp.jawampa.messages.handling.BaseMessageHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ClientConnection extends BaseMessageHandler {
    private final BaseClient baseClient;
    private final String realm;
    private final Set<WampRoles> roles;
    private final String authId;
    private final List<ClientSideAuthentication> authMethods;
    private final ObjectMapper mapper;

    public ClientConnection( BaseClient baseClient,
                             String realm,
                             Set<WampRoles> roles,
                             String authId,
                             List<ClientSideAuthentication> authMethods,
                             ObjectMapper mapper ) {
        this.baseClient = baseClient;
        this.realm = realm;
        this.roles = roles;
        this.authId = authId;
        this.authMethods = authMethods;
        this.mapper = mapper;
    }

    @Override
    public void onChallenge( ChallengeMessage msg ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public void onWelcome( WelcomeMessage msg ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public void onAbort( AbortMessage msg ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public void onGoodbye( GoodbyeMessage msg ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void sendHello() {
        ObjectNode detailsNode = mapper.createObjectNode();
        ObjectNode rolesNode = detailsNode.putObject("roles");
        for ( WampRoles role : roles ) {
            ObjectNode roleNode = rolesNode.putObject( role.toString() );
        }
        baseClient.scheduleMessageToRouter( new HelloMessage( realm, detailsNode ) );
    }
}
