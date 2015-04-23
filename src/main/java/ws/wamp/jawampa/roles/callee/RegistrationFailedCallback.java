package ws.wamp.jawampa.roles.callee;

public interface RegistrationFailedCallback {
    void registrationFailed( String uri, String Reason, RPCImplementation implementation );
}
