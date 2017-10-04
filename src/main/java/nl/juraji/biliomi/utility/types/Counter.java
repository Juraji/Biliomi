package nl.juraji.biliomi.utility.types;

/**
 * Counts using increment from zero or an other initial value
 */
public class Counter extends Number {
  protected int counter;

  public Counter() {
    counter = 0;
  }

  @Override
  public int intValue() {
    return counter;
  }

  @Override
  public long longValue() {
    return counter;
  }

  @Override
  public float floatValue() {
    return counter;
  }

  @Override
  public double doubleValue() {
    return counter;
  }

  public Counter(int initial) {
    this.counter = initial;
  }

  public int increment() {
    return ++counter;
  }

  public int get() {
    return counter;
  }

  public void set(int value) {
    counter = value;
  }

  public boolean islesserThan(int compare) {
    return counter < compare;
  }

  public boolean isMoreThan(int compare) {
    return counter > compare;
  }

  public boolean is(int compare) {
    return counter == compare;
  }

  public void reset() {
    counter = 0;
  }

  @Override
  public String toString() {
    return "Counter{counter=" + counter + '}';
  }
}
