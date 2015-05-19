package ws.wamp.jawampa.ids;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ws.wamp.jawampa.ids.SessionScopeIdGenerator;

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
