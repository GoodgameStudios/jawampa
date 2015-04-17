package ws.wamp.jawampa.io;

import java.util.ArrayList;
import java.util.List;
import ws.wamp.jawampa.messages.WampMessage;

/**
 * Messages to into the bucket router and stay in the bucket router.
 * 
 * This class is meant for tests.
 * 
 * @author hkraemer@goodgamestudios.com
 */
public class ValidatingBucketRouter implements ClientRoleIOFacade {
    private final List<WampMessage> expectedMessages;
    private final List<WampMessage> receivedMessages;
    
    public ValidatingBucketRouter() {
        expectedMessages = new ArrayList<WampMessage>();
        receivedMessages = new ArrayList<WampMessage>();
    }

    @Override
    public void scheduleMessageToRouter(WampMessage receivedMessage ) {
        if ( receivedMessages.size() < expectedMessages.size() ) {
            WampMessage expectedMessage = expectedMessages.get( receivedMessages.size() );
            if ( !expectedMessage.equals( receivedMessage )) {
                // TODO: better reporting
                throw new IllegalStateException( receivedMessage.toString() + " is unexpected!" );
            }
        } else {
            throw new IllegalStateException( receivedMessage.toString() + " is unexpected!" );
        }
        
        receivedMessages.add( receivedMessage );
    }
    
    public void expect( WampMessage message ) {
        expectedMessages.add( message );
    }
    
    public void checkExpectations() {
        if ( receivedMessages.size() < expectedMessages.size() ) {
            // TODO: better reporting
            throw new IllegalStateException( "Missing messages" );
        }
    }
}
