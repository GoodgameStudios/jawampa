/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.wamp.jawampa.roles;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import ws.wamp.jawampa.io.ValidatingBucketRouter;
import ws.wamp.jawampa.messages.InvocationMessage;
import ws.wamp.jawampa.messages.WampMessage;
import ws.wamp.jawampa.messages.YieldMessage;

/**
 *
 * @author hkraemer@ggs-hh.net
 */
public class CalleeMessageHandlerTest {
    private ValidatingBucketRouter router;
    private ObjectMapper mapper;
    
    @Before
    public void setUp() {
        router = new ValidatingBucketRouter();
        mapper = new ObjectMapper();
    }
    
    @Test
    public void testInvocation() {
        long requestId = 1234567890L;
        
        long registrationId = 9876543210L;
        
        ArrayNode callPosArgs = mapper.createArrayNode();
        ObjectNode callKwArgs = mapper.createObjectNode();
        ObjectNode hopefullyIgnoredDetails = mapper.createObjectNode();
        
        ObjectNode optionsJustPassedThrough = mapper.createObjectNode();
        ArrayNode resultsButCalledArguments = mapper.createArrayNode();
        ObjectNode resultsKwButCalledArgumentsKw = mapper.createObjectNode();
        
        router.expect( new YieldMessage( requestId, optionsJustPassedThrough, resultsButCalledArguments, resultsKwButCalledArgumentsKw));
        
        CalleeMessageHandler subject = new CalleeMessageHandler();
        subject.addMethod( "todo" );
        
        InvocationMessage invocation = new InvocationMessage( requestId,
                                                              registrationId,
                                                              hopefullyIgnoredDetails,
                                                              callPosArgs,
                                                              callKwArgs);
        
        subject.onInvocation( invocation );
        
        router.checkExpectations();
    }
}
