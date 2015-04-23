/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.wamp.jawampa.roles;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;
import ws.wamp.jawampa.Response;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.messages.InvocationMessage;
import ws.wamp.jawampa.messages.RegisterMessage;
import ws.wamp.jawampa.messages.WampMessage;
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
        Executor executor = Executors.newFixedThreadPool( 1 );
        
        ArgumentCaptor<WampMessage> captor = ArgumentCaptor.forClass(WampMessage.class);
        CalleeMessageHandler subject = new CalleeMessageHandler( baseClient, executor );
        Observable<Response> obs_req = subject.registerProcedure( "foo" );
        
        verify(baseClient).scheduleMessageToRouter( captor.capture() );
        RegisterMessage registration_sent = (RegisterMessage)captor.getValue();
        
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
