package ws.wamp.jawampa.messages;

import ws.wamp.jawampa.ids.BaseId;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class MessageNodeBuilder {
    private final ObjectMapper mapper;

    private final ArrayNode arrayNode;

    public MessageNodeBuilder( ObjectMapper mapper, MessageCode messageCode ) {
        this.mapper = mapper;
        this.arrayNode = mapper.createArrayNode().add( messageCode.getValue() );
    }

    public MessageNodeBuilder add( BaseId id ) {
        this.arrayNode.add( id.getValue() );
        return this;
    }

    public MessageNodeBuilder add( MessageCode messageCode ) {
        this.arrayNode.add( messageCode.getValue() );
        return this;
    }

    public MessageNodeBuilder add( String string ) {
        this.arrayNode.add( string );
        return this;
    }

    public MessageNodeBuilder add( ObjectNode node ) {
        if ( node == null ) {
            this.arrayNode.add( mapper.createObjectNode() );
        } else {
            this.arrayNode.add( node );
        }
        return this;
    }

    public MessageNodeBuilder add( ArrayNode node ) {
        if ( node == null ) {
            this.arrayNode.add( mapper.createArrayNode() );
        } else {
            this.arrayNode.add( node );
        }
        return this;
    }

    public ArrayNode build() {
        return this.arrayNode;
    }
}
