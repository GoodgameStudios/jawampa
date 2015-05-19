package ws.wamp.jawampa.messages.handling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ws.wamp.jawampa.messages.AbortMessage;
import ws.wamp.jawampa.messages.AuthenticateMessage;
import ws.wamp.jawampa.messages.CallMessage;
import ws.wamp.jawampa.messages.ChallengeMessage;
import ws.wamp.jawampa.messages.ErrorMessage;
import ws.wamp.jawampa.messages.EventMessage;
import ws.wamp.jawampa.messages.GoodbyeMessage;
import ws.wamp.jawampa.messages.HelloMessage;
import ws.wamp.jawampa.messages.InvocationMessage;
import ws.wamp.jawampa.messages.PublishMessage;
import ws.wamp.jawampa.messages.PublishedMessage;
import ws.wamp.jawampa.messages.RegisterMessage;
import ws.wamp.jawampa.messages.RegisteredMessage;
import ws.wamp.jawampa.messages.ResultMessage;
import ws.wamp.jawampa.messages.SubscribeMessage;
import ws.wamp.jawampa.messages.SubscribedMessage;
import ws.wamp.jawampa.messages.UnregisterMessage;
import ws.wamp.jawampa.messages.UnregisteredMessage;
import ws.wamp.jawampa.messages.UnsubscribeMessage;
import ws.wamp.jawampa.messages.UnsubscribedMessage;
import ws.wamp.jawampa.messages.WelcomeMessage;
import ws.wamp.jawampa.messages.YieldMessage;

/**
 *
 * @author hkraemer@ggs-hh.net
 */
public class CompositeMessageHandler implements MessageHandler {
    private final List<MessageHandler> subHandlers;

    private CompositeMessageHandler( List<MessageHandler> subHandlers ) {
        this.subHandlers = subHandlers;
    }
    
    public static CompositeMessageHandler withSubHandlers( List<MessageHandler> subHandlers ) {
        return new CompositeMessageHandler( new ArrayList<MessageHandler>( subHandlers ) );
    }
    
    public static CompositeMessageHandler withSubHandlers( MessageHandler... subHandlers ) {
        return withSubHandlers( Arrays.asList( subHandlers ) );
    }
    
