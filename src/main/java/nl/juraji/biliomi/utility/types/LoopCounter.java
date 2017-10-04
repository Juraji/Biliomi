package nl.juraji.biliomi.utility.types;

import nl.juraji.biliomi.utility.calculate.MathUtils;

/**
 * Count up to max then return to counting from zero, excludes max
 * decrement reverses count when reached zero it will start back at max - 1
 */
public class LoopCounter extends Counter {
  private int size;

  public LoopCounter(int initial) {
    this(initial, initial);
  }

  public LoopCounter(int initial, int size) {
    super(initial);
    this.size = Integer.max(0, size);
  }

  @Override
  public int increment() {
    super.increment();
    if (size > 0 && counter == size) {
      reset();
    }
    return counter;
  }

  public int decrement() {
    if (size <= 0) {
      return 0;
    }
    --counter;
    if (size > 0 && counter < 0) {
      counter = size - 1;
    }
    return counter;
  }

  public int rand() {
    counter = MathUtils.rand(size);
    return counter;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    if (counter >= size) {
      reset();
    }
    this.size = size;
  }
}
