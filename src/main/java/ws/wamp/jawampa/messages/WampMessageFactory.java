package ws.wamp.jawampa.messages;

import ws.wamp.jawampa.WampError;

import com.fasterxml.jackson.databind.node.ArrayNode;

public interface WampMessageFactory {
    public WampMessage fromObjectArray(ArrayNode messageNode) throws WampError;
}