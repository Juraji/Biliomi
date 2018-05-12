package nl.juraji.biliomi.utility.types;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Counts using increment from zero or an other initial value
 */
public class Counter extends AtomicInteger {

    public Counter() {
    }

    public Counter(int initialValue) {
        super(initialValue);
    }

    public boolean islesserThan(int compare) {
        return this.intValue() < compare;
    }

    public boolean isMoreThan(int compare) {
        return this.intValue() > compare;
    }

    public boolean is(int compare) {
        return this.intValue() == compare;
    }

    public void reset() {
        set(0);
    }
}
