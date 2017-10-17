package nl.juraji.biliomi.utility.calculate;

import org.junit.Test;

import static nl.juraji.biliomi.utility.calculate.Numbers.asNumber;
import static org.junit.Assert.*;

/**
 * Created by Juraji on 21-4-2017.
 * Biliomi v3
 */
public class NumbersTest {

  @Test
  public void withDefault() throws Exception {
    int result = asNumber("abc").withDefault(1).toInteger();
    assertEquals(1, result);
  }

  @Test
  public void toInteger() throws Exception {
    int result = asNumber("123").toInteger();
    Integer nullResult = asNumber("abc").toInteger();
    assertEquals(123, result);
    assertNull(nullResult);
  }

  @Test
  public void toDouble() throws Exception {
    double result = asNumber("123.1").toDouble();
    Double nullResult = asNumber("abc").toDouble();
    assertEquals(123.1, result, 0);
    assertNull(nullResult);
  }

  @Test
  public void toLong() throws Exception {
    long result = asNumber("123").toLong();
    Long nullResult = asNumber("abc").toLong();
    assertEquals(123, result);
    assertNull(nullResult);
  }

  @Test
  public void isNaN() throws Exception {
    boolean isNan = asNumber("abc").isNaN();
    boolean notIsNan = asNumber("123").isNaN();

    assertTrue(isNan);
    assertFalse(notIsNan);
  }

}