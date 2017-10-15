package nl.juraji.biliomi.utility.calculate;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Juraji on 15-10-2017.
 * Biliomi
 */
public class MathUtilsTest {

  @Test
  public void getOrdinal() throws Exception {
    assertEquals("1st", MathUtils.getOrdinal(1));
    assertEquals("2nd", MathUtils.getOrdinal(2));
    assertEquals("3rd", MathUtils.getOrdinal(3));
    assertEquals("4th", MathUtils.getOrdinal(4));
    assertEquals("11th", MathUtils.getOrdinal(11));
    assertEquals("21st", MathUtils.getOrdinal(21));
    assertEquals("32nd", MathUtils.getOrdinal(32));
    assertEquals("101st", MathUtils.getOrdinal(101));
    assertEquals("1003rd", MathUtils.getOrdinal(1003));
  }

  @Test
  public void calcPercentage() throws Exception {
    assertEquals(0.05, MathUtils.calcPercentage(5.0, 100.0), 0.01);
    assertEquals(0.05, MathUtils.calcPercentage(2.5, 50), 0.01);
    assertEquals(0.3, MathUtils.calcPercentage(3.0, 10.0), 0.01);
    assertEquals(0.01, MathUtils.calcPercentage(654.0, 56414.0), 0.01);
  }

  @Test
  public void isNotInRange() throws Exception {
    assertTrue(MathUtils.isNotInRange(654, 0, 10));
    assertTrue(MathUtils.isNotInRange(654, 10, 0));
    assertFalse(MathUtils.isNotInRange(5, 0, 10));
  }

  @Test
  public void doubleToPercentage() throws Exception {
    assertEquals("33%", MathUtils.doubleToPercentage(0.3333333));
    assertEquals("66%", MathUtils.doubleToPercentage(0.6666666));
    assertEquals("21%", MathUtils.doubleToPercentage(0.213685));
    assertEquals("21%", MathUtils.doubleToPercentage(0.215685));
  }

  @Test
  public void getGcd() throws Exception {
    assertEquals(100, MathUtils.getGcd(100, 2000));
    assertEquals(100, MathUtils.getGcd(2000, 100));
    assertEquals(53, MathUtils.getGcd(53, 535353));
  }

  @Test
  public void asFraction() throws Exception {
    assertEquals("1/20", MathUtils.asFraction(100, 2000));
    assertEquals("20/1", MathUtils.asFraction(2000, 100));
    assertEquals("1/10101", MathUtils.asFraction(53, 535353));
  }

  @Test
  public void minMax() throws Exception {
    assertEquals(5, MathUtils.minMax(5, 0, 10));
    assertEquals(2, MathUtils.minMax(1, 2, 10));
    assertEquals(10, MathUtils.minMax(131, 2, 10));
  }

  @Test
  public void compareNumbers() throws Exception {
    assertTrue(MathUtils.compareNumbers(1, 1, 1, 1, 1));
    assertFalse(MathUtils.compareNumbers(1, 1, 1, 2, 1));
    assertFalse(MathUtils.compareNumbers(1, 2, 1, 1, 1));
  }

  @Test
  public void compareGroupedNumbers() throws Exception {
    Number[] group1 = new Number[]{1, 2, 3};
    Number[] group2 = new Number[]{2, 2, 3};
    Number[] group3 = new Number[]{2, 2, 2};

    // No group is expected to match, so the output should be 0
    assertEquals(0, MathUtils.compareGroupedNumbers(group1, group1, group1));

    // Group 3 is expected to be matched, so the output should be 3
    assertEquals(3, MathUtils.compareGroupedNumbers(group1, group2, group3));
  }

  @Test
  public void isPlural() throws Exception {
    assertTrue(MathUtils.isPlural(0));
    assertFalse(MathUtils.isPlural(1));
    assertTrue(MathUtils.isPlural(56));
  }

}