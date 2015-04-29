package ws.wamp.jawampa.roles.callee;

import ws.wamp.jawampa.ids.RegistrationId;


public interface RegistrationStateWatcher {
    void registrationComplete( RegistrationId registrationId, String uri );
    void registrationFailed( String uri, String reason );
    void unregistrationComplete( RegistrationId registrationId );
    void unregistrationFailed( RegistrationId registrationId, String reason );
}
