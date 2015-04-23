package ws.wamp.jawampa.roles.callee;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import rx.functions.Action1;
import ws.wamp.jawampa.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class FunctionMapTest {
    private class MockAction implements Action1<PendingRegistration> {
        public String uri;

        @Override
        public void call( PendingRegistration t1 ) {
            if ( uri != null ) throw new IllegalStateException( "MockAction.call was called twice!!!" );
            uri = t1.getUri();
        }
    }

    private class MockRPCImplementation implements RPCImplementation {
        public Response req;
        public ArrayNode pos;
        public ObjectNode kw;

        @Override
        public void call( Response req, ArrayNode positionalArguments, ObjectNode keywordArguments ) {
            if ( this.req != null ) throw new IllegalStateException( "MockRPCImplementation.call was called twice!!!" );
            this.req = req;
            this.pos = positionalArguments;
            this.kw = keywordArguments;
        }
    }

    private static final String TEST_URI = "arbitrary_uri";

    @Test
    public void testNotifiesRegistrationMessageHandlerOfRegistrations() {
        MockAction action = new MockAction();

        FunctionMap subject = new FunctionMap();
        subject.getRegistrationsSubject().subscribe( action );
        subject.register( TEST_URI, null );

        assertEquals( TEST_URI, action.uri );
    }

    @Test
    public void testFunctionIsAvailableAfterRegistrationCompleteWasCalled() {
        ObjectMapper mapper = new ObjectMapper();
        MockRPCImplementation impl = new MockRPCImplementation();
        Response request = mock(Response.class);

        FunctionMap subject = new FunctionMap();

        subject.register( TEST_URI, impl );

        subject.registrationComplete( RegistrationId.of( 42 ), TEST_URI );
        ArrayNode pos = mapper.createArrayNode();
        ObjectNode kw = mapper.createObjectNode();
        subject.call( RegistrationId.of( 42 ), request, pos, kw );

        assertEquals( request, impl.req );
        assertEquals( pos, impl.pos );
        assertEquals( kw, impl.kw );
    }

    @Test
    public void testRegistrationFailedCallbackIsCalledOnFailure() {
        ObjectMapper mapper = new ObjectMapper();
        MockRPCImplementation impl = new MockRPCImplementation();
        Response request = mock(Response.class);

        FunctionMap subject = new FunctionMap();

        subject.register( TEST_URI, impl );

        subject.registrationFailed( RegistrationId.of( 42 ), TEST_URI );
    }
}
