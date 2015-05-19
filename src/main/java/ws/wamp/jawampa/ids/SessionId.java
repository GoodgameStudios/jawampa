package ws.wamp.jawampa.ids;

public final class SessionId extends BaseId {
    private SessionId( long value ) {
        super( value );
    }

    public static SessionId of( long value ) {
        return new SessionId( value );
    }
}
