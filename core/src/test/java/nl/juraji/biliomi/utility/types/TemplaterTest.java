package nl.juraji.biliomi.utility.types;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by Juraji on 8-5-2017.
 * Biliomi v3
 */
public class TemplaterTest {
    private static final String TEMPLATE_STRING = "{{value}}{{value2}}{{value3}}";
    private static final String VALUE_KEY = "value";
    private static final String VALUE_KEY_2 = "value2";
    private static final String VALUE_KEY_3 = "value3";

    @Test
    public void addCollection() {
        Templater templater = Templater.template(TEMPLATE_STRING);
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");

        templater.add(VALUE_KEY, list);

        Map<Integer, String> map = new HashMap<>();
        map.put(4, "456");
        map.put(5, "567");
        map.put(6, "678");

        templater.add(VALUE_KEY_2, map);
        templater.add(VALUE_KEY_3, "");

        String apply = templater.apply();

        assertEquals("1, 2, 34: 456, 5: 567, 6: 678", apply);
    }

    @Test
    public void addObject() {
        Templater templater = Templater.template(TEMPLATE_STRING);

        templater.add(VALUE_KEY, 5);
        templater.add(VALUE_KEY_2, "--");
        templater.add(VALUE_KEY_3, () -> null);

        String apply = templater.apply();

        assertEquals("5--", apply);
    }

    @Test
    public void size() {
        Templater templater = Templater.template(TEMPLATE_STRING);

        templater.add(VALUE_KEY, 1);
        templater.add(VALUE_KEY_2, 2);
        templater.add(VALUE_KEY_3, 3);

        assertEquals(3, templater.size());
    }

    @Test
    public void templaterReusage() {
        Templater templater = Templater.template(TEMPLATE_STRING);

        templater.add(VALUE_KEY, 1);
        templater.add(VALUE_KEY_2, 2);
        templater.add(VALUE_KEY_3, 3);

        assertEquals("123", templater.apply());

        templater.add(VALUE_KEY, 4);
        templater.add(VALUE_KEY_2, 5);
        templater.add(VALUE_KEY_3, 6);

        assertEquals("456", templater.apply());
    }
}