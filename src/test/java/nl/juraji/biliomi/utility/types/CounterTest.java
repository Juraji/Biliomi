package nl.juraji.biliomi.utility.types;

import org.junit.Test;

import java.util.stream.IntStream;

import static org.junit.Assert.*;

/**
 * Created by Juraji on 24-4-2017.
 * Biliomi v3
 */
public class CounterTest {

  @Test
  public void counterTest() throws Exception {
    Counter counter = new Counter();

    assertEquals("The default Counter should start at value 0", 0, counter.get());

    IntStream.range(0, 8).forEach(i -> counter.increment());
    assertEquals("A Counter incremented 8 times should be at 8", 8, counter.get());
  }

  @Test
  public void counterTestWithInitValue() throws Exception {
    Counter counter2 = new Counter(2);

    assertEquals("The default Counter initialized with a value of 2 should start at value 2", 2, counter2.get());

    IntStream.range(0, 8).forEach(i -> counter2.increment());
    assertEquals("A Counter initialized with a value of 2 incremented 8 times should be at 10", 10, counter2.get());
  }
}