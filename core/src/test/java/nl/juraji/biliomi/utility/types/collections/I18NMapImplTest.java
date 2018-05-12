package nl.juraji.biliomi.utility.types.collections;

import nl.juraji.biliomi.utility.types.Templater;
import nl.juraji.biliomi.utility.types.enums.StreamState;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Juraji on 10-5-2017.
 * Biliomi v3
 */
public class I18NMapImplTest {

    private I18NMapImpl i18nMap;

    @Before
    public void setUp() throws Exception {
        // Setup an I18NMapImpl instance with some test data

        InputStream commonStream = I18NMapImplTest.class.getResourceAsStream("/i18n/Common.properties");
        InputStream componentStream = I18NMapImplTest.class.getResourceAsStream("/i18n/SomeComponent.properties");

        i18nMap = new I18NMapImpl();
        i18nMap.load(commonStream);
        i18nMap.load(componentStream);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNonExistent() {
        String s = i18nMap.get("Some.nonExistent.Key").apply();
        assertNull(s);
    }

    @Test
    public void getTemplate() {
        Templater template = i18nMap.get("TestKey.1");
        assertEquals("TestKey1", template.apply());
    }

    @Test
    public void supply() {
        Supplier<String> supply = i18nMap.supply("TestKey.1");
        assertEquals("TestKey1", supply.get());
    }

    @Test
    public void getIfElse() {
        String whenTrue = i18nMap.getIfElse(true, "TestKey.1", "TestKey.2");
        String whenFalse = i18nMap.getIfElse(false, "TestKey.1", "TestKey.2");

        assertEquals("TestKey1", whenTrue);
        assertEquals("TestKey2", whenFalse);
    }

    @Test
    public void getEnabledDisabled() {
        String whenTrue = i18nMap.getEnabledDisabled(true);
        String whenFalse = i18nMap.getEnabledDisabled(false);

        assertEquals("enabled", whenTrue);
        assertEquals("disabled", whenFalse);
    }

    @Test
    public void getStreamState() {
        String whenOnline = i18nMap.getStreamState(StreamState.ONLINE);
        String whenOffline = i18nMap.getStreamState(StreamState.OFFLINE);

        assertEquals("online", whenOnline);
        assertEquals("offline", whenOffline);
    }

    @Test
    public void getUserNonExistent() {
        String notice = i18nMap.getUserNonExistent("TestUser");
        assertEquals("User TestUser does not exist", notice);
    }

    @Test
    public void getCommandNonExistent() {
        String notice = i18nMap.getCommandNonExistent("testcommand");
        assertEquals("Command !testcommand does not exist", notice);
    }

    @Test
    public void getGroupNonExistent() {
        String notice = i18nMap.getGroupNonExistent("TestGroup");
        assertEquals("Group with name TestGroup was not found", notice);
    }
}