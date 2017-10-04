package nl.juraji.biliomi.utility.calculate;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Juraji on 24-4-2017.
 * Biliomi v3
 */
public class PatternUtilsTest {

  @Test
  public void hasLinks() throws Exception {
    assertEquals("Fully qualified matched incorrectly", 1,
        PatternUtils.getLinks("Hi, my name is Juraji and http://juraji.nl is my website!").size());
    assertEquals("Host only matched incorrectly", 1,
        PatternUtils.getLinks("Hi, my name is Juraji and juraji.nl is my website!").size());
    assertEquals("With replacements matched incorrectly", 1,
        PatternUtils.getLinks("Hi, my name is Juraji and juraji(dot)nl is my website!").size());
    assertEquals("Email matched incorrectly", 1,
        PatternUtils.getLinks("Hi, my name is Juraji and email@example.ru is my email!").size());
    assertEquals("Hostname with page matched incorrectly", 1,
        PatternUtils.getLinks("Hi, my name is Juraji and juraji.nl/tamagotchis is my favorite page!").size());
    assertEquals("IP address matched incorrectly", 1,
        PatternUtils.getLinks("Hi, my name is Juraji and 54.165.24.58 is my ip-address!").size());
    assertEquals("No link matched incorrectly", 0,
        PatternUtils.getLinks("Hi, my name is Juraji and do not mention websites!").size());
    assertEquals("Did not find multiple links", 5,
        PatternUtils.getLinks("Hi, my name is Juraji (http://juraji.nl) http://juraji.nl sadf54.165.24.58sadf juraji(dot)nl email@example.ru!").size());
  }

  @Test
  public void getLongestRepeatedCharacterSequence() throws Exception {
    assertEquals("The number of repeated characters matched did not equal expected value",
        7, PatternUtils.getLongestRepeatedCharacterSequence("aa ccc bbbbb aaaaaaa sssss"));
    assertEquals("The number of repeated characters matched did not equal expected value",
        9, PatternUtils.getLongestRepeatedCharacterSequence("sdfsdfwegewr sadfasfaaaaaaaaasgsfgdfggfffgdrgeregee aergesargaerthret"));
    assertEquals("The number of repeated symbols did not equal expected value",
        50, PatternUtils.getLongestRepeatedCharacterSequence("#@$% %^&$%^&$% %^&$%^&$%^&#%$^#*^%& %^$&$%^%*$$#%^&$"));
    assertEquals("The number of repeated symbols did not equal expected value",
        41, PatternUtils.getLongestRepeatedCharacterSequence("#$%#$&^#$%&#$%^&%$^$% a ^$#%^$%^$^% ^@$%#^$#%^#$% ^$%^#$%^$% ^#$%^$%"));
  }
}