/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.wamp.jawampa.roles;

import rx.subjects.PublishSubject;
import ws.wamp.jawampa.Request;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.messages.ErrorMessage;
import ws.wamp.jawampa.messages.InvocationMessage;
import ws.wamp.jawampa.messages.RegisterMessage;
import ws.wamp.jawampa.messages.RegisteredMessage;
import ws.wamp.jawampa.messages.UnregisterMessage;
import ws.wamp.jawampa.messages.UnregisteredMessage;
import ws.wamp.jawampa.messages.handling.BaseMessageHandler;

/**
 *
 * @author hkraemer@ggs-hh.net
 */
public class Callee extends BaseMessageHandler {
    private final BaseClient baseClient;

    public Callee( BaseClient baseClient ) {
        this.baseClient = baseClient;
    }

    public void register( final String procedure, final PublishSubject<Request> resultSubject ) {
        baseClient.scheduleMessageToRouter( new RegisterMessage( baseClient.getNewRequestId(),
                                                                 null,
                                                                 procedure ) );
    }

    @Override
    public void onRegister( RegisterMessage msg ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onRegistered( RegisteredMessage msg ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onRegisterError( ErrorMessage msg ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onInvocation( InvocationMessage msg ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onInvocationError( ErrorMessage msg ) {
        throw new UnsupportedOperationException();
    }

    public void unregister( final String procedure, final PublishSubject<Void> resultSubject ) {
        throw new UnsupportedOperationException();
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
