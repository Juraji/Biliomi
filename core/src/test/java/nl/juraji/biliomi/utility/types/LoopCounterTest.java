package nl.juraji.biliomi.utility.types;

import org.junit.Test;

import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

/**
 * Created by Juraji on 24-4-2017.
 * Biliomi v3
 */
public class LoopCounterTest {

  @Test
  public void loopCounterTest() throws Exception {
    LoopCounter counter = new LoopCounter(0, 3);

    assertEquals("A LoopCounter with initial at 0 should be equal to 0", 0, counter.get());

    IntStream.range(0, 8).forEach(i -> counter.increment());
    assertEquals("A LoopCounter with initial at 0 and size set to 3 incremented 8 times should be equal to 2", 2, counter.get());

    IntStream.range(0, 4).forEach(i -> counter.decrement());
    assertEquals("A LoopCounter with initial at 2 and size set to 3 decremented 4 times should be equal to 1", 1, counter.get());

    counter.setSize(5);
    IntStream.range(0, 4).forEach(i -> counter.decrement());
    assertEquals("A LoopCounter with initial at 1 and size changed to 5 decremented 4 times should be equal to 2", 2, counter.get());

    IntStream.range(0, 8).forEach(i -> counter.increment());
    assertEquals("A LoopCounter with initial at 2 and size set to 5 incremented 8 times should be equal to 0", 0, counter.get());

    counter.reset();
    assertEquals("Resetting the LoopCounter should set it back to 0", 0, counter.get());
  }
}