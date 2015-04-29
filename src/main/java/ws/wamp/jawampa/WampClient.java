/*
 * Copyright 2014 Matthias Einwag
 *
 * The jawampa authors license this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package ws.wamp.jawampa;

import java.util.concurrent.Future;

import rx.Observable;
import ws.wamp.jawampa.roles.callee.Response;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Provides the client-side functionality for WAMP.<br>
 * The {@link WampClient} allows to make remote procedudure calls, subscribe to
 * and publish events and to register functions for RPC.<br>
 * It has to be constructed through a {@link WampClientBuilder} and can not
 * directly be instantiated.
 */
public interface WampClient {
    public static enum Status {
        /** The session is not connected */
        DISCONNECTED,
        /** The session is trying to connect to the router */
        CONNECTING,
        /** The session is connected to the router */
        CONNECTED
    }

    public enum PubSubState {
        Subscribing,
        Subscribed,
        Unsubscribing,
        Unsubscribed
    }
    
    public enum RegistrationState {
        Registering,
        Registered,
        Unregistering,
        Unregistered
    }
    /**
     * Opens the session<br>
     * This should be called after a subscription on {@link #statusChanged}
     * was installed.<br>
     * If the session was already opened this has no effect besides
     * resetting the reconnect counter.<br>
     * If the session was already closed through a call to {@link #close}
     * no new connect attempt will be performed.
     */
    public void open();

    /**
     * Closes the session.<br>
     * It will not be possible to open the session again with {@link #open} for safety
     * reasons. If a new session is required a new {@link WampClient} should be built
     * through the used {@link WampClientBuilder}.
     */
    public void close();

    /**
     * An Observable that allows to monitor the connection status of the Session.
     */
    public Observable<Status> statusChanged();

    /**
     * Publishes an event under the given topic.
     * @param topic The topic that should be used for publishing the event
     * @param args A list of all positional arguments of the event to publish.
     * These will be get serialized according to the Jackson library serializing
     * behavior.
     * @return An observable that provides a notification whether the event
     * publication was successful. This contains either a single value (the
     * publication ID) and will then be completed or will be completed with
     * an error if the event could not be published.
     */
    public Observable<Long> publish(final String topic, Object... args);
    
    /**
     * Publishes an event under the given topic.
     * @param topic The topic that should be used for publishing the event
     * @param event The event to publish
     * @return An observable that provides a notification whether the event
     * publication was successful. This contains either a single value (the
     * publication ID) and will then be completed or will be completed with
     * an error if the event could not be published.
     */
    public Observable<Long> publish(final String topic, PubSubData event);
    
    /**
     * Publishes an event under the given topic.
     * @param topic The topic that should be used for publishing the event
     * @param arguments The positional arguments for the published event
     * @param argumentsKw The keyword arguments for the published event.
     * These will only be taken into consideration if arguments is not null.
     * @return An observable that provides a notification whether the event
     * publication was successful. This contains either a single value (the
     * publication ID) and will then be completed or will be completed with
     * an error if the event could not be published.
     */
    public Observable<Long> publish(final String topic, final ArrayNode arguments, 
        final ObjectNode argumentsKw);
    
    /**
     * Registers a procedure at the router which will afterwards be available
     * for remote procedure calls from other clients.<br>
     * The actual registration will only happen after the user subscribes on
     * the returned Observable. This guarantees that no RPC requests get lost.
     * Incoming RPC requests will be pushed to the Subscriber via it's
     * onNext method. The Subscriber can send responses through the methods on
     * the {@link Response}.<br>
     * If the client no longer wants to provide the method it can call
     * unsubscribe() on the Subscription to unregister the procedure.<br>
     * If the connection closes onCompleted will be called.<br>
     * In case of errors during subscription onError will be called.
     * @param topic The name of the procedure which this client wants to
     * provide.<br>
     * Must be valid WAMP URI.
     * @return An observable that can be used to provide a procedure.
     */
    public Observable<Response> registerProcedure(final String topic);
    

