/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.wamp.jawampa.roles;

import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.messages.ErrorMessage;
import ws.wamp.jawampa.messages.InvocationMessage;
import ws.wamp.jawampa.messages.RegisterMessage;
import ws.wamp.jawampa.messages.RegisteredMessage;
import ws.wamp.jawampa.messages.UnregisterMessage;
import ws.wamp.jawampa.messages.UnregisteredMessage;
import ws.wamp.jawampa.messages.handling.BaseMessageHandler;
import ws.wamp.jawampa.roles.callee.FunctionMap;
import ws.wamp.jawampa.roles.callee.InvocationMessageHandler;
import ws.wamp.jawampa.roles.callee.RPCImplementation;
import ws.wamp.jawampa.roles.callee.RegistrationMessageHandler;
import ws.wamp.jawampa.roles.callee.UnregistrationMessageHandler;

/**
 *
 * @author hkraemer@ggs-hh.net
 */
public class CalleeMessageHandler extends BaseMessageHandler {
    private final FunctionMap map;
    private final InvocationMessageHandler imh;
    private final RegistrationMessageHandler rmh;
    private final UnregistrationMessageHandler urmh;

    public CalleeMessageHandler( BaseClient baseClient ) {
        map = new FunctionMap();
        imh = new InvocationMessageHandler( baseClient, map );
        rmh = new RegistrationMessageHandler( baseClient, map.getRegistrationsSubject() );
        urmh = new UnregistrationMessageHandler( baseClient, map.getUnregistrationsSubject() );
    }

    @Override
    public void onRegister( RegisterMessage msg ) {
        rmh.onRegister( msg );
    }

    @Override
    public void onRegistered( RegisteredMessage msg ) {
        rmh.onRegistered( msg );
    }

    @Override
    public void onRegisterError( ErrorMessage msg ) {
        rmh.onRegisterError( msg );
    }

    @Override
    public void onUnregister( UnregisterMessage msg ) {
        urmh.onUnregister( msg );
    }

    @Override
    public void onUnregistered( UnregisteredMessage msg ) {
        urmh.onUnregistered( msg );
    }

    @Override
    public void onUnregisterError( ErrorMessage msg ) {
        urmh.onUnregisterError( msg );
    }

    @Override
    public void onInvocation( InvocationMessage msg ) {
        imh.onInvocation( msg );
    }

    @Override
    public void onInvocationError( ErrorMessage msg ) {
        imh.onInvocationError( msg );
    }

    public void register( String uri, RPCImplementation implementation ) {
        map.register( uri, implementation );
    }

    public void unregister( String uri ) {
        map.unregister( uri );
    }
}
