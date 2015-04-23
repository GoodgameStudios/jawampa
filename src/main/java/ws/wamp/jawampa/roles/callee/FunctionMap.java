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
    private final PublishSubject<PendingUnregistration> unregistrationsSubject;

    private final Map<String, RPCImplementation> uri2implementation;
    private final Map<RegistrationId, String> id2uri;
    private final Map<String, RegistrationFailedCallback> uri2registrationFailureCallback;
    private final Map<String, UnregistrationFailedCallback> uri2unregistrationFailureCallback;

    public FunctionMap( ) {
        this.registrationsSubject = PublishSubject.create();
        this.unregistrationsSubject = PublishSubject.create();

        uri2implementation = new HashMap<String, RPCImplementation>();
        id2uri = new HashMap<RegistrationId, String>();
        uri2registrationFailureCallback = new HashMap<String, RegistrationFailedCallback>();
        uri2unregistrationFailureCallback = new HashMap<String, UnregistrationFailedCallback>();
    }

    public PublishSubject<PendingRegistration> getRegistrationsSubject() {
        return registrationsSubject;
    }

    public PublishSubject<PendingUnregistration> getUnregistrationsSubject() {
        return unregistrationsSubject;
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
        uri2registrationFailureCallback.put( uri, failed );
        registrationsSubject.onNext( new PendingRegistration( uri, this ) );
    }

    public void unregister( String uri ) {
        unregister( uri, new UnregistrationFailedCallback() {
            @Override
            public void unregistrationFailed( String uri, String reason ) {
                // intentionally empty
            }
        });
    }

    public void unregister( String uri, UnregistrationFailedCallback failed ) {
        if ( !uri2implementation.containsKey( uri ) ) {
            throw new IllegalArgumentException( "Function " + uri + " not registered" );
        }
        uri2unregistrationFailureCallback.put( uri, failed );
        unregistrationsSubject.onNext( new PendingUnregistration( uri, this ) );
    }

    @Override
    public void registrationComplete( RegistrationId registrationId, String uri ) {
        if (id2uri.containsKey( registrationId ) ) {
            throw new IllegalStateException( "Uri " + uri + " registration already completed." );
        }
        id2uri.put( registrationId, uri );
        uri2registrationFailureCallback.remove( uri );
    }

    @Override
    public void registrationFailed( String uri, String reason ) {
        if(!uri2registrationFailureCallback.containsKey( uri ) ) {
            throw new IllegalStateException( "Uri " + uri + " was not registered." );
        }
        uri2registrationFailureCallback.get( uri ).registrationFailed( uri, reason, uri2implementation.get( uri ) );
        uri2registrationFailureCallback.remove( uri );
        uri2implementation.remove( uri );
    }

    @Override
    public void unregistrationComplete( RegistrationId registrationId, String uri ) {
        uri2implementation.remove( uri );
        id2uri.remove( registrationId );
        uri2unregistrationFailureCallback.remove( uri );
    }

    @Override
    public void unregistrationFailed( String uri, String reason ) {
        uri2unregistrationFailureCallback.get( uri ).unregistrationFailed( uri, reason );
        uri2unregistrationFailureCallback.remove( uri );
    }

    public void call( RegistrationId registrationId, Response request, ArrayNode pos, ObjectNode kw ) {
        if( id2uri.containsKey( registrationId ) ) {
            uri2implementation.get( id2uri.get( registrationId ) ).call( request, pos, kw );
        } else {
            throw new IllegalStateException( "No function registered for registration id " + registrationId );
        }
    }
}
