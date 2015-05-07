/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.wamp.jawampa.roles;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import rx.Observer;
import rx.subjects.AsyncSubject;
import rx.subjects.PublishSubject;
import ws.wamp.jawampa.PubSubData;
import ws.wamp.jawampa.Request;
import ws.wamp.jawampa.ids.RegistrationId;
import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.ids.SubscriptionId;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.messages.ErrorMessage;
import ws.wamp.jawampa.messages.InvocationMessage;
import ws.wamp.jawampa.messages.RegisterMessage;
import ws.wamp.jawampa.messages.RegisteredMessage;
import ws.wamp.jawampa.messages.SubscribeMessage;
import ws.wamp.jawampa.messages.UnregisterMessage;
import ws.wamp.jawampa.messages.UnregisteredMessage;
import ws.wamp.jawampa.messages.WampMessage;
import ws.wamp.jawampa.messages.handling.BaseMessageHandler;
import ws.wamp.jawampa.roles.RequestTracker.MessageFactory;

/**
 *
 * @author hkraemer@ggs-hh.net
 */
public class Callee extends BaseMessageHandler {
    private final BaseClient baseClient;

    private final RequestTracker<RegistrationId> registrationTracker;
    private final RequestTracker<Void> unregistrationTracker;

    private final Map<RegistrationId, PublishSubject<Request>> registrationId2PublishSubject;
    private final BidiMap<String, RegistrationId> procedureName2registrationId;

    public Callee( BaseClient baseClient ) {
        this.baseClient = baseClient;

        registrationTracker = new RequestTracker<RegistrationId>( baseClient );
        unregistrationTracker = new RequestTracker<Void>( baseClient );

        registrationId2PublishSubject = new HashMap<RegistrationId, PublishSubject<Request>>();
        procedureName2registrationId = new DualHashBidiMap<String, RegistrationId>();
    }

    public void register( final String procedure, final PublishSubject<Request> resultSubject ) {
        AsyncSubject<RegistrationId> registrationSubject = AsyncSubject.create();
        registrationSubject.subscribe( new Observer<RegistrationId>() {
            @Override
            public void onNext( RegistrationId subscriptionId ) {
                registrationId2PublishSubject.put( subscriptionId, resultSubject );
                procedureName2registrationId.put( procedure, subscriptionId );
            }

            @Override
            public void onCompleted() {
                // intentionally empty
            }

            @Override
            public void onError( Throwable e ) {
                resultSubject.onError( e );
            }
        } );
        registrationTracker.sendRequest( registrationSubject, new MessageFactory() {
            @Override
            public WampMessage fromRequestId( RequestId requestId ) {
                return new RegisterMessage( requestId,
                                            null,
                                            procedure );
            }
        } );
    }

    @Override
    public void onRegister( RegisterMessage msg ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onRegistered( RegisteredMessage msg ) {
        registrationTracker.onSuccess( msg.requestId, msg.registrationId );
    }

    @Override
    public void onRegisterError( ErrorMessage msg ) {
        registrationTracker.onError( msg );
    }

    @Override
    public void onInvocation( InvocationMessage msg ) {
        registrationId2PublishSubject.get( msg.registrationId )
                                     .onNext( new Request( baseClient,
                                                           msg.requestId,
                                                           msg.arguments,
                                                           msg.argumentsKw ) );
    }

    @Override
    public void onInvocationError( ErrorMessage msg ) {
        throw new UnsupportedOperationException();
    }

    public void unregister( final String procedure, final PublishSubject<Void> resultSubject ) {
        baseClient.scheduleMessageToRouter( new UnregisterMessage( baseClient.getNewRequestId(),
                                                                   procedureName2registrationId.get( procedure ) ) );
    }

    @Override
    public void onUnregister( UnregisterMessage msg ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onUnregistered( UnregisteredMessage msg ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onUnregisterError( ErrorMessage msg ) {
        throw new UnsupportedOperationException();
    }
}
