package ws.wamp.jawampa.roles.callee;

import ws.wamp.jawampa.ids.RegistrationId;

public class PendingUnregistration {
    private final String uri;
    private final RegistrationId registrationId;
    private final RegistrationStateWatcher handleRegistrationIdCallback;

    public PendingUnregistration( String uri, RegistrationId registrationId, RegistrationStateWatcher handleRegistrationIdCallback ) {
        this.uri = uri;
        this.registrationId = registrationId;
        this.handleRegistrationIdCallback = handleRegistrationIdCallback;
    }

    public String getUri() {
        return uri;
    }

    public RegistrationId getRegistrationId() {
        return registrationId;
    }

    public RegistrationStateWatcher getHandleRegistrationIdCallback() {
        return handleRegistrationIdCallback;
    }
}
