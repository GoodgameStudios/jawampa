package ws.wamp.jawampa.io;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.concurrent.ThreadFactory;

import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import ws.wamp.jawampa.WampClient.Status;
import ws.wamp.jawampa.messages.WampMessage;
import ws.wamp.jawampa.transport.WampChannelEvents;
import ws.wamp.jawampa.transport.WampClientChannelFactory;

public class NettyConnection {
    private final BehaviorSubject<Status> statusObservable = BehaviorSubject.create( Status.DISCONNECTED );
    private final PublishSubject<WampMessage> messageObservable = PublishSubject.create();

    private final EventLoopGroup eventLoop;
    private volatile ChannelFuture connectFuture;
    private Channel channel;

    private final WampClientChannelFactory channelFactory;

    private MySessionHandler handler;

    public NettyConnection( WampClientChannelFactory channelFactory ) {
        this.eventLoop = new NioEventLoopGroup(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "WampClientEventLoop");
                t.setDaemon(true);
                return t;
            }
        });

        this.channelFactory = channelFactory;
    }

    public void connect() {
        synchronized(statusObservable) {
            if ( statusObservable.getValue() != Status.DISCONNECTED ) {
                throw new IllegalStateException( "Cannot connect if not disconnected first!" );
            }
            statusObservable.onNext( Status.CONNECTING );
        }
        eventLoop.execute(new Runnable() {
            @Override
            public void run() {
                handler = new MySessionHandler();
                try {
                    connectFuture = channelFactory.createChannel(handler, eventLoop);
                    connectFuture.addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture f) throws Exception {
                            if (f.isSuccess()) {
                                channel = f.channel();
                                connectFuture = null;
                            } else {
                                synchronized( statusObservable ) {
                                    statusObservable.onNext( Status.DISCONNECTED );
                                }
                            }
                        }
                    });
                } catch ( Exception e ) {
                    //FIXME
                }
            }
        });
    }

    public void disconnect() {
        synchronized( statusObservable ) {
            if ( statusObservable.getValue() != Status.CONNECTED ) {
                throw new IllegalStateException( "Cannot disconnect if not connected first!" );
            }
            statusObservable.onNext( Status.DISCONNECTED );
        }
        channel.disconnect();
    }

    public void sendMessage( final WampMessage msg ) {
        eventLoop.execute( new Runnable() {
            @Override
            public void run() {
                channel.writeAndFlush( msg );
            }
        });
    }

    public BehaviorSubject<Status> getStatusObservable() {
        return statusObservable;
    }

    public PublishSubject<WampMessage> getMessageObservable() {
        return messageObservable;
    }

    private class MySessionHandler extends SimpleChannelInboundHandler<WampMessage> {
        @Override
        protected void channelRead0( ChannelHandlerContext ctx, WampMessage msg ) throws Exception {
            messageObservable.onNext( msg );
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt == WampChannelEvents.WEBSOCKET_CONN_ESTABLISHED) {
                statusObservable.onNext( Status.CONNECTED );
            } else if (evt == WampChannelEvents.WEBSOCKET_CLOSE_RECEIVED) {
                statusObservable.onNext( Status.DISCONNECTED );
            }
        }
    }
}
