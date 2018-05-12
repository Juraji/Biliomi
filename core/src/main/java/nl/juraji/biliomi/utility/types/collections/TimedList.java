package nl.juraji.biliomi.utility.types.collections;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Juraji on 25-12-2017.
 * Biliomi
 */
public class TimedList<T> {
    private final AtomicInteger indexGenerator;
    private final TimedMap<Integer, T> timedMap;

    public TimedList(String name) {
        this.indexGenerator = new AtomicInteger();
        this.timedMap = new TimedMap<>(name);
    }

    public T add(T value, long timeToLive, TimeUnit timeUnit) {
        return this.timedMap.put(indexGenerator.getAndIncrement(), value, timeToLive, timeUnit);
    }

    public T remove(T value) {
        Integer key = timedMap.findKey(value);
        if (key != null) {
            return timedMap.remove(key);
        } else {
            return null;
        }
    }

    public void stop() {
        timedMap.stop();
    }

    public boolean contains(T value) {
        return timedMap.containsValue(value);
    }
}
