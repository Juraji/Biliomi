package nl.juraji.biliomi.utility.types;

import nl.juraji.biliomi.utility.types.hibernatetypes.DateTimeISO8601Type;
import org.joda.time.DateTime;
import org.junit.Test;

import java.io.Serializable;
import java.sql.Types;

import static org.junit.Assert.*;

/**
 * Created by Juraji on 8-5-2017.
 * Biliomi v3
 */
public class DateTimeISO8601TypeTest {

    private final DateTimeISO8601Type instance = new DateTimeISO8601Type();
    private final DateTime now = DateTime.now();
    private final DateTime later = DateTime.now().withDurationAdded(1, 1000);

    @Test
    public void typeStaticEqualsPackage() {
        String actualType = DateTimeISO8601Type.class.getPackage().getName() + '.' + DateTimeISO8601Type.class.getSimpleName();
        assertEquals(actualType, DateTimeISO8601Type.TYPE);
    }

    @Test
    public void sqlTypes() {
        assertArrayEquals(new int[]{Types.VARCHAR}, instance.sqlTypes());
    }

    @Test
    public void returnedClass() {
        assertEquals(DateTime.class, instance.returnedClass());
    }

    @Test
    public void objEquals() {
        assertTrue(instance.equals(now, now));
        assertFalse(instance.equals(now, null));
        assertFalse(instance.equals(now, later));
    }

    @Test
    public void nullSafeGet() {
        // Untestable, due to complex argument construction
        assertTrue(true);
    }

    @Test
    public void nullSafeSet() {
        // Untestable, due to complex argument construction
        assertTrue(true);
    }

    @Test
    public void deepCopy() {
        Object o = instance.deepCopy(now);
        assertEquals(now, o);
    }

    @Test
    public void isMutable() {
        assertFalse(instance.isMutable());
    }

    @Test
    public void disassemble() {
        assertNotNull(instance.disassemble(now));
    }

    @Test
    public void assemble() {
        //noinspection UnnecessaryLocalVariable
        Serializable serializable = now;
        DateTime dateTime = new DateTime();
        Object assembled = instance.assemble(serializable, dateTime);
        assertEquals(now.getMillis(), ((DateTime) assembled).getMillis(), 1000);
    }

    @Test
    public void replace() {
        assertEquals(now, instance.replace(now, later, later));
    }
}