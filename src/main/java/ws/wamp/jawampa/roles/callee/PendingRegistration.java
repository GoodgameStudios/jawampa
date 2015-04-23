package ws.wamp.jawampa.roles.callee;

public class PendingRegistration {
    private final String uri;
    private final RegistrationCallback handleRegistrationIdCallback;

    public PendingRegistration( String uri, RegistrationCallback handleRegistrationIdCallback ) {
        this.uri = uri;
        this.handleRegistrationIdCallback = handleRegistrationIdCallback;
    }

    public String getUri() {
        return uri;
    }

    public RegistrationCallback getHandleRegistrationIdCallback() {
        return handleRegistrationIdCallback;
    }
}
