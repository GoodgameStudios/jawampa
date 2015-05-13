/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.wamp.jawampa.roles;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import rx.Observer;
import rx.subjects.AsyncSubject;
import rx.subjects.PublishSubject;
import ws.wamp.jawampa.Request;
import ws.wamp.jawampa.ids.RegistrationId;
import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.messages.ErrorMessage;
import ws.wamp.jawampa.messages.InvocationMessage;
import ws.wamp.jawampa.messages.RegisterMessage;
import ws.wamp.jawampa.messages.RegisteredMessage;
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

    private final Set<String> pendingRegistrations;
    /*
     * An entry for a procedure is in this set, iff
     * - unregister was called
     * - neither REGISTERED nor ERROR was yet recieved in response to REGISTER
     */
    private final Map<String, PublishSubject<Void>> earlyUnregistrations;

    public Callee( BaseClient baseClient ) {
        this.baseClient = baseClient;

        registrationTracker = new RequestTracker<RegistrationId>( baseClient );
        unregistrationTracker = new RequestTracker<Void>( baseClient );

        registrationId2PublishSubject = new HashMap<RegistrationId, PublishSubject<Request>>();
        procedureName2registrationId = new DualHashBidiMap<String, RegistrationId>();

        pendingRegistrations = new HashSet<String>();
        earlyUnregistrations = new HashMap<String, PublishSubject<Void>>();
    }

    public void register( final String procedure, final PublishSubject<Request> resultSubject ) {
        AsyncSubject<RegistrationId> registrationSubject = AsyncSubject.create();
        registrationSubject.subscribe( new Observer<RegistrationId>() {
            @Override
            public void onNext( RegistrationId subscriptionId ) {
                registrationId2PublishSubject.put( subscriptionId, resultSubject );
                procedureName2registrationId.put( procedure, subscriptionId );
                pendingRegistrations.remove( procedure );
                if ( earlyUnregistrations.containsKey( procedure ) ) {
                    doUnregister( procedure, earlyUnregistrations.get( procedure ) );
                }
            }

            @Override
            public void onCompleted() {
                // intentionally empty
            }

            @Override
            public void onError( Throwable e ) {
                pendingRegistrations.remove( procedure );
                earlyUnregistrations.remove( procedure );
                resultSubject.onError( e );
            }
        } );
        pendingRegistrations.add( procedure );
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
    public void onRegistered( RegisteredMessage msg ) {
        registrationTracker.onSuccess( msg.requestId, msg.registrationId );
    }

    @Override
    public void onRegisterError( ErrorMessage msg ) {
        registrationTracker.onError( msg );
    }

    @Override
    public void onInvocation( InvocationMessage msg ) {
        if ( registrationId2PublishSubject.containsKey( msg.registrationId ) ) {
            registrationId2PublishSubject.get( msg.registrationId )
                                         .onNext( new Request( baseClient,
                                                               msg.requestId,
                                                               msg.arguments,
                                                               msg.argumentsKw ) );
        } else {
            baseClient.onProtocolError();
        }
    }

    @Override
    public void onInvocationError( ErrorMessage msg ) {
        throw new UnsupportedOperationException();
    }

    private void doUnregister( final String procedure, final PublishSubject<Void> resultSubject ) {
        AsyncSubject<Void> unregistrationSubject = AsyncSubject.create();
        unregistrationSubject.subscribe( new Observer<Void>() {
            @Override
            public void onNext( Void t ) {
                // intentionally empty
            }

            @Override
            public void onCompleted() {
                RegistrationId registrationId = procedureName2registrationId.get( procedure );
                PublishSubject<Request> publishSubject = registrationId2PublishSubject.get( registrationId );

                registrationId2PublishSubject.remove( registrationId );
                procedureName2registrationId.remove( procedure );

                resultSubject.onCompleted();
                publishSubject.onCompleted();
            }

            @Override
            public void onError( Throwable e ) {
                resultSubject.onError( e );
            }
        } );
        unregistrationTracker.sendRequest( unregistrationSubject, new MessageFactory() {
            @Override
            public WampMessage fromRequestId( RequestId requestId ) {
                return new UnregisterMessage( requestId,
                                              procedureName2registrationId.get( procedure ) );
            }
        } );
        earlyUnregistrations.remove( procedure );
    }

    public void unregister( final String procedure, final PublishSubject<Void> resultSubject ) {
        if ( pendingRegistrations.contains( procedure ) ) {
            // unregister was called before REGISTERED was received
            earlyUnregistrations.put( procedure, resultSubject );
        } else {
            doUnregister( procedure, resultSubject );
        }
    }

    @Override
    public void onUnregistered( UnregisteredMessage msg ) {
        unregistrationTracker.onSuccess( msg.requestId, null );
    }

    @Override
    public void onUnregisterError( ErrorMessage msg ) {
        unregistrationTracker.onError( msg );
    }
}
