package ws.wamp.jawampa.internal;

// See https://github.com/tavendo/WAMP/blob/master/spec/basic.md#ids
public class SessionScopeIdGenerator {
    private static final long MAX_ID = 1L << 53;
    private long nextId;

    public SessionScopeIdGenerator() {
        this( 1L );
    }

    // For tests only
    SessionScopeIdGenerator( long firstIdGenerated ) {
        this.nextId = firstIdGenerated;
    }

    public long nextId() {
        try {
            return nextId;
        } finally {
            nextId++;
            if ( nextId > MAX_ID ) {
                nextId = 1;
            }
        }
    }
}
