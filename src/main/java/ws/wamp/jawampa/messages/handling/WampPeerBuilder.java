package ws.wamp.jawampa.messages.handling;

public class WampPeerBuilder {
    private MessageHandler broker;
    private MessageHandler dealer;

    private MessageHandler caller;
    private MessageHandler callee;

    private MessageHandler subscriber;
    private MessageHandler publisher;

    private MessageHandler handshakingClient;
    private MessageHandler handshakingRouter;

    public WampPeer build() {
        return new WampPeer( broker, dealer,
                             caller, callee,
                             subscriber, publisher,
                             handshakingClient, handshakingRouter );
    }

    public WampPeerBuilder withBroker( MessageHandler broker ) {
        this.broker = broker;
        return this;
    }

    public WampPeerBuilder withDealer( MessageHandler dealer ) {
        this.dealer = dealer;
        return this;
    }

    public WampPeerBuilder withCaller( MessageHandler caller ) {
        this.caller = caller;
        return this;
    }

    public WampPeerBuilder withCallee( MessageHandler callee ) {
        this.callee = callee;
        return this;
    }

    public WampPeerBuilder withSubscriber( MessageHandler subscriber ) {
        this.subscriber = subscriber;
        return this;
    }

    public WampPeerBuilder withPublisher( MessageHandler publisher ) {
        this.publisher = publisher;
        return this;
    }

    public WampPeerBuilder withHandshakingClient( MessageHandler handshakingClient ) {
        this.handshakingClient = handshakingClient;
        return this;
    }

    public WampPeerBuilder withHandshakingRouter( MessageHandler handshakingRouter ) {
        this.handshakingRouter = handshakingRouter;
        return this;
    }
}
