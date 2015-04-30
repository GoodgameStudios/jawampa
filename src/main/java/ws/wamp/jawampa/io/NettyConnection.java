package ws.wamp.jawampa.io;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.subjects.PublishSubject;
import ws.wamp.jawampa.connectionStates.HasConnectionState;
import ws.wamp.jawampa.messages.WampMessage;
import ws.wamp.jawampa.transport.WampChannelEvents;
import ws.wamp.jawampa.transport.WampClientChannelFactory;

public class NettyConnection {
    private static final Logger log = LoggerFactory.getLogger( NettyConnection.class );

    private final PublishSubject<WampMessage> messageObservable = PublishSubject.create();

    private EventLoopGroup eventLoop;
    private volatile ChannelFuture connectFuture;
    private Channel channel;

    private final WampClientChannelFactory channelFactory;

    private MySessionHandler handler;

    private final HasConnectionState stateHolder;

    public NettyConnection( WampClientChannelFactory channelFactory, HasConnectionState stateHolder ) {
        this.channelFactory = channelFactory;
        this.stateHolder = stateHolder;
    }

    public void connect() {
        eventLoop = new NioEventLoopGroup(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "WampClientEventLoop");
                t.setDaemon(true);
                return t;
            }
        });

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
                                log.debug( "TCP Connection established!" );
                                channel = f.channel();
                                connectFuture = null;
                            } else {
                                log.warn( "Connection failed" );
                                stateHolder.getInternalConnectionState().connectionFailed();
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
        channel.disconnect();
        Future<Void> terminationFuture = (Future<Void>)eventLoop.shutdownGracefully();
        terminationFuture.addListener( new GenericFutureListener<Future<Void>>() {
            @Override
            public void operationComplete( Future<Void> future ) throws Exception {
                stateHolder.getInternalConnectionState().connectionTerminated();
            }
        } );
    }

    public void sendMessage( final WampMessage msg ) {
        log.debug( "Outgoing message: " + msg );
        channel.writeAndFlush( msg );
    }

    public PublishSubject<WampMessage> getMessageObservable() {
        return messageObservable;
    }

    public Executor executor() {
        return eventLoop;
    }

    private class MySessionHandler extends SimpleChannelInboundHandler<WampMessage> {
        @Override
        protected void channelRead0( ChannelHandlerContext ctx, WampMessage msg ) throws Exception {
            log.debug( "Incoming message: " + msg );
            messageObservable.onNext( msg );
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt == WampChannelEvents.WEBSOCKET_CONN_ESTABLISHED) {
                log.debug( "Handshake complete" );
                stateHolder.getInternalConnectionState().connectionEstablished();
            } else if (evt == WampChannelEvents.WEBSOCKET_CLOSE_RECEIVED) {
                log.debug( "Connection terminated" );
                stateHolder.getInternalConnectionState().connectionTerminated();
            }
        }
    }
}
