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
    public static final RegistrationId SOME_REGISTRATION_ID = RegistrationId.of( 42 );
    public static final String TEST_URI = "arbitrary_uri";
    public static final String SOME_REASON = "I don't know whyyyyyyyyyyyyyyy";

    private static class MockAction implements Action1<PendingRegistration> {
        public String uri;

        @Override
        public void call( PendingRegistration t1 ) {
            if ( uri != null ) throw new IllegalStateException( "MockAction.call was called twice!!!" );
            uri = t1.getUri();
        }
    }

    public static class MockRPCImplementation implements RPCImplementation {
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

    public static class MockRegistrationFailedCallback implements RegistrationFailedCallback {
        public String failedUri = null;
        public String failedReason = null;
        public RPCImplementation failedImpl = null;
        @Override
        public void registrationFailed( String uri, String reason, RPCImplementation implementation ) {
            failedUri = uri;
            failedReason = reason;
            failedImpl = implementation;
        }
    }

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

        subject.registrationComplete( SOME_REGISTRATION_ID, TEST_URI );
        ArrayNode pos = mapper.createArrayNode();
        ObjectNode kw = mapper.createObjectNode();
        subject.call( SOME_REGISTRATION_ID, request, pos, kw );

        assertEquals( request, impl.req );
        assertEquals( pos, impl.pos );
        assertEquals( kw, impl.kw );
    }

    @Test
    public void testRegistrationFailedCallbackIsCalledOnFailure() {
        MockRPCImplementation impl = new MockRPCImplementation();

        FunctionMap subject = new FunctionMap();

        MockRegistrationFailedCallback callback = new MockRegistrationFailedCallback();
        subject.register( TEST_URI, impl, callback );

        subject.registrationFailed( TEST_URI, SOME_REASON );

        assertEquals( TEST_URI, callback.failedUri );
        assertEquals( SOME_REASON, callback.failedReason );
        assertEquals( impl, callback.failedImpl );
    }

    @Test
    public void testRetryRegistrationWorks() {
        MockRPCImplementation impl = new MockRPCImplementation();

        FunctionMap subject = new FunctionMap();

        MockRegistrationFailedCallback callback = new MockRegistrationFailedCallback();
        subject.register( TEST_URI, impl, callback );
        subject.registrationFailed( TEST_URI, SOME_REASON );
        subject.register( TEST_URI, impl, callback );
    }

    @Test(expected=IllegalStateException.class)
    public void testCallingFailedTwiceThrows() {
        MockRPCImplementation impl = new MockRPCImplementation();

        FunctionMap subject = new FunctionMap();

        MockRegistrationFailedCallback callback = new MockRegistrationFailedCallback();
        subject.register( TEST_URI, impl, callback );
        subject.registrationFailed( TEST_URI, SOME_REASON );
        subject.registrationFailed( TEST_URI, SOME_REASON );
    }

    @Test(expected=IllegalStateException.class)
    public void testCallingRegistrationCompleteTwiceThrows() {
        MockRPCImplementation impl = new MockRPCImplementation();

        FunctionMap subject = new FunctionMap();

        MockRegistrationFailedCallback callback = new MockRegistrationFailedCallback();
        subject.register( TEST_URI, impl, callback );
        subject.registrationComplete( SOME_REGISTRATION_ID, TEST_URI );
        subject.registrationComplete( SOME_REGISTRATION_ID, TEST_URI );
    }
}
