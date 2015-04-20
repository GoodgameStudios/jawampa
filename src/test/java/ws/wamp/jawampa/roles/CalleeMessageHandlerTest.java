/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.wamp.jawampa.roles;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.messages.InvocationMessage;
import ws.wamp.jawampa.messages.YieldMessage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


/**
 *
 * @author hkraemer@ggs-hh.net
 */
public class CalleeMessageHandlerTest {
    private ObjectMapper mapper;

    @Mock
    private BaseClient baseClient;
    
    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        MockitoAnnotations.initMocks( this );
    }
    
    @Test
    public void testInvocation() {
        CalleeMessageHandler subject = new CalleeMessageHandler( baseClient );
        subject.registerProcedure( "foo" );
        
        long requestId = 1234567890L;
        
        long registrationId = 9876543210L;
        
        ArrayNode callPosArgs = mapper.createArrayNode();
        ObjectNode callKwArgs = mapper.createObjectNode();
        ObjectNode hopefullyIgnoredDetails = mapper.createObjectNode();
        
//        ObjectNode optionsJustPassedThrough = mapper.createObjectNode();
//        ArrayNode resultsButCalledArguments = mapper.createArrayNode();
//        ObjectNode resultsKwButCalledArgumentsKw = mapper.createObjectNode();
        
//        router.expect( new YieldMessage( requestId, optionsJustPassedThrough, resultsButCalledArguments, resultsKwButCalledArgumentsKw));
        
        
        InvocationMessage invocation = new InvocationMessage( requestId,
                                                              registrationId,
                                                              hopefullyIgnoredDetails,
                                                              callPosArgs,
                                                              callKwArgs);
        
        subject.onInvocation( invocation );
        
        verify( baseClient ).scheduleMessageToRouter( any( YieldMessage.class ) ); // FIXME: Make this more specific
        
//        router.checkExpectations();
    }
}
