package ws.wamp.jawampa.auth.client;

import ws.wamp.jawampa.messages.AuthenticateMessage;
import ws.wamp.jawampa.messages.ChallengeMessage;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface ClientSideAuthentication {
    String authMethod();
    AuthenticateMessage handleChallenge( ChallengeMessage message, ObjectMapper objectMapper );
}
