package ws.wamp.jawampa.messages;

public enum MessageCode {
    HELLO(1),
    WELCOME(2),
    ABORT(3),
    CHALLENGE(4),
    AUTHENTICATE(5),
    GOODBYE(6),
    
    ERROR(8),
    
    PUBLISH(16),
    PUBLISHED(17),
    
    SUBSCRIBE(32),
    SUBSCRIBED(33),
    UNSUBSCRIBE(34),
    UNSUBSCRIBED(35),
    EVENT(36),
    
    CALL(48),
    CANCEL(49),
    RESULT(50),
    
    REGISTER(64),
    REGISTERED(65),
    UNREGISTER(66),
    UNREGISTERED(67),
    INVOCATION(68),
    INTERRUPT(69),
    YIELD(70);

    private final long code;

    MessageCode( long code ) {
        this.code = code;
    }
    
    public long getValue()  {
        return code;
    }

    public static MessageCode of( long code ) {
        for ( MessageCode messageCode : MessageCode.values() ) {
            if ( messageCode.code == code ) {
                return messageCode;
            }
        }
        throw new IllegalArgumentException( "Unknown message code " + code );
    }
}
