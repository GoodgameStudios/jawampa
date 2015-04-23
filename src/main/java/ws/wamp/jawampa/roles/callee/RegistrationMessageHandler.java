package ws.wamp.jawampa.roles.callee;

import java.util.HashMap;
import java.util.Map;

import rx.functions.Action1;
import rx.subjects.PublishSubject;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.messages.ErrorMessage;
import ws.wamp.jawampa.messages.RegisterMessage;
import ws.wamp.jawampa.messages.RegisteredMessage;
import ws.wamp.jawampa.messages.handling.BaseMessageHandler;

public class RegistrationMessageHandler extends BaseMessageHandler {
    private Map<Long, RegistrationCallback> requestId2CompletionCallback = new HashMap<Long, RegistrationCallback>();
    private Map<Long, String> requestId2Uri = new HashMap<Long, String>();

    public RegistrationMessageHandler( final BaseClient baseClient, PublishSubject<PendingRegistration> registrationSubject ) {
        registrationSubject.subscribe( new Action1<PendingRegistration>() {
            @Override
            public void call( PendingRegistration pending ) {
                long requestId = baseClient.getNewRequestId();
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
        requestId2CompletionCallback.get( msg.requestId ).registrationComplete( msg.newResourceId, requestId2Uri.get( msg.requestId ) );
        requestId2CompletionCallback.remove( msg.requestId );
        requestId2Uri.remove( msg.requestId );
    }

    @Override
    public void onRegisterError( ErrorMessage msg ) {
        // TODO Auto-generated method stub
        super.onRegisterError( msg );
    }
}
