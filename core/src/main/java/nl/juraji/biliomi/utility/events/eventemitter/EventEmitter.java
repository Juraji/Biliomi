package nl.juraji.biliomi.utility.events.eventemitter;

import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.estreams.EBiStream;
import nl.juraji.biliomi.utility.factories.concurrent.ThreadPools;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * Created by Juraji on 21-4-2017.
 * Biliomi v3
 */
public final class EventEmitter<T> {
    private static final ExecutorService EXECUTOR = ThreadPools.newSingleThreadExecutor("SynchronousEventEmitter");
    private final Map<String, Subscriber> consumers;

    public EventEmitter() {
        consumers = new HashMap<>();
    }

    /**
     * Register a new consumer to this event emitter
     *
     * @param consumer A consumer accepting values of type T
     * @return A unique subscription id
     */
    public String subscribe(Consumer<T> consumer) {
        return subscribe(null, consumer);
    }

    /**
     * Register a new consumer to this event emitter.
     * Only runs cunsumer when the emitted object class equals the filter
     *
     * @param filter   A class (extending T) to use as filter for this consumer
     * @param consumer A consumer accepting values of type T
     * @return A unique subscription id
     */
    public String subscribe(Class<? extends T> filter, Consumer<T> consumer) {
        String subId = MathUtils.newUUID();
        consumers.put(subId, new Subscriber(filter, consumer));
        return subId;
    }

    /**
     * Unsubscribe a consumer from this event emitter
     *
     * @param subscriptionId The subscription id returned by subscribing
     */
    public void unsubscribe(String subscriptionId) {
        consumers.remove(subscriptionId);
    }

    /**
     * Emit a value to all subscribers.
     * Automatically removes subscribers that threw an exception during execution.
     *
     * @param value The value to emit
     */
    public void emit(T value) {
        EBiStream.from(this.consumers)
                .filter((id, subscriber) -> subscriber.filter == null || subscriber.filter.equals(value.getClass()))
                .forEach((id, subscriber) -> EXECUTOR.submit(() -> {
                    try {
                        subscriber.consumer.accept(value);
                    } catch (Exception e) {
                        unsubscribe(id);
                    }
                }));
    }

    /**
     * Get the amount of current subscribers
     *
     * @return An integer representing the consumer map size
     */
    public int subscriptionsSize() {
        return consumers.size();
    }

    private class Subscriber {
        final Class<?> filter;
        final Consumer<T> consumer;

        private Subscriber(Class<? extends T> filter, Consumer<T> consumer) {
            this.filter = filter;
            this.consumer = consumer;
        }
    }
}
