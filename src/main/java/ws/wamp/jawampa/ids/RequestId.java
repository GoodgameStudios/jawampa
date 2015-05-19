package ws.wamp.jawampa.ids;

public final class RequestId extends BaseId {
    private RequestId( long value ) {
        super( value );
    }

    public static RequestId of( long value ) {
        return new RequestId( value );
    }
}
