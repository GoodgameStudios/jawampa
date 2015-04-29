package ws.wamp.jawampa.ids;

public class RequestId {
    private final long value;

    private RequestId( long value ) {
        this.value = value;
    }

    public static RequestId of( long value ) {
        return new RequestId( value );
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
        RequestId other = (RequestId)obj;
        if ( value != other.value )
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RequestId [value=" + value + "]";
    }
}
