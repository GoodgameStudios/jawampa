package ws.wamp.jawampa.roles.callee;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.ids.RegistrationId;
import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.messages.ErrorMessage;
import ws.wamp.jawampa.messages.InvocationMessage;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class InvocationMessageHandlerTest {
    @Test
    public void testOnInvocationCallsFunctionMapCall() {
        FunctionMap map = mock(FunctionMap.class);

        InvocationMessageHandler subject = new InvocationMessageHandler( null, map );

        when( map.isRegistered( RegistrationId.of( 23 ) ) ).thenReturn( true );

        subject.onInvocation( new InvocationMessage( RequestId.of( 42L ), RegistrationId.of( 23L ), null, null, null ) );

        verify( map ).call( eq( RegistrationId.of( 23 ) ),
                            any( Response.class ),
                            (ArrayNode)isNull(),
                            (ObjectNode)isNull() );
    }

    @Test
    public void testOnInvocationOfUnregisteredMessageReturnError() {
        FunctionMap map = mock(FunctionMap.class);
        BaseClient baseClient = mock(BaseClient.class);

        InvocationMessageHandler subject = new InvocationMessageHandler( baseClient, map );

        subject.onInvocation( new InvocationMessage( RequestId.of( 42L ), RegistrationId.of( 23L ), null, null, null ) );

        when( map.isRegistered( RegistrationId.of( 23 ) ) ).thenReturn( false );
        verify( baseClient ).scheduleMessageToRouter( new ErrorMessage( InvocationMessage.ID,
                                                                        RequestId.of( 42L ),
                                                                        null,
                                                                        ApplicationError.NO_SUCH_PROCEDURE,
                                                                        null,
                                                                        null ) );
    }
}
