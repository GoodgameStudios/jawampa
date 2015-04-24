package ws.wamp.jawampa.roles.callee;

import java.util.HashMap;
import java.util.Map;

import rx.functions.Action1;
import rx.subjects.PublishSubject;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.io.RequestId;
import ws.wamp.jawampa.messages.ErrorMessage;
import ws.wamp.jawampa.messages.UnregisterMessage;
import ws.wamp.jawampa.messages.UnregisteredMessage;
import ws.wamp.jawampa.messages.handling.BaseMessageHandler;

public class UnregistrationMessageHandler extends BaseMessageHandler {
    private Map<RequestId, RegistrationStateWatcher> requestId2CompletionCallback = new HashMap<RequestId, RegistrationStateWatcher>();
    private Map<RequestId, String> requestId2Uri = new HashMap<RequestId, String>();
    private Map<RequestId, RegistrationId> requestId2registrationId = new HashMap<RequestId, RegistrationId>();

    public UnregistrationMessageHandler( final BaseClient baseClient, PublishSubject<PendingUnregistration> unregistrationsSubject ) {
        unregistrationsSubject.subscribe( new Action1<PendingUnregistration>() {
            @Override
            public void call( PendingUnregistration pending ) {
                RequestId requestId = baseClient.getNewRequestId();
                requestId2CompletionCallback.put( requestId, pending.getHandleRegistrationIdCallback() );
                requestId2Uri.put( requestId, pending.getUri() );
                requestId2registrationId.put( requestId, pending.getRegistrationId() );

                UnregisterMessage message = new UnregisterMessage( requestId, pending.getRegistrationId() );
                baseClient.scheduleMessageToRouter( message );
            }
        });
    }

    @Override
    public void onUnregistered( UnregisteredMessage msg ) {
        requestId2CompletionCallback.get( msg.requestId )
                                    .unregistrationComplete( requestId2registrationId.get( msg.requestId ) );
        cleanup( msg.requestId );
    }

    @Override
    public void onUnregisterError( ErrorMessage msg ) {
        requestId2CompletionCallback.get( msg.requestId )
                                    .unregistrationFailed( requestId2registrationId.get( msg.requestId ),
                                                           msg.error );
        cleanup( msg.requestId );
    }

    private void cleanup( RequestId requestId ) {
        requestId2CompletionCallback.remove( requestId );
        requestId2Uri.remove( requestId );
        requestId2registrationId.remove( requestId );
    }
}
