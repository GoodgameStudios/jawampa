package ws.wamp.jawampa.messages;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.WampError;
import ws.wamp.jawampa.ids.RequestId;
import ws.wamp.jawampa.messages.handling.MessageHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Error reply sent by a Peer as an error response to different kinds of
 * requests. Possible formats: [ERROR, REQUEST.Type|int, REQUEST.Request|id,
 * Details|dict, Error|uri] [ERROR, REQUEST.Type|int, REQUEST.Request|id,
 * Details|dict, Error|uri, Arguments|list] [ERROR, REQUEST.Type|int,
 * REQUEST.Request|id, Details|dict, Error|uri, Arguments|list,
 * ArgumentsKw|dict]
 */
public class ErrorMessage extends WampMessage {
    public final static int ID = 8;
    public final int requestType;
    public final RequestId requestId;
    public final ObjectNode details;
    public final String error;
    public final ArrayNode arguments;
    public final ObjectNode argumentsKw;

    public ErrorMessage(int requestType, RequestId requestId,
            ObjectNode details, String error, ArrayNode arguments,
            ObjectNode argumentsKw) {
        this.requestType = requestType;
        this.requestId = requestId;
        this.details = details;
        this.error = error;
        this.arguments = arguments;
        this.argumentsKw = argumentsKw;
    }

    public JsonNode toObjectArray(ObjectMapper mapper) throws WampError {
        ArrayNode messageNode = mapper.createArrayNode();
        messageNode.add(ID);
        messageNode.add(requestType);
        messageNode.add(requestId.getValue());
        if (details != null)
            messageNode.add(details);
        else
            messageNode.add(mapper.createObjectNode());
        messageNode.add(error.toString());
        if (arguments != null)
            messageNode.add(arguments);
        else if (argumentsKw != null)
            messageNode.add(mapper.createArrayNode());
        if (argumentsKw != null)
            messageNode.add(argumentsKw);
        return messageNode;
    }

    static class Factory implements WampMessageFactory {
        @Override
        public WampMessage fromObjectArray(ArrayNode messageNode) throws WampError {
            if (messageNode.size() < 5 || messageNode.size() > 7
                    || !messageNode.get(1).canConvertToInt()
                    || !messageNode.get(2).canConvertToLong()
                    || !messageNode.get(3).isObject()
                    || !messageNode.get(4).isTextual())
                throw new WampError(ApplicationError.INVALID_MESSAGE);

            int requestType = messageNode.get(1).asInt();
            long requestId = messageNode.get(2).asLong();
            ObjectNode details = (ObjectNode) messageNode.get(3);
            String error = messageNode.get(4).asText();
            ArrayNode arguments = null;
            ObjectNode argumentsKw = null;

            if (messageNode.size() >= 6) {
                if (!messageNode.get(5).isArray())
                    throw new WampError(ApplicationError.INVALID_MESSAGE);
                arguments = (ArrayNode) messageNode.get(5);
                if (messageNode.size() >= 7) {
                    if (!messageNode.get(6).isObject())
                        throw new WampError(ApplicationError.INVALID_MESSAGE);
                    argumentsKw = (ObjectNode) messageNode.get(6);
                }
            }

            return new ErrorMessage(requestType, RequestId.of( requestId ), details, error,
                    arguments, argumentsKw);
        }
    }


    @Override
    public void onMessage( MessageHandler messageHandler ) {
        // FIXME: Implement
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( arguments == null ) ? 0 : arguments.hashCode() );
        result = prime * result + ( ( argumentsKw == null ) ? 0 : argumentsKw.hashCode() );
        result = prime * result + ( ( details == null ) ? 0 : details.hashCode() );
        result = prime * result + ( ( error == null ) ? 0 : error.hashCode() );
        result = prime * result + ( ( requestId == null ) ? 0 : requestId.hashCode() );
        result = prime * result + requestType;
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        ErrorMessage other = (ErrorMessage)obj;
        if ( arguments == null ) {
            if ( other.arguments != null )
                return false;
        } else if ( !arguments.equals( other.arguments ) )
            return false;
        if ( argumentsKw == null ) {
            if ( other.argumentsKw != null )
                return false;
        } else if ( !argumentsKw.equals( other.argumentsKw ) )
            return false;
        if ( details == null ) {
            if ( other.details != null )
                return false;
        } else if ( !details.equals( other.details ) )
            return false;
        if ( error == null ) {
            if ( other.error != null )
                return false;
        } else if ( !error.equals( other.error ) )
            return false;
        if ( requestId == null ) {
            if ( other.requestId != null )
                return false;
        } else if ( !requestId.equals( other.requestId ) )
            return false;
        if ( requestType != other.requestType )
            return false;
        return true;
    }
}