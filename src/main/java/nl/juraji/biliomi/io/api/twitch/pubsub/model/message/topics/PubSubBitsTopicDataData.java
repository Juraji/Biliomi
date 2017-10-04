package nl.juraji.biliomi.io.api.twitch.pubsub.model.message.topics;

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
public class PubSubBitsTopicDataData {

  @XmlElement(name = "user_name")
  private String username;

  @XmlElement(name = "channel_name")
  private String channelName;

  @XmlElement(name = "user_id")
  private String userId;

  @XmlElement(name = "channel_id")
  private String channelId;

  @XmlElement(name = "time")
  private String time;

  @XmlElement(name = "chat_message")
  private String chatMessage;

  @XmlElement(name = "bits_used")
  private long bitsUsed;

  @XmlElement(name = "total_bits_used")
  private long totalBitsUsed;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
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

  public String getChatMessage() {
    return chatMessage;
  }

  public void setChatMessage(String chatMessage) {
    this.chatMessage = chatMessage;
  }

  public long getBitsUsed() {
    return bitsUsed;
  }

  public void setBitsUsed(long bitsUsed) {
    this.bitsUsed = bitsUsed;
  }

  public long getTotalBitsUsed() {
    return totalBitsUsed;
  }

  public void setTotalBitsUsed(long totalBitsUsed) {
    this.totalBitsUsed = totalBitsUsed;
  }
}
