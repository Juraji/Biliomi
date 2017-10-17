package nl.juraji.biliomi.model.internal.events.twitch.subscribers;

import nl.juraji.biliomi.model.internal.events.twitch.TwitchEvent;
import org.joda.time.DateTime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 3-9-2017.
 * Biliomi v3
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TwitchSubscriberEvent extends TwitchEvent {

  @XmlElement(name = "Username")
  private String username;

  @XmlElement(name = "UserId")
  private long userId;

  @XmlElement(name = "Time")
  private DateTime time;

  @XmlElement(name = "SubPlan")
  private SubscriberPlanType subPlan;

  @XmlElement(name = "IsResub")
  private boolean isResub;

  public TwitchSubscriberEvent(String username, long userId, DateTime time, SubscriberPlanType subPlan, boolean isResub) {
    this.username = username;
    this.userId = userId;
    this.time = time;
    this.subPlan = subPlan;
    this.isResub = isResub;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public DateTime getTime() {
    return time;
  }

  public void setTime(DateTime time) {
    this.time = time;
  }

  public SubscriberPlanType getSubPlan() {
    return subPlan;
  }

  public void setSubPlan(SubscriberPlanType subPlan) {
    this.subPlan = subPlan;
  }

  public boolean isResub() {
    return isResub;
  }

  public void setResub(boolean resub) {
    isResub = resub;
  }
}
