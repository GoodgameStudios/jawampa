package ws.wamp.jawampa.roles.callee;


public interface RegistrationStateWatcher {
    void registrationComplete( RegistrationId requestId, String uri );
    void registrationFailed( RegistrationId requestId, String uri );
}
