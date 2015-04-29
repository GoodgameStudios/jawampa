package ws.wamp.jawampa.roles.callee;

import java.util.HashMap;
import java.util.Map;

import rx.functions.Action1;
import rx.subjects.PublishSubject;
import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.messages.ErrorMessage;
import ws.wamp.jawampa.messages.RegisterMessage;
import ws.wamp.jawampa.messages.RegisteredMessage;
import ws.wamp.jawampa.messages.handling.BaseMessageHandler;

public class RegistrationMessageHandler extends BaseMessageHandler {
    private Map<RequestId, RegistrationStateWatcher> requestId2CompletionCallback = new HashMap<RequestId, RegistrationStateWatcher>();
    private Map<RequestId, String> requestId2Uri = new HashMap<RequestId, String>();

    public RegistrationMessageHandler( final BaseClient baseClient, PublishSubject<PendingRegistration> registrationSubject ) {
        registrationSubject.subscribe( new Action1<PendingRegistration>() {
            @Override
            public void call( PendingRegistration pending ) {
                RequestId requestId = baseClient.getNewRequestId();
                requestId2CompletionCallback.put( requestId, pending.getHandleRegistrationIdCallback() );
                requestId2Uri.put( requestId, pending.getUri() );

                RegisterMessage message = new RegisterMessage( requestId,
                                                               null,
                                                               pending.getUri() );
                baseClient.scheduleMessageToRouter( message );
            }
        });
    }

    @Override
    public void onRegistered( RegisteredMessage msg ) {
        requestId2CompletionCallback.get( msg.requestId ).registrationComplete( msg.registrationId, requestId2Uri.get( msg.requestId ) );
        cleanup( msg.requestId );
    }

    @Override
    public void onRegisterError( ErrorMessage msg ) {
        requestId2CompletionCallback.get( msg.requestId ).registrationFailed( requestId2Uri.get( msg.requestId ), msg.error );
        cleanup( msg.requestId );
    }

    private void cleanup( RequestId requestId ) {
        requestId2CompletionCallback.remove( requestId );
        requestId2Uri.remove( requestId );
    }
}
