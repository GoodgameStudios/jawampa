package ws.wamp.jawampa.roles.callee;

import ws.wamp.jawampa.ApplicationError;
import ws.wamp.jawampa.io.BaseClient;
import ws.wamp.jawampa.messages.ErrorMessage;
import ws.wamp.jawampa.messages.InvocationMessage;
import ws.wamp.jawampa.messages.handling.BaseMessageHandler;

public class InvocationMessageHandler extends BaseMessageHandler {
    private final BaseClient baseClient;
    private final FunctionMap functionMap;

    public InvocationMessageHandler( BaseClient baseClient, FunctionMap functionMap ) {
        this.baseClient = baseClient;
        this.functionMap = functionMap;
    }

    @Override
    public void onInvocation( InvocationMessage m ) {
        if ( functionMap.isRegistered( m.registrationId ) ) {
            functionMap.call( m.registrationId,
                              new Response( baseClient, m.requestId ),
                              m.arguments,
                              m.argumentsKw );
        } else {
            baseClient.scheduleMessageToRouter( new ErrorMessage( InvocationMessage.ID,
                                                                  m.requestId,
                                                                  null,
                                                                  ApplicationError.NO_SUCH_PROCEDURE,
                                                                  null,
                                                                  null ) );
        }
    }
}
