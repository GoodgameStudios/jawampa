package ws.wamp.jawampa.io;

import ws.wamp.jawampa.messages.WampMessage;

/**
 * @author hkraemer@ggs-hh.net
 */
public interface ClientRoleIOFacade {
    public void scheduleMessageToRouter( WampMessage message );
}
