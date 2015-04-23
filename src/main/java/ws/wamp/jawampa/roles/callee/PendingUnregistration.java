package ws.wamp.jawampa.roles.callee;

public class PendingUnregistration {
    private final String uri;
    private final RegistrationStateWatcher handleRegistrationIdCallback;

    public PendingUnregistration( String uri, RegistrationStateWatcher handleRegistrationIdCallback ) {
        this.uri = uri;
        this.handleRegistrationIdCallback = handleRegistrationIdCallback;
    }

    public String getUri() {
        return uri;
    }

    public RegistrationStateWatcher getHandleRegistrationIdCallback() {
        return handleRegistrationIdCallback;
    }
}
