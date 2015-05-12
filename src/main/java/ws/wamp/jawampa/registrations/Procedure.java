package ws.wamp.jawampa.registrations;

import java.util.Objects;
import java.util.concurrent.Executor;

import rx.Observer;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import ws.wamp.jawampa.Request;
import ws.wamp.jawampa.roles.Callee;

public class Procedure {
    private final Executor executor;
    private final Callee callee;
    private final String procedure;

    private Procedure( Executor executor, Callee callee, String procedure ) {
        this.executor = Objects.requireNonNull( executor );
        this.callee = Objects.requireNonNull( callee );
        this.procedure = Objects.requireNonNull( procedure );
    }

    public void unsubscribe() {
        final PublishSubject<Void> resultSubject = PublishSubject.create();

        executor.execute( new Runnable() {
            @Override
            public void run() {
                callee.unregister( procedure, resultSubject );
            }
        });
    }

    public static class Builder {
        private final Executor executor;
        private final Callee callee;
        private String procedure;

        private Action1<Throwable> onError;
        private Action0 onFinished;
        private Action1<Request> onInvocation;

        public Builder( Executor executor, Callee callee, String procedure ) {
            this.executor = Objects.requireNonNull( executor );
            this.callee = Objects.requireNonNull( callee );
            this.procedure = Objects.requireNonNull( procedure );
        }

        public Builder onError( Action1<Throwable> onError ) {
            this.onError = Objects.requireNonNull( onError );
            return this;
        }

        public Builder onFinished( Action0 onFinished ) {
            this.onFinished = Objects.requireNonNull( onFinished );
            return this;
        }

        public Builder onInvocation( Action1<Request> onInvocation ) {
            this.onInvocation = Objects.requireNonNull( onInvocation );
            return this;
        }

        public Procedure register() {
            if ( onError == null || onFinished == null || onInvocation == null ) {
                throw new IllegalStateException( "You must call each of onError, onFinished and onInvocation before calling register!" );
            }
            final PublishSubject<Request> resultSubject = PublishSubject.create();

            resultSubject.subscribe( new Observer<Request>() {
                @Override
                public void onCompleted() {
                    onFinished.call();
                }

                @Override
                public void onError( Throwable e ) {
                    onError.call( e );
                }

                @Override
                public void onNext( Request request ) {
                    onInvocation.call( request );
                }
            } );

            executor.execute( new Runnable() {
                @Override
                public void run() {
                    callee.register( procedure, resultSubject );
                }
            });

            return new Procedure( executor, callee, procedure );
        }
    }
}
