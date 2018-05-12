package nl.juraji.biliomi.utility.types.collections;

import nl.juraji.biliomi.utility.estreams.EBiStream;
import nl.juraji.biliomi.utility.factories.concurrent.DefaultThreadFactory;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * Created by Juraji on 14-5-2017.
 * Biliomi v3
 */
public class TimedMap<K, V> {
    private static final String THREAD_SUFFIX = "TimedMapEvictionTimer";
    private static final AtomicInteger SERIAL_GENERATOR = new AtomicInteger();

    private final ConcurrentMap<K, ExpiringObject> map;
    private final Lock writeLock;
    private final Timer timer;

    public TimedMap(Class cls) {
        this(cls.getSimpleName());
    }

    public TimedMap(String name) {
        map = new ConcurrentHashMap<>();
        writeLock = new ReentrantLock();
        timer = new Timer(DefaultThreadFactory.threadNameBuilder(name + '-' + THREAD_SUFFIX, SERIAL_GENERATOR.getAndIncrement()), true);
    }

    /**
     * Add or overwrite an entry, specifying a time to live
     *
     * @param key        A unique identifier
     * @param value      The value
     * @param timeToLive The time to live in milliseconds
     */
    public void put(K key, V value, long timeToLive) {
        put(key, value, timeToLive, TimeUnit.MILLISECONDS);
    }

    /**
     * Add or overwrite an entry, specifying a time to live
     *
     * @param key        A unique identifier
     * @param value      The value
     * @param timeToLive The time to live
     * @param timeUnit   The time unit of the timeToLive parameter
     * @return The value that has been set
     */
    public V put(K key, V value, long timeToLive, TimeUnit timeUnit) {
        final ExpiringObject object;

        try {
            writeLock.lock();

            long millsTtl = TimeUnit.MILLISECONDS.convert(timeToLive, timeUnit);
            object = map.put(key, new ExpiringObject(key, value, millsTtl));
            if (object != null) {
                object.getTask().cancel();
            }
        } finally {
            writeLock.unlock();
        }

        return (object != null ? object.getValue() : null);
    }

    /**
     * Add or update an existing entry
     *
     * @param key         The unique identifier
     * @param valueMapper A mapping Function
     *                    param V: The current value or null or null on new entry
     *                    return V: The new or updated value
     * @param timeToLive  The time to live on new entry in milliseconds.
     * @return The value that has been set
     */
    public V putOrUpdate(K key, Function<V, V> valueMapper, long timeToLive) {
        return putOrUpdate(key, valueMapper, timeToLive, TimeUnit.MILLISECONDS);
    }

    /**
     * Add or update an existing entry
     *
     * @param key         The unique identifier
     * @param valueMapper A mapping Function
     *                    param V: The current value or null on new entry
     *                    return V: The new or updated value
     * @param timeToLive  The time to live on new entry.
     * @param sourceUnit  The time unit of the timeToLive parameter
     * @return The value that has been set
     */
    public V putOrUpdate(K key, Function<V, V> valueMapper, long timeToLive, TimeUnit sourceUnit) {
        final V value;

        try {
            writeLock.lock();

            if (map.containsKey(key)) {
                final ExpiringObject expiringObject = map.get(key);
                value = valueMapper.apply(expiringObject.getValue());
                expiringObject.setValue(value);
            } else {
                value = valueMapper.apply(null);
                put(key, value, timeToLive, sourceUnit);
            }

        } finally {
            writeLock.unlock();
        }

        return value;
    }

    /**
     * Get an entry
     *
     * @param key The unique identifier
     * @return The value associated with the identifier or null if it does not exist
     */
    public V get(K key) {
        if (map.containsKey(key)) {
            return map.get(key).getValue();
        }
        return null;
    }

    /**
     * Remove an entry
     *
     * @param key The unique identifier
     * @return The value associated with the identifier or null if it does not exist
     */
    public V remove(K key) {
        final ExpiringObject object;

        try {
            writeLock.lock();

            object = map.remove(key);
            if (object != null) {
                object.getTask().cancel();
            }
        } finally {
            writeLock.unlock();
        }

        return (object == null ? null : object.getValue());
    }

    /**
     * Verify if an entry is present
     *
     * @param key The unique identifier
     * @return True if it is present else False
     */
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    public boolean containsValue(V value) {
        return findKey(value) != null;
    }

    public K findKey(V value) {
        if (isEmpty()) {
            return null;
        }

        try {
            writeLock.lock();
            return EBiStream.from(map)
                    .mapValue(ExpiringObject::getValue)
                    .filterValue(Objects::nonNull)
                    .filterValue(v -> v.equals(value))
                    .map((k, v) -> k)
                    .findFirst()
                    .orElse(null);
        } finally {
            writeLock.unlock();
        }
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public int size() {
        return map.size();
    }

    /**
     * Clear all entries
     */
    public void clear() {
        try {
            writeLock.lock();

            map.values().forEach(expiringObject -> expiringObject.getTask().cancel());
            map.clear();
            timer.purge();
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Stop execution of eviction timer
     */
    public void stop() {
        timer.purge();
        timer.cancel();
    }

    private class ExpiringObject {

        private final ExpiryTask task;
        private V value;

        public ExpiringObject(K key, V value, long ttl) {
            this.value = value;
            this.task = new ExpiryTask(key);
            timer.schedule(task, ttl);
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public ExpiryTask getTask() {
            return task;
        }
    }

    private class ExpiryTask extends TimerTask {
        private final K key;

        private ExpiryTask(K key) {
            this.key = key;
        }

        public K getKey() {
            return key;
        }

        @Override
        public void run() {
            try {
                writeLock.lock();
                map.remove(key);
            } finally {
                writeLock.unlock();
            }
        }
    }
}
