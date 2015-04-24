package ws.wamp.jawampa.roles.callee;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static ws.wamp.jawampa.roles.callee.FunctionMapTest.*;

import org.junit.Before;
import org.junit.Test;

import rx.functions.Action1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class FunctionMapUnregistrationTest {
    private FunctionMap subject = new FunctionMap();

    private ObjectMapper mapper = new ObjectMapper();
    private MockRPCImplementation impl = new MockRPCImplementation();
    private Response request = mock(Response.class);

    private static class MockAction implements Action1<PendingUnregistration> {
        public String uri;

        @Override
        public void call( PendingUnregistration t1 ) {
            if ( uri != null ) throw new IllegalStateException( "MockAction.call was called twice!!!" );
            uri = t1.getUri();
        }
    }

    @Before
    public void setup() {
        subject.register( TEST_URI, impl );
        subject.registrationComplete( SOME_REGISTRATION_ID, TEST_URI );
    }

    @Test
    public void testNotifiesRegistrationMessageHandlerOfRegistrations() {
        MockAction action = new MockAction();

        subject.getUnregistrationsSubject().subscribe( action );
        subject.unregister( TEST_URI );

        assertEquals( TEST_URI, action.uri );
    }

    @Test
    public void testFunctionStillAvailableDuringUnregistration() {
        subject.unregister( TEST_URI );

        ArrayNode pos = mapper.createArrayNode();
        ObjectNode kw = mapper.createObjectNode();
        subject.call( SOME_REGISTRATION_ID, request, pos, kw );

        assertEquals( request, impl.req );
        assertEquals( pos, impl.pos );
        assertEquals( kw, impl.kw );
    }

    @Test(expected=IllegalStateException.class)
    public void testFunctionUnavailableAfterUnregistrationComplete() {
        subject.unregister( TEST_URI );
        subject.unregistrationComplete( SOME_REGISTRATION_ID );

        ArrayNode pos = mapper.createArrayNode();
        ObjectNode kw = mapper.createObjectNode();
        subject.call( SOME_REGISTRATION_ID, request, pos, kw );
    }
}
