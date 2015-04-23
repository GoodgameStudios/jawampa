package ws.wamp.jawampa.roles.callee;

public interface RegistrationFailedCallback {
    void registrationFailed( String uri, String reason, RPCImplementation implementation );
}
