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

import rx.Observable;
import ws.wamp.jawampa.registrations.Procedure;
import ws.wamp.jawampa.registrations.Subscription;

import com.fasterxml.jackson.databind.ObjectMapper;
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
        /** The session is connected to the router */
        CONNECTED,
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
     * @param arguments The positional arguments for the published event
     * @param argumentsKw The keyword arguments for the published event.
     * These will only be taken into consideration if arguments is not null.
     * @return An observable that provides a notification whether the event
     * publication was successful. This contains either a single value (the
     * publication ID) and will then be completed or will be completed with
     * an error if the event could not be published.
     */
    public Observable<Void> publish(final String topic, final ArrayNode arguments, 
        final ObjectNode argumentsKw);
    
    /**
     * Begins registering a procedure at the router which will afterwards be
     * available for remote procedure calls from other clients.<br>
     * To actually register the procedure, do something like
     * <pre>
     * wampclient.startRegisteringProcedure( procedure )
     *           .onError( someLambda )
     *           .onFinished( someLambda )
     *           .onCall( someLambda )
     *           .register()
     * </pre>
     * To unregister the procedure, call unregister() on the Procedure object
     * returned from Procedure.Builder.register()<br>
     * @param procedure The name of the procedure which this client wants to
     * provide.<br>
     * Must be valid WAMP URI.
     * @return A Procedure.Builder that can be used to register a procedure.
     */
    public Procedure.Builder startRegisteringProcedure( String procedure );
    

    /**
     * Begins subscribing to a topic</br>
     * To actually subscribe, call something like
     * <pre>
     * wampclient.startSubscribing( topic )
     *           .onError( someLambda )
     *           .onFinished( someLambda )
     *           .onEvent( someLambda )
     *           .subscribe()
     * </pre>
     * Received publications will be pushed to the client via the method passed
     * in onEvent.<br>
     * To unsubscribe from a topic, call unsubscribe() on the Subscription object
     * returned from Subscribption.Builder.subscribe()<br>
     * The client can unsubscribe from the topic by calling unsubscribe() on
     * it's Subscription.<br>
     * @param topic The topic to subscribe on.<br>
     * Must be valid WAMP URI.
     * @return A Subscription.Builder that can be used to actually subscribe to a topic.
     */
    public Subscription.Builder startSubscribing( String topic );
    

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
     * Returns the ObjectMapper used. If any methods of this API require an ArrayNode or
     * an ObjectNode, use this mapper to create them!.
     * @return the mapper
     */
    public ObjectMapper getMapper();
}
