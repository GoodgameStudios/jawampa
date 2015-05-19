package ws.wamp.jawampa.ids;

public abstract class BaseId {
    private final long value;

    protected BaseId( long value ) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int)( value ^ ( value >>> 32 ) );
        result = prime * result + getClass().hashCode();
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
        BaseId other = (BaseId)obj;
        if ( value != other.value )
            return false;
        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [value=" + value + "]";
    }
}
