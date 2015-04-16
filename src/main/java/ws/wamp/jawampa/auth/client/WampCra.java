package ws.wamp.jawampa.auth.client;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import ws.wamp.jawampa.messages.AuthenticateMessage;
import ws.wamp.jawampa.messages.ChallengeMessage;
import ws.wamp.jawampa.messages.WampMessage;

import com.fasterxml.jackson.databind.ObjectMapper;

public class WampCra implements ClientSideAuthentication {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(WampMessage.class);

    private final String secret;

    public WampCra(String key) {
        this.secret = key;
    }

    public String authMethod() {
        return "wampcra";
    }

    private String calculateHmacSHA256(String challenge, ObjectMapper objectMapper) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(challenge.getBytes());
        return objectMapper.convertValue( rawHmac, String.class );
    }

    @Override
    public AuthenticateMessage handleChallenge( ChallengeMessage message, ObjectMapper objectMapper ) {
        String challenge = message.extra.get( "challenge" ).asText();
        try {
            String signature = calculateHmacSHA256( challenge, objectMapper );
            return new AuthenticateMessage(signature, objectMapper.createObjectNode() );
        } catch( InvalidKeyException e ) {
            logger.warn( "InvalidKeyException while calculating HMAC" );
            return null;
        } catch( NoSuchAlgorithmException e ) {
            logger.warn( "NoSuchAlgorithmException while calculating HMAC" );
            return null;
        }
    }
}