    @Override
    public void onHello(HelloMessage msg) {
        for (MessageHandler handler : subHandlers) {
            try {
                handler.onHello(msg);
            } catch (MessageNotHandledHereException e) {
                // ignore
            }
        }
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onChallenge(ChallengeMessage msg) {
        for (MessageHandler handler : subHandlers) {
            try {
                handler.onChallenge(msg);
            } catch (MessageNotHandledHereException e) {
                // ignore
            }
        }
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onAuthenticate(AuthenticateMessage msg) {
        for (MessageHandler handler : subHandlers) {
            try {
                handler.onAuthenticate(msg);
            } catch (MessageNotHandledHereException e) {
                // ignore
            }
        }
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onWelcome(WelcomeMessage msg) {
        for (MessageHandler handler : subHandlers) {
            try {
                handler.onWelcome(msg);
            } catch (MessageNotHandledHereException e) {
                // ignore
            }
        }
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onAbort(AbortMessage msg) {
        for (MessageHandler handler : subHandlers) {
            try {
                handler.onAbort(msg);
            } catch (MessageNotHandledHereException e) {
                // ignore
            }
        }
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onGoodbye(GoodbyeMessage msg) {
        for (MessageHandler handler : subHandlers) {
            try {
                handler.onGoodbye(msg);
            } catch (MessageNotHandledHereException e) {
                // ignore
            }
        }
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onSubscribe(SubscribeMessage msg) {
        for (MessageHandler handler : subHandlers) {
            try {
                handler.onSubscribe(msg);
            } catch (MessageNotHandledHereException e) {
                // ignore
            }
        }
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onSubscribed(SubscribedMessage msg) {
        for (MessageHandler handler : subHandlers) {
            try {
                handler.onSubscribed(msg);
            } catch (MessageNotHandledHereException e) {
                // ignore
            }
        }
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onSubscribeError(ErrorMessage msg) {
        for (MessageHandler handler : subHandlers) {
            try {
                handler.onSubscribeError(msg);
            } catch (MessageNotHandledHereException e) {
                // ignore
            }
        }
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onUnsubscribe(UnsubscribeMessage msg) {
        for (MessageHandler handler : subHandlers) {
            try {
                handler.onUnsubscribe(msg);
            } catch (MessageNotHandledHereException e) {
                // ignore
            }
        }
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onUnsubscribed(UnsubscribedMessage msg) {
        for (MessageHandler handler : subHandlers) {
            try {
                handler.onUnsubscribed(msg);
            } catch (MessageNotHandledHereException e) {
                // ignore
            }
        }
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onUnsubscribeError(ErrorMessage msg) {
        for (MessageHandler handler : subHandlers) {
            try {
                handler.onUnsubscribeError(msg);
            } catch (MessageNotHandledHereException e) {
                // ignore
            }
        }
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onPublish(PublishMessage msg) {
        for (MessageHandler handler : subHandlers) {
            try {
                handler.onPublish(msg);
            } catch (MessageNotHandledHereException e) {
                // ignore
            }
        }
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onPublished(PublishedMessage msg) {
        for (MessageHandler handler : subHandlers) {
            try {
                handler.onPublished(msg);
            } catch (MessageNotHandledHereException e) {
                // ignore
            }
        }
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onEvent(EventMessage msg) {
        for (MessageHandler handler : subHandlers) {
            try {
                handler.onEvent(msg);
            } catch (MessageNotHandledHereException e) {
                // ignore
            }
        }
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onPublishError(ErrorMessage msg) {
        for (MessageHandler handler : subHandlers) {
            try {
                handler.onPublishError(msg);
            } catch (MessageNotHandledHereException e) {
                // ignore
            }
        }
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onRegister(RegisterMessage msg) {
        for (MessageHandler handler : subHandlers) {
            try {
                handler.onRegister(msg);
            } catch (MessageNotHandledHereException e) {
                // ignore
            }
        }
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onRegistered(RegisteredMessage msg) {
        for (MessageHandler handler : subHandlers) {
            try {
                handler.onRegistered(msg);
            } catch (MessageNotHandledHereException e) {
                // ignore
            }
        }
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onRegisterError(ErrorMessage msg) {
        for (MessageHandler handler : subHandlers) {
            try {
                handler.onRegisterError(msg);
            } catch (MessageNotHandledHereException e) {
                // ignore
            }
        }
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onUnregister(UnregisterMessage msg) {
        for (MessageHandler handler : subHandlers) {
            try {
                handler.onUnregister(msg);
            } catch (MessageNotHandledHereException e) {
                // ignore
            }
        }
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onUnregistered(UnregisteredMessage msg) {
        for (MessageHandler handler : subHandlers) {
            try {
                handler.onUnregistered(msg);
            } catch (MessageNotHandledHereException e) {
                // ignore
            }
        }
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onUnregisterError(ErrorMessage msg) {
        for (MessageHandler handler : subHandlers) {
            try {
                handler.onUnregisterError(msg);
            } catch (MessageNotHandledHereException e) {
                // ignore
            }
        }
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onCall(CallMessage msg) {
        for (MessageHandler handler : subHandlers) {
            try {
                handler.onCall(msg);
            } catch (MessageNotHandledHereException e) {
                // ignore
            }
        }
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onInvocation(InvocationMessage msg) {
        for (MessageHandler handler : subHandlers) {
            try {
                handler.onInvocation(msg);
            } catch (MessageNotHandledHereException e) {
                // ignore
            }
        }
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onYield(YieldMessage msg) {
        for (MessageHandler handler : subHandlers) {
            try {
                handler.onYield(msg);
            } catch (MessageNotHandledHereException e) {
                // ignore
            }
        }
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onResult(ResultMessage msg) {
        for (MessageHandler handler : subHandlers) {
            try {
                handler.onResult(msg);
            } catch (MessageNotHandledHereException e) {
                // ignore
            }
        }
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onInvocationError(ErrorMessage msg) {
        for (MessageHandler handler : subHandlers) {
            try {
                handler.onInvocationError(msg);
            } catch (MessageNotHandledHereException e) {
                // ignore
            }
        }
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onCallError(ErrorMessage msg) {
        for (MessageHandler handler : subHandlers) {
            try {
                handler.onCallError(msg);
            } catch (MessageNotHandledHereException e) {
                // ignore
            }
        }
        throw new MessageNotHandledHereException();
    }

    @Override
    public void onError(ErrorMessage msg) {
        for (MessageHandler handler : subHandlers) {
            try {
                handler.onError(msg);
            } catch (MessageNotHandledHereException e) {
                // ignore
            }
        }
        throw new MessageNotHandledHereException();
    }
}
