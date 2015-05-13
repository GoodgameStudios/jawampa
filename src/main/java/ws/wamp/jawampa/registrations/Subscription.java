package ws.wamp.jawampa.registrations;

import java.util.Objects;
import java.util.concurrent.Executor;

import rx.Observer;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import ws.wamp.jawampa.PubSubData;
import ws.wamp.jawampa.roles.Subscriber;

public class Subscription {
    private final Executor executor;
    private final Subscriber subscriber;
    private final String topic;

    private Subscription( Executor executor, Subscriber subscriber, String topic ) {
        this.executor = Objects.requireNonNull( executor );
        this.subscriber = Objects.requireNonNull( subscriber );
        this.topic = Objects.requireNonNull( topic );
    }

    public void unsubscribe() {
        final PublishSubject<Void> resultSubject = PublishSubject.create();

        executor.execute( new Runnable() {
            @Override
            public void run() {
                subscriber.unsubscribe( topic, resultSubject );
            }
        });
    }

    public static class Builder {
        private final Executor executor;
        private final Subscriber subscriber;
        private String topic;

        private Action1<Throwable> onError;
        private Action0 onFinished;
        private Action1<PubSubData> onEvent;

        public Builder( Executor executor, Subscriber subscriber, String topic ) {
            this.executor = Objects.requireNonNull( executor );
            this.subscriber = Objects.requireNonNull( subscriber );
            this.topic = Objects.requireNonNull( topic );
        }

        public Builder onError( Action1<Throwable> onError ) {
            this.onError = Objects.requireNonNull( onError );
            return this;
        }

        public Builder onFinished( Action0 onFinished ) {
            this.onFinished = Objects.requireNonNull( onFinished );
            return this;
        }

        public Builder onEvent( Action1<PubSubData> onEvent ) {
            this.onEvent = Objects.requireNonNull( onEvent );
            return this;
        }

        public Subscription subscribe() {
            if ( onError == null || onFinished == null || onEvent == null ) {
                throw new IllegalStateException( "You must call each of onError, onFinished and onEvent before calling register!" );
            }
            final PublishSubject<PubSubData> resultSubject = PublishSubject.create();

            resultSubject.subscribe( new Observer<PubSubData>() {
                @Override
                public void onCompleted() {
                    onFinished.call();
                }

                @Override
                public void onError( Throwable e ) {
                    onError.call( e );
                }

                @Override
                public void onNext( PubSubData request ) {
                    onEvent.call( request );
                }
            } );

            executor.execute( new Runnable() {
                @Override
                public void run() {
                    subscriber.subscribe( topic, resultSubject );
                }
            });

            return new Subscription( executor, subscriber, topic );
        }
    }
}
