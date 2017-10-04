package nl.juraji.biliomi.utility.types.collections;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Juraji on 12-5-2017.
 * Biliomi v3
 */
public class FastListTest {

  private List<String> list;

  @Before
  public void setUp() throws Exception {
    list = new FastList<>();
    list.add("Test1");
    list.add("Test2");
    list.add("Test3");
  }

  @Test
  public void contains() throws Exception {
    assertTrue(list.contains("Test1"));
    assertFalse(list.contains("XXXXX"));
  }

  @Test
  public void remove() throws Exception {
    boolean removed = list.remove("Test2");
    assertTrue(removed);
    assertTrue(list.contains("Test1") && list.contains("Test3"));
    assertFalse(list.contains("Test2"));
    assertFalse(list.remove("XXXXX"));
  }
}