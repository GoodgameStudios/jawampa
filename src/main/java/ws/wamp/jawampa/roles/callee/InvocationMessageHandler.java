package ws.wamp.jawampa.roles.callee;

import ws.wamp.jawampa.io.BaseClient;
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
//        RegisteredProceduresMapEntry entry = registeredProceduresById.get(m.registrationId);
//        if (entry == null || entry.state != RegistrationState.Registered) {
//            // Send an error that we are no longer registered
//            baseClient.scheduleMessageToRouter( new ErrorMessage( InvocationMessage.ID,
//                                                                  m.requestId,
//                                                                  null,
//                                                                  ApplicationError.NO_SUCH_PROCEDURE,
//                                                                  null,
//                                                                  null ) );
//        }
//        else {
//            // Send the request to the subscriber, which can then send responses
//            Request request = new Request(baseClient, m.requestId, m.arguments, m.argumentsKw);
//            entry.subscriber.onNext(request);
//        }
    }

}
