package nl.juraji.biliomi.model.internal.events.bot;

import nl.juraji.biliomi.utility.events.Event;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Juraji on 1-5-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "ConsoleInputEvent")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConsoleInputEvent extends Event {


  @XmlElement(name = "Input")
  private final String input;

  @XmlElement(name = "LegacyMode")
  private final boolean legacyMode;

  @XmlElement(name = "Indentifier")
  private final char identifier;

  public ConsoleInputEvent(String input, boolean legacyMode) {
    this.input = (input.length() > 0 ? input.substring(1) : input);
    this.identifier = (input.length() > 0 ? input.charAt(0) : 0);
    this.legacyMode = legacyMode;
  }

  public String getInput() {
    return input;
  }

  public List<String> getInputSplit() {
    return Arrays.asList(input.split(" "));
  }

  public boolean isLegacyMode() {
    return legacyMode;
  }

  public char getIdentifier() {
    return identifier;
  }

  @Override
  public String toString() {
    return identifier + input;
  }
}
