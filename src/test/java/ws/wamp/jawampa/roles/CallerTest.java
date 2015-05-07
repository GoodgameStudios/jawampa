package ws.wamp.jawampa.roles;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;
import org.mockito.Mock;

import rx.Observer;
import rx.subjects.AsyncSubject;
import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.Reply;
import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.messages.CallMessage;
import ws.wamp.jawampa.messages.ErrorMessage;
import ws.wamp.jawampa.messages.ResultMessage;
import ws.wamp.jawampa.messages.WampMessage;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CallerTest {
    @Mock private BaseClient baseClient;

    private String procedure = "some_procedure";
    @Mock private ArrayNode arguments;
    @Mock private ObjectNode kwArguments;
    @Mock private ArrayNode results;
    @Mock private ObjectNode kwResults;

    @Before
    public void setup() {
        initMocks( this );
    }

    @Test
    public void testCallSendsCallMessage() {
        Caller subject = new Caller( baseClient );
        AsyncSubject<Reply> resultSubject = AsyncSubject.create();

        when( baseClient.getNewRequestId() ).thenReturn( RequestId.of( 42L ) );

        subject.call( procedure, arguments, kwArguments, resultSubject );

        ArgumentMatcher<WampMessage> messageMatcher = new ArgumentMatcher<WampMessage>() {
            @Override
            public boolean matches( Object argument ) {
                CallMessage message = (CallMessage)argument;
                if ( !message.requestId.equals( RequestId.of( 42L ) ) ) return false;
                if ( message.procedure != procedure ) return false;
                if ( message.arguments != arguments ) return false;
                if ( message.argumentsKw != kwArguments ) return false;
                return true;
            }
        };
        verify( baseClient ).scheduleMessageToRouter( argThat( messageMatcher ) );
    }

    @Test
    public void testResultIsReturned() {
        Caller subject = new Caller( baseClient );
        AsyncSubject<Reply> resultSubject = AsyncSubject.create();

        when( baseClient.getNewRequestId() ).thenReturn( RequestId.of( 42L ) );

        subject.call( procedure, arguments, kwArguments, resultSubject );
        @SuppressWarnings( "unchecked" )
        Observer<Reply> observer = mock(Observer.class);
        resultSubject.subscribe( observer );

        subject.onResult( new ResultMessage( RequestId.of( 42L ),
                                             null,
                                             results,
                                             kwResults ) );

        ArgumentMatcher<Reply> replyMatcher = new ArgumentMatcher<Reply>() {
            @Override
            public boolean matches( Object argument ) {
                Reply reply = (Reply)argument;
                if ( reply.arguments() != results ) return false;
                if ( reply.keywordArguments() != kwResults ) return false;
                return true;
            }
        };
        InOrder inOrder = inOrder( observer );
        inOrder.verify( observer ).onNext( argThat( replyMatcher ) );
        inOrder.verify( observer ).onCompleted();
        verify( observer, never() ).onError( any( Throwable.class ) );
    }

    @Test
    public void testErrorIsReturned() {
        Caller subject = new Caller( baseClient );
        AsyncSubject<Reply> resultSubject = AsyncSubject.create();

        when( baseClient.getNewRequestId() ).thenReturn( RequestId.of( 42L ) );

        subject.call( procedure, arguments, kwArguments, resultSubject );
        @SuppressWarnings( "unchecked" )
        Observer<Reply> observer = mock(Observer.class);
        resultSubject.subscribe( observer );

        subject.onCallError( new ErrorMessage( CallMessage.ID,
                                               RequestId.of( 42L ),
                                               null,
                                               ApplicationError.INVALID_ARGUMENT,
                                               null,
                                               null ) );
        verify( observer, never() ).onNext( any( Reply.class ) );
        verify( observer, never() ).onCompleted();
        verify( observer ).onError( any( ApplicationError.class ) );
    }
}
