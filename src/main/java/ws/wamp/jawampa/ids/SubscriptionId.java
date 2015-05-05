package ws.wamp.jawampa.ids;

public class SubscriptionId extends BaseId {
    private SubscriptionId( long value ) {
        super( value );
    }

    public static SubscriptionId of( long value ) {
        return new SubscriptionId( value );
    }
}
