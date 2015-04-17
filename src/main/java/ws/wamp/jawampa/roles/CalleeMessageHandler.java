/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.wamp.jawampa.roles;

import ws.wamp.jawampa.messages.InvocationMessage;
import ws.wamp.jawampa.messages.handling.BaseMessageHandler;

/**
 *
 * @author hkraemer@ggs-hh.net
 */
public class CalleeMessageHandler extends BaseMessageHandler {
    
    @Override
    public void onInvocation( InvocationMessage m ) {
        System.out.println( "Hi, I am supposed to be annoying so you can't forget me" );
    }
    
    public void addMethod( String notDone ) {
        System.out.println( "Hi, I am supposed to be annoying so you can't forget me" );
    }
}