    /**
     * Returns an observable that allows to subscribe on the given topic.<br>
     * The actual subscription will only be made after subscribe() was called
     * on it.<br>
     * This version of makeSubscription will automatically transform the
     * received events data into the type eventClass and will therefore return
     * a mapped Observable. It will only look at and transform the first
     * argument of the received events arguments, therefore it can only be used
     * for events that carry either a single or no argument.<br>
     * Received publications will be pushed to the Subscriber via it's
     * onNext method.<br>
     * The client can unsubscribe from the topic by calling unsubscribe() on
     * it's Subscription.<br>
     * If the connection closes onCompleted will be called.<br>
     * In case of errors during subscription onError will be called.
     * @param topic The topic to subscribe on.<br>
     * Must be valid WAMP URI.
     * @param eventClass The class type into which the received event argument
     * should be transformed. E.g. use String.class to let the client try to
     * transform the first argument into a String and let the return value of
     * of the call be Observable&lt;String&gt;.
     * @return An observable that can be used to subscribe on the topic.
     */
    public <T> Observable<T> makeSubscription(final String topic, final Class<T> eventClass);
    
    /**
     * Returns an observable that allows to subscribe on the given topic.<br>
     * The actual subscription will only be made after subscribe() was called
     * on it.<br>
     * Received publications will be pushed to the Subscriber via it's
     * onNext method.<br>
     * The client can unsubscribe from the topic by calling unsubscribe() on
     * it's Subscription.<br>
     * If the connection closes onCompleted will be called.<br>
     * In case of errors during subscription onError will be called.
     * @param topic The topic to subscribe on.<br>
     * Must be valid WAMP URI.
     * @return An observable that can be used to subscribe on the topic.
     */
    public Observable<PubSubData> makeSubscription(final String topic);
    

    /**
     * Performs a remote procedure call through the router.<br>
     * The function will return immediately, as the actual call will happen
     * asynchronously.
     * @param procedure The name of the procedure to call. Must be a valid WAMP
     * Uri.
     * @param arguments A list of all positional arguments for the procedure call
     * @param argumentsKw All named arguments for the procedure call
     * @return An observable that provides a notification whether the call was
     * was successful and the return value. If the call is successful the
     * returned observable will be completed with a single value (the return value).
     * If the remote procedure call yields an error the observable will be completed
     * with an error.
     */
    public Observable<Reply> call(final String procedure,
                                  final ArrayNode arguments,
                                  final ObjectNode argumentsKw);
    
    /**
     * Performs a remote procedure call through the router.<br>
     * The function will return immediately, as the actual call will happen
     * asynchronously.
     * @param procedure The name of the procedure to call. Must be a valid WAMP
     * Uri.
     * @param args The list of positional arguments for the remote procedure call.
     * These will be get serialized according to the Jackson library serializing
     * behavior.
     * @return An observable that provides a notification whether the call was
     * was successful and the return value. If the call is successful the
     * returned observable will be completed with a single value (the return value).
     * If the remote procedure call yields an error the observable will be completed
     * with an error.
     */
    public Observable<Reply> call(final String procedure, Object... args);
    
    /**
     * Performs a remote procedure call through the router.<br>
     * The function will return immediately, as the actual call will happen
     * asynchronously.<br>
     * This overload of the call function will automatically map the received
     * reply value into the specified Java type by using Jacksons object mapping
     * facilities.<br>
     * Only the first value in the array of positional arguments will be taken
     * into account for the transformation. If multiple return values are required
     * another overload of this function has to be used.<br>
     * If the expected return type is not {@link Void} but the return value array
     * contains no value or if the value in the array can not be deserialized into
     * the expected type the returned {@link Observable} will be completed with
     * an error.
     * @param procedure The name of the procedure to call. Must be a valid WAMP
     * Uri.
     * @param returnValueClass The class of the expected return value. If the function
     * uses no return values Void should be used.
     * @param args The list of positional arguments for the remote procedure call.
     * These will be get serialized according to the Jackson library serializing
     * behavior.
     * @return An observable that provides a notification whether the call was
     * was successful and the return value. If the call is successful the
     * returned observable will be completed with a single value (the return value).
     * If the remote procedure call yields an error the observable will be completed
     * with an error.
     */
    public <T> Observable<T> call(final String procedure, 
                                  final Class<T> returnValueClass, Object... args);
    
    /**
     * Returns a future that will be completed once the client terminates.<br>
     * This can be used to wait for completion after {@link #close() close} was called.
     */
    public Future<Void> getTerminationFuture();
}
