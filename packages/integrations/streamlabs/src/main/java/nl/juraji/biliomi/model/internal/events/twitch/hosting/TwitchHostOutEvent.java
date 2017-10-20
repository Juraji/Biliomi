package nl.juraji.biliomi.model.internal.events.twitch.hosting;

import nl.juraji.biliomi.model.internal.events.twitch.TwitchEvent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 23-4-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "TwitchHostOutEvent")
@XmlAccessorType(XmlAccessType.FIELD)
public class TwitchHostOutEvent extends TwitchEvent {

  @XmlElement(name = "Target")
  private final String target;

  public TwitchHostOutEvent(String target) {
    this.target = target;
  }

  public String getTarget() {
    return target;
  }
}
