package nl.juraji.biliomi.model.internal.events.bot;

import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.utility.events.Event;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 22-10-2017.
 * Biliomi
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AchievementEvent extends Event {

  @XmlElement(name = "User")
  private User user;

  @XmlElement(name = "Achievement")
  private String achievement;

  public AchievementEvent(User user, String achievement, boolean isBuiltIn) {
    this.user = user;
    this.achievement = achievement;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getAchievement() {
    return achievement;
  }

  public void setAchievement(String achievement) {
    this.achievement = achievement;
  }
}
