package ws.wamp.jawampa.roles;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;

import rx.Observer;
import rx.subjects.AsyncSubject;
import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.ids.PublicationId;
import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.messages.ErrorMessage;
import ws.wamp.jawampa.messages.PublishMessage;
import ws.wamp.jawampa.messages.PublishedMessage;
import ws.wamp.jawampa.messages.WampMessage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PublisherTest {
    private final ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    @Mock private BaseClient baseClient;

    private String topic = "some_topic";
    @Mock private ArrayNode arguments;
    @Mock private ObjectNode kwArguments;

    private Publisher subject;
    private AsyncSubject<Void> resultSubject;

    @Before
    public void setup() {
        initMocks( this );

        subject = new Publisher( baseClient, mapper );
        resultSubject = AsyncSubject.create();

        when( baseClient.getNewRequestId() ).thenReturn( RequestId.of( 42L ) )
                                            .thenThrow( new IllegalStateException( "No more request ids for you!" ) );
    }

    @Test
    public void testPublishSendsPublishMessage() {
        subject.publish( topic, arguments, kwArguments, resultSubject );

        ArgumentMatcher<WampMessage> messageMatcher = new ArgumentMatcher<WampMessage>() {
            @Override
            public boolean matches( Object argument ) {
                PublishMessage message = (PublishMessage)argument;
                if ( !message.requestId.equals( RequestId.of( 42L ) ) ) return false;
                if ( !message.options.get( "acknowledge" ).asBoolean() ) return false;
                if ( message.topic != topic ) return false;
                if ( message.arguments != arguments ) return false;
                if ( message.argumentsKw != kwArguments ) return false;
                return true;
            }
        };
        verify( baseClient ).scheduleMessageToRouter( argThat( messageMatcher ) );
    }

    @Test
    public void testNotificationOfClientOnSuccessfulPublication() {
        subject.publish( topic, arguments, kwArguments, resultSubject );
        @SuppressWarnings( "unchecked" )
        Observer<Void> observer = mock(Observer.class);
        resultSubject.subscribe( observer );

        subject.onPublished( new PublishedMessage( RequestId.of( 42L ), PublicationId.of( 23L ) ) );

        verify( observer ).onCompleted();
        verify( observer, never() ).onError( any( Throwable.class ) );
    }

    @Test
    public void testNotificationOfClientOnPublicationError() {
        subject.publish( topic, arguments, kwArguments, resultSubject );
        @SuppressWarnings( "unchecked" )
        Observer<Void> observer = mock(Observer.class);
        resultSubject.subscribe( observer );

        subject.onPublishError( new ErrorMessage( PublishMessage.ID,
                                                  RequestId.of( 42L ),
                                                  null,
                                                  ApplicationError.INVALID_ARGUMENT,
                                                  null,
                                                  null ) );

        verify( observer, never() ).onCompleted();
        verify( observer ).onError( any( ApplicationError.class ) );
    }
}
