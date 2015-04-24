package ws.wamp.jawampa.roles.callee;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface RPCImplementation {
    void call( Response req, ArrayNode positionalArguments, ObjectNode keywordArguments );
}
