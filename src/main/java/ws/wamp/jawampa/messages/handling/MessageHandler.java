/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.wamp.jawampa.messages.handling;

import ws.wamp.jawampa.messages.AbortMessage;
import ws.wamp.jawampa.messages.AuthenticateMessage;
import ws.wamp.jawampa.messages.CallMessage;
import ws.wamp.jawampa.messages.ChallengeMessage;
import ws.wamp.jawampa.messages.ErrorMessage;
import ws.wamp.jawampa.messages.EventMessage;
import ws.wamp.jawampa.messages.GoodbyeMessage;
import ws.wamp.jawampa.messages.HelloMessage;
import ws.wamp.jawampa.messages.InvocationMessage;
import ws.wamp.jawampa.messages.PublishMessage;
import ws.wamp.jawampa.messages.PublishedMessage;
import ws.wamp.jawampa.messages.RegisterMessage;
import ws.wamp.jawampa.messages.RegisteredMessage;
import ws.wamp.jawampa.messages.RequestedMessage;
import ws.wamp.jawampa.messages.ResultMessage;
import ws.wamp.jawampa.messages.SubscribeMessage;
import ws.wamp.jawampa.messages.SubscribedMessage;
import ws.wamp.jawampa.messages.UnregisterMessage;
import ws.wamp.jawampa.messages.UnregisteredMessage;
import ws.wamp.jawampa.messages.UnsubscribeMessage;
import ws.wamp.jawampa.messages.UnsubscribedMessage;
import ws.wamp.jawampa.messages.WampMessage;
import ws.wamp.jawampa.messages.WelcomeMessage;
import ws.wamp.jawampa.messages.YieldMessage;

/**
 *
 * @author hkraemer@ggs-hh.net
 */
public interface MessageHandler {
    // Note: Router & Client are no official WAMP roles. 
    // However, at this point in the protocol, we just don't know the roles,
    // so we just use router and client to note who can receive the messages
    
    // Initial Handshake and authentication
    void onHello( HelloMessage msg );               // Role: Router
    void onChallenge( ChallengeMessage msg );       // Role: Client
    void onAuthenticate( AuthenticateMessage msg ); // Role: Router
    void onWelcome( WelcomeMessage msg );           // Role: Client
    void onAbort( AbortMessage msg );               // Role: Client & Router
    
    // connection termination
    void onGoodbye( GoodbyeMessage msg );           // Role: Client & Router
    
    // At this point in the protocol, we have exchanged roles and now both
    // participants know the capabilities and roles of the other side.
    // Thus, we can use proper roles now.
    
    // Note that if you're looking at wamp.ws diagrams, these are the 
    // roles at the tip of the arrow, not the start, because these
    // are methods made to process messages.
    
    // Also note that we are rarely handling error on it's own, usually,
    // we map error to the corresponding on$Foo$Error. That is, if a
    // SUBSCRIBE got answered with an ERROR, we don't call onError,
    // we rather call onSubscribeError.
    
    // subscriptions
    void onSubscribe( SubscribeMessage msg );       // Role: Broker
    void onSubscribed( SubscribedMessage msg );     // Role: Subscriber
    void onSubscribeError( ErrorMessage msg );      // Role: Subscriber.
    
    // unsubscription
    void onUnsubscribe( UnsubscribeMessage msg );   // Role: Broker
    void onUnsubscribed( UnsubscribedMessage msg ); // Role: Subscriber
    void onUnsubscribeError( ErrorMessage msg );    // Role: Subscriber
    
    // publication
    void onPublish( PublishMessage msg );           // Role: Broker
    void onPublished( PublishedMessage msg );       // Role: Publisher
    void onEvent( EventMessage msg );               // Role: Subscriber
    void onPublishError( ErrorMessage msg );        // Role: Publisher
    
    // registration
    void onRegister( RegisterMessage msg );         // Role: Dealer
    void onRegistered( RegisteredMessage msg );     // Role: Callee
    void onRegisterError( ErrorMessage msg );       // Role: Callee
    
    // unregister
    void onUnregister( UnregisterMessage msg );     // Role: Dealer
    void onUnregistered( UnregisteredMessage msg ); // Role: Callee
    void onUnregisterError( ErrorMessage msg );     // Role: Callee

    // RPC calls
    void onCall( CallMessage msg );                 // Role: Dealer
    void onInvocation( InvocationMessage msg );     // Role: Callee
    void onYield( YieldMessage msg );               // Role: Dealer
    void onResult( ResultMessage msg );             // Role: Caller
    void onInvocationError( ErrorMessage msg );     // Role: Dealer
    void onCallError( ErrorMessage msg );           // Role: Caller
        
    // called like this if we can't put the error in a more specific place.
    void onError( ErrorMessage msg );               // Role: All
}

