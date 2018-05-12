package nl.juraji.biliomi.utility.events.interceptors;

import nl.juraji.biliomi.utility.events.EventBus;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 * Created by Juraji on 24-4-2017.
 * Biliomi v3
 */
@Interceptor
@EventBusSubscriber
public class EventBusSubscriberInterceptor {

    @Inject
    private EventBus eventBus;

    /**
     * Register beans, annotated with @EventBusSubscriber to the EventBus before PostConstruct
     *
     * @param ctx The invocation context
     * @return The interceptor proceed command
     * @throws Exception When an error occurs while registering the bean
     */
    @PostConstruct
    public Object registerObject(InvocationContext ctx) throws Exception {
        eventBus.register(ctx.getTarget());
        return ctx.proceed();
    }

    /**
     * Unregister beans, annotated with @EventBusSubscriber to the EventBus before PreDestroy
     *
     * @param ctx The invocation context
     * @return The interceptor proceed command
     * @throws Exception When an error occurs while registering the bean
     */
    @PreDestroy
    public Object unregisterObject(InvocationContext ctx) throws Exception {
        eventBus.unregister(ctx.getTarget());
        return ctx.proceed();
    }
}
