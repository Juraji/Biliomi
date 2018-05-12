package nl.juraji.biliomi.utility.calculate;

import nl.juraji.biliomi.utility.types.enums.OnOff;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Juraji on 8-5-2017.
 * Biliomi v3
 */
public class EnumUtilsTest {

    @Test
    public void fromValidStringValue() {
        OnOff onOff = EnumUtils.toEnum("off", OnOff.class);

        assertEquals(OnOff.OFF, onOff);
    }

    @Test
    public void fromInvalidStringValue() {
        OnOff onOff = EnumUtils.toEnum("NotAValidValue", OnOff.class);

        assertNull(onOff);
    }
}