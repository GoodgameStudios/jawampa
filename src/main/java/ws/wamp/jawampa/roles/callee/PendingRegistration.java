package ws.wamp.jawampa.roles.callee;

public class PendingRegistration {
    private final String uri;
    private final RegistrationStateWatcher handleRegistrationIdCallback;

    public PendingRegistration( String uri, RegistrationStateWatcher handleRegistrationIdCallback ) {
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
