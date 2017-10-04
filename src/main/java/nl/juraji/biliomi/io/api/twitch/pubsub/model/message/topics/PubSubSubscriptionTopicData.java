package nl.juraji.biliomi.io.api.twitch.pubsub.model.message.topics;

import nl.juraji.biliomi.model.internal.events.twitch.subscribers.SubscriberPlanType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 4-9-2017.
 * Biliomi v3
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PubSubSubscriptionTopicData {

  @XmlElement(name = "user_name")
  private String username;

  @XmlElement(name = "display_name")
  private String displayName;

  @XmlElement(name = "channel_name")
  private String channelName;

  @XmlElement(name = "user_id")
  private String userId;

  @XmlElement(name = "channel_id")
  private String channelId;

  @XmlElement(name = "time")
  private String time;

  @XmlElement(name = "sub_plan")
  private SubscriberPlanType subPlan;

  @XmlElement(name = "sub_plan_name")
  private String subPlanName;

  @XmlElement(name = "months")
  private int months;

  @XmlElement(name = "context")
  private String context;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getChannelName() {
    return channelName;
  }

  public void setChannelName(String channelName) {
    this.channelName = channelName;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getChannelId() {
    return channelId;
  }

  public void setChannelId(String channelId) {
    this.channelId = channelId;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public SubscriberPlanType getSubPlan() {
    return subPlan;
  }

  public void setSubPlan(SubscriberPlanType subPlan) {
    this.subPlan = subPlan;
  }

  public String getSubPlanName() {
    return subPlanName;
  }

  public void setSubPlanName(String subPlanName) {
    this.subPlanName = subPlanName;
  }

  public int getMonths() {
    return months;
  }

  public void setMonths(int months) {
    this.months = months;
  }

  public String getContext() {
    return context;
  }

  public void setContext(String context) {
    this.context = context;
  }
}
