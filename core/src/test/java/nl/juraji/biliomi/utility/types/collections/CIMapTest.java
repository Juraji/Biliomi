package nl.juraji.biliomi.utility.types.collections;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by Juraji on 12-5-2017.
 * Biliomi v3
 */
public class CIMapTest {

    CIMap<String> map;

    @Before
    public void setUp() {
        map = new CIMap<>();
        map.put("test1", "Test1");

        Map<String, String> t = new HashMap<>();
        t.put("test2", "Test2");
        t.put("test3", "Test3");
        map.putAll(t);
    }

    @Test
    public void get() {
        assertEquals("Test1", map.get("test1"));
        assertEquals("Test1", map.get("TEST1"));
    }

    @Test
    public void containsKey() {
        assertTrue(map.containsKey("test1"));
        assertTrue(map.containsKey("TEST1"));
    }

    @Test
    public void remove() {
        String test1 = map.remove("test1");
        String test3 = map.remove("TEST3");

        assertEquals("Test1", test1);
        assertEquals("Test3", test3);
    }

    @Test
    public void getOrDefault() {
        assertEquals("Test1", map.getOrDefault("Test1", "FAILED"));
        assertEquals("Test1", map.getOrDefault("TEST1", "FAILED"));
        assertEquals("FAILED", map.getOrDefault("NONEXISTENT", "FAILED"));
        assertEquals("FAILED", map.getOrDefault(null, "FAILED"));
    }

    @Test
    public void putIfAbsent() {
        map.putIfAbsent("TEST1", "OTHERVALUE");
        assertEquals("Test1", map.get("test1"));
    }

    @Test
    public void remove1() {
        assertTrue(map.remove("TEST1", "Test1"));
        assertTrue(map.remove("test2", "Test2"));
    }

    @Test
    public void replace() {
        assertTrue(map.replace("TEST1", "Test1", "OtherValue1"));
        assertTrue(map.replace("test2", "Test2", "OtherValue2"));
        assertEquals("OtherValue1", map.get("test1"));
        assertEquals("OtherValue2", map.get("test2"));
    }

    @Test
    public void replace1() {
        assertNotNull(map.replace("TEST1", "OtherValue1"));
        assertNotNull(map.replace("test2", "OtherValue2"));
        assertEquals("OtherValue1", map.get("test1"));
        assertEquals("OtherValue2", map.get("test2"));
    }

    @Test
    public void computeIfAbsent() {
        String test1 = map.computeIfAbsent("test1", t -> "NOTRETURNED");
        assertEquals("Test1", test1);
        String test2 = map.computeIfAbsent("TEST1", t -> "NOTRETURNED");
        assertEquals("Test1", test2);
        String test3 = map.computeIfAbsent("XXXX", t -> t);
        assertEquals("xxxx", test3);
    }

    @Test
    public void computeIfPresent() {
        String test1 = map.computeIfPresent("test1", (t, k) -> "RETURNED1");
        assertEquals("RETURNED1", test1);
        String test2 = map.computeIfPresent("TEST1", (t, k) -> "RETURNED2");
        assertEquals("RETURNED2", test2);
        String test3 = map.computeIfPresent("XXXX", (t, k) -> t);
        assertNull(test3);
    }

    @Test
    public void compute() {
        String test1 = map.compute("test1", (t, k) -> "RETURNED1");
        assertEquals("RETURNED1", test1);
        String test2 = map.compute("TEST1", (t, k) -> "RETURNED2");
        assertEquals("RETURNED2", test2);
        String test3 = map.compute("XXXX", (t, k) -> t);
        assertEquals("xxxx", test3);
    }

    @Test
    public void merge() {
        assertEquals("Test1XXX", map.merge("test1", "XXX", (v1, v2) -> v1 + v2));
        assertEquals("Test2XXX", map.merge("TEST2", "XXX", (v1, v2) -> v1 + v2));
    }

}