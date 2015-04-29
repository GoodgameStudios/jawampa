package ws.wamp.jawampa.internal;

import static org.junit.Assert.*;

import org.junit.Test;

public class SessionScopeIdGeneratorTest {

    @Test
    public void testFirstIDs() {
        SessionScopeIdGenerator subject = new SessionScopeIdGenerator();
        assertEquals( 1, subject.nextId() );
        assertEquals( 2, subject.nextId() );
    }

    @Test
    public void testOverflow() {
        SessionScopeIdGenerator subject = new SessionScopeIdGenerator( 9007199254740992L );
        assertEquals( 9007199254740992L, subject.nextId() );
        assertEquals( 1L, subject.nextId() );
    }
}
