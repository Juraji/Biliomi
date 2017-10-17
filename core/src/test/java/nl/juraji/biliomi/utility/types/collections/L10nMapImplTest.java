package nl.juraji.biliomi.utility.types.collections;

import nl.juraji.biliomi.components.interfaces.enums.StreamState;
import nl.juraji.biliomi.utility.types.Templater;
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
public class L10nMapImplTest {

  private L10nMapImpl l10nMap;

  @Before
  public void setUp() throws Exception {
    // Setup an L10nMapImpl instance with some test data

    InputStream commonStream = L10nMapImplTest.class.getResourceAsStream("/l10n/Common.properties");
    InputStream componentStream = L10nMapImplTest.class.getResourceAsStream("/l10n/SomeComponent.properties");

    l10nMap = new L10nMapImpl();
    l10nMap.load(commonStream);
    l10nMap.load(componentStream);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getNonExistent() throws Exception {
    String s = l10nMap.get("Some.nonExistent.Key").apply();
    assertNull(s);
  }

  @Test
  public void getTemplate() throws Exception {
    Templater template = l10nMap.get("TestKey.1");
    assertEquals("TestKey1", template.apply());
  }

  @Test
  public void supply() throws Exception {
    Supplier<String> supply = l10nMap.supply("TestKey.1");
    assertEquals("TestKey1", supply.get());
  }

  @Test
  public void getIfElse() throws Exception {
    String whenTrue = l10nMap.getIfElse(true, "TestKey.1", "TestKey.2");
    String whenFalse = l10nMap.getIfElse(false, "TestKey.1", "TestKey.2");

    assertEquals("TestKey1", whenTrue);
    assertEquals("TestKey2", whenFalse);
  }

  @Test
  public void getEnabledDisabled() throws Exception {
    String whenTrue = l10nMap.getEnabledDisabled(true);
    String whenFalse = l10nMap.getEnabledDisabled(false);

    assertEquals("enabled", whenTrue);
    assertEquals("disabled", whenFalse);
  }

  @Test
  public void getStreamState() throws Exception {
    String whenOnline = l10nMap.getStreamState(StreamState.ONLINE);
    String whenOffline = l10nMap.getStreamState(StreamState.OFFLINE);

    assertEquals("online", whenOnline);
    assertEquals("offline", whenOffline);
  }

  @Test
  public void getUserNonExistent() throws Exception {
    String notice = l10nMap.getUserNonExistent("TestUser");
    assertEquals("User TestUser does not exist", notice);
  }

  @Test
  public void getCommandNonExistent() throws Exception {
    String notice = l10nMap.getCommandNonExistent("testcommand");
    assertEquals("Command !testcommand does not exist", notice);
  }

  @Test
  public void getGroupNonExistent() throws Exception {
    String notice = l10nMap.getGroupNonExistent("TestGroup");
    assertEquals("Group with name TestGroup was not found", notice);
  }
}