package ws.wamp.jawampa.ids;

public final class RegistrationId extends BaseId {
    private RegistrationId( long value ) {
        super( value );
    }

    public static RegistrationId of( long value ) {
        return new RegistrationId( value );
    }
}
