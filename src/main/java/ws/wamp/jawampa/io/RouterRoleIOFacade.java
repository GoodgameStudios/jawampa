package ws.wamp.jawampa.io;

import ws.wamp.jawampa.messages.WampMessage;

/**
 *
 * @author hkraemer@ggs-hh.net
 */
public interface RouterRoleIOFacade {
    void scheduleMessageToRouter( ClientIdentification target, WampMessage message );
}
