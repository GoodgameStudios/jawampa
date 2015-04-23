package ws.wamp.jawampa.roles.callee;

public interface RegistrationCallback {
    void registrationComplete( long id, String uri );
}
