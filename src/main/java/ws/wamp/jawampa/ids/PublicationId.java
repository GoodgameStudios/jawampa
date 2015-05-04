package ws.wamp.jawampa.ids;

public class PublicationId extends BaseId {
    private PublicationId( long value ) {
        super( value );
    }

    public static PublicationId of( long value ) {
        return new PublicationId( value );
    }
}
