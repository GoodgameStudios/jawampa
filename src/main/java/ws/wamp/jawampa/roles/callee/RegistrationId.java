package ws.wamp.jawampa.roles.callee;

public class RegistrationId {
    private final long value;

    private RegistrationId( long value ) {
        this.value = value;
    }

    public static RegistrationId of( long value ) {
        return new RegistrationId( value );
    }

    public long getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int)( value ^ ( value >>> 32 ) );
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        RegistrationId other = (RegistrationId)obj;
        if ( value != other.value )
            return false;
        return true;
    }
}
