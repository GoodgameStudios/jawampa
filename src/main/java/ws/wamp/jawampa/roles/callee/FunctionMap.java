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
public class FunctionMap implements RegistrationCallback {
    private final PublishSubject<PendingRegistration> registrationsSubject;
    private final Map<String, RPCImplementation> uri2implementation;
    private final Map<Long, String> id2uri;

    public FunctionMap( ) {
        this.registrationsSubject = PublishSubject.create();
        uri2implementation = new HashMap<String, RPCImplementation>();
        id2uri = new HashMap<Long, String>();
    }

    public PublishSubject<PendingRegistration> getRegistrationsSubject() {
        return registrationsSubject;
    }

    public void register( String uri, RPCImplementation implementation ) {
        if ( uri2implementation.containsKey( uri ) ) {
            throw new IllegalArgumentException( "Function " + uri + " already registered" );
        }
        UriValidator.validate( uri );
        uri2implementation.put( uri, implementation );
        registrationsSubject.onNext( new PendingRegistration( uri, this ) );
    }

    @Override
    public void registrationComplete( long id, String uri ) {
        id2uri.put( id, uri );
    }

    public void call( long id, Response request, ArrayNode pos, ObjectNode kw ) {
        uri2implementation.get( id2uri.get( id ) ).call( request, pos, kw );
    }
}
