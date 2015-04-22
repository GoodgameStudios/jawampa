/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.wamp.jawampa.roles;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.AsyncSubject;
import rx.subscriptions.Subscriptions;
import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.Request;
import ws.wamp.jawampa.WampClient.Status;
import ws.wamp.jawampa.WampError;
import ws.wamp.jawampa.internal.UriValidator;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.messages.ErrorMessage;
import ws.wamp.jawampa.messages.InvocationMessage;
import ws.wamp.jawampa.messages.RegisterMessage;
import ws.wamp.jawampa.messages.UnregisterMessage;
import ws.wamp.jawampa.messages.handling.BaseMessageHandler;

/**
 *
 * @author hkraemer@ggs-hh.net
 */
public class CalleeMessageHandler extends BaseMessageHandler {
    private enum RegistrationState {
        Registering,
        Registered,
        Unregistering,
        Unregistered
    }

    private static class RegisteredProceduresMapEntry {
        public RegistrationState state;
        public long registrationId = 0;
        public final Subscriber<? super Request> subscriber;

        public RegisteredProceduresMapEntry(Subscriber<? super Request> subscriber, RegistrationState state) {
            this.subscriber = subscriber;
            this.state = state;
        }
    }

    private final Map<Long, AsyncSubject<?>> requestIdToPendingRegistration
            = new HashMap<Long, AsyncSubject<?>>();
    private final Map<Long, AsyncSubject<?>> requestIdToPendingUnRegistration
            = new HashMap<Long, AsyncSubject<?>>();

    private final HashMap<String, RegisteredProceduresMapEntry> registeredProceduresByUri = 
            new HashMap<String, RegisteredProceduresMapEntry>();
    private final HashMap<Long, RegisteredProceduresMapEntry> registeredProceduresById = 
            new HashMap<Long, RegisteredProceduresMapEntry>();

    private final BaseClient baseClient;
    private final Executor executor;
    private final Scheduler scheduler;

    public CalleeMessageHandler( BaseClient baseClient, Executor executor ) {
        this.baseClient = baseClient;
        this.executor = executor;
        this.scheduler = Schedulers.from(executor);
    }

    private void attachCancelRegistrationAction(final Subscriber<? super Request> subscriber,
            final RegisteredProceduresMapEntry mapEntry,
            final String topic)
    {
        subscriber.add(Subscriptions.create(new Action0() {
            @Override
            public void call() {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mapEntry.state != RegistrationState.Registered) return;

                        mapEntry.state = RegistrationState.Unregistering;
                        registeredProceduresByUri.remove(topic);
                        registeredProceduresById.remove(mapEntry.registrationId);

                        // Make the unregister call
                        final long requestId = baseClient.getNewRequestId();
                        final UnregisterMessage msg = new UnregisterMessage(requestId, mapEntry.registrationId);

                        final AsyncSubject<Void> unregisterFuture = AsyncSubject.create();
                        unregisterFuture.observeOn(scheduler)
                                        .subscribe(new Action1<Void>() {
                            @Override
                            public void call(Void t1) {
                                // Unregistration at the broker was successful
                                mapEntry.state = RegistrationState.Unregistered;
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable t1) {
                                // Error on unregister
                            }
                        });

                        requestIdToPendingUnRegistration.put(requestId,  unregisterFuture);
                        baseClient.scheduleMessageToRouter( msg );
                    }
                });
            }
        }));
    }

    public Observable<Request> registerProcedure(final String topic) {
        return Observable.create(new OnSubscribe<Request>() {
            @Override
            public void call(final Subscriber<? super Request> subscriber) {
                try {
                    UriValidator.validate(topic);
                }
                catch (WampError e) {
                    subscriber.onError(e);
                    return;
                }

                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        // If the Subscriber unsubscribed in the meantime we return early
                        if (subscriber.isUnsubscribed()) return;
                        // Set subscription to completed if we are not connected
                        if (baseClient.connectionState() != Status.Connected) {
                            subscriber.onCompleted();
                            return;
                        }

                        final RegisteredProceduresMapEntry entry = registeredProceduresByUri.get(topic);
                        // Check if we have already registered a function with the same name
                        if (entry != null) {
                            subscriber.onError(
                                new ApplicationError(ApplicationError.PROCEDURE_ALREADY_EXISTS));
                            return;
                        }

                        // Insert a new entry in the subscription map
                        final RegisteredProceduresMapEntry newEntry = 
                            new RegisteredProceduresMapEntry(subscriber, RegistrationState.Registering);
                        registeredProceduresByUri.put(topic, newEntry);

                        // Make the subscribe call
                        final long requestId = baseClient.getNewRequestId();
                        final RegisterMessage msg = new RegisterMessage(requestId, null, topic);

                        final AsyncSubject<Long> registerFuture = AsyncSubject.create();
                        registerFuture.observeOn(scheduler)
                                      .subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long t1) {
                                // Check if we were unsubscribed (through transport close)
                                if (newEntry.state != RegistrationState.Registering) return;
                                // Registration at the broker was successful
                                newEntry.state = RegistrationState.Registered;
                                newEntry.registrationId = t1;
                                registeredProceduresById.put(t1, newEntry);
                                // Add the cancellation functionality to the subscriber
                                attachCancelRegistrationAction(subscriber, newEntry, topic);
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable t1) {
                                // Error on registering
                                if (newEntry.state != RegistrationState.Registering) return;
                                // Remark: Actually noone can't unregister until this Future completes because
                                // the unregister functionality is only added in the success case
                                // However a transport close event could set us to Unregistered early
                                newEntry.state = RegistrationState.Unregistered;

                                boolean isClosed = false;
                                if (t1 instanceof ApplicationError &&
                                        ((ApplicationError)t1).uri().equals(ApplicationError.TRANSPORT_CLOSED))
                                    isClosed = true;

                                if (isClosed) subscriber.onCompleted();
                                else subscriber.onError(t1);

                                registeredProceduresByUri.remove(topic);
                            }
                        });

                        requestIdToPendingRegistration.put(requestId, registerFuture);
                        baseClient.scheduleMessageToRouter(msg);
                    }
                });
            }
        });
    }

    @Override
    public void onInvocation( InvocationMessage m ) {
        RegisteredProceduresMapEntry entry = registeredProceduresById.get(m.registrationId);
        if (entry == null || entry.state != RegistrationState.Registered) {
            // Send an error that we are no longer registered
            baseClient.scheduleMessageToRouter( new ErrorMessage( InvocationMessage.ID,
                                                                  m.requestId,
                                                                  null,
                                                                  ApplicationError.NO_SUCH_PROCEDURE,
                                                                  null,
                                                                  null ) );
        }
        else {
            // Send the request to the subscriber, which can then send responses
            Request request = new Request(baseClient, m.requestId, m.arguments, m.argumentsKw);
            entry.subscriber.onNext(request);
        }
    }
}
