package ws.wamp.jawampa.roles.callee;

public interface UnregistrationFailedCallback {
    void unregistrationFailed( String uri, String reason );
}
