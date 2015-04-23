package ws.wamp.jawampa.roles.callee;


public interface RegistrationStateWatcher {
    void registrationComplete( RegistrationId registrationId, String uri );
    void registrationFailed( String uri, String reason );
    void unregistrationComplete( RegistrationId registrationId, String uri );
    void unregistrationFailed( String uri, String reason );
}
