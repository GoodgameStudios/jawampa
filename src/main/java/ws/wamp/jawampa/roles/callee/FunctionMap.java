package ws.wamp.jawampa.roles.callee;

import java.util.HashMap;
import java.util.Map;

import rx.subjects.PublishSubject;
import ws.wamp.jawampa.Response;
import ws.wamp.jawampa.internal.UriValidator;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * This class relates uris, registration IDs ands implementations.
 * 
 * @author sglimm@goodgamestudios.com
 */
public class FunctionMap implements RegistrationStateWatcher {
    private final PublishSubject<PendingRegistration> registrationsSubject;
    private final Map<String, RPCImplementation> uri2implementation;
    private final Map<RegistrationId, String> id2uri;
    private final Map<String, RegistrationFailedCallback> uri2failureCallback;

    public FunctionMap( ) {
        this.registrationsSubject = PublishSubject.create();
        uri2implementation = new HashMap<String, RPCImplementation>();
        id2uri = new HashMap<RegistrationId, String>();
        uri2failureCallback = new HashMap<String, RegistrationFailedCallback>();
    }

    public PublishSubject<PendingRegistration> getRegistrationsSubject() {
        return registrationsSubject;
    }

    public void register( String uri, RPCImplementation implementation ) {
        register( uri, implementation, new RegistrationFailedCallback() {
            @Override
            public void registrationFailed( String uri, String reason, RPCImplementation implementation ) {
                // intentionally empty
            }
        });
    }

    public void register( String uri, RPCImplementation implementation, RegistrationFailedCallback failed ) {
        if ( uri2implementation.containsKey( uri ) ) {
            throw new IllegalArgumentException( "Function " + uri + " already registered" );
        }
        UriValidator.validate( uri );
        uri2implementation.put( uri, implementation );
        registrationsSubject.onNext( new PendingRegistration( uri, this ) );
        uri2failureCallback.put( uri, failed );
    }

    @Override
    public void registrationComplete( RegistrationId registrationId, String uri ) {
        if (id2uri.containsKey( registrationId ) ) {
            throw new IllegalStateException( "Uri " + uri + " registration already completed." );
        }
        id2uri.put( registrationId, uri );
        uri2failureCallback.remove( uri );
    }

    @Override
    public void registrationFailed( String uri, String reason ) {
        if(!uri2failureCallback.containsKey( uri ) ) {
            throw new IllegalStateException( "Uri " + uri + " was not registered." );
        }
        uri2failureCallback.get( uri ).registrationFailed( uri, reason, uri2implementation.get( uri ) );
        uri2failureCallback.remove( uri );
        uri2implementation.remove( uri );
    }

    public void call( RegistrationId id, Response request, ArrayNode pos, ObjectNode kw ) {
        uri2implementation.get( id2uri.get( id ) ).call( request, pos, kw );
    }
}
