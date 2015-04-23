package ws.wamp.jawampa.roles.callee;

import rx.functions.Action1;
import rx.subjects.PublishSubject;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.messages.ErrorMessage;
import ws.wamp.jawampa.messages.UnregisteredMessage;
import ws.wamp.jawampa.messages.handling.BaseMessageHandler;

public class UnregistrationMessageHandler extends BaseMessageHandler {
    public UnregistrationMessageHandler( final BaseClient baseclient, PublishSubject<PendingUnregistration> unregistrationsSubject ) {
        unregistrationsSubject.subscribe( new Action1<PendingUnregistration>() {
            @Override
            public void call( PendingUnregistration t1 ) {
                // TODO Auto-generated method stub
                
            }
        });
    }

    @Override
    public void onUnregistered( UnregisteredMessage msg ) {
        // TODO Auto-generated method stub
        super.onUnregistered( msg );
    }

    @Override
    public void onUnregisterError( ErrorMessage msg ) {
        // TODO Auto-generated method stub
        super.onUnregisterError( msg );
    }
}
