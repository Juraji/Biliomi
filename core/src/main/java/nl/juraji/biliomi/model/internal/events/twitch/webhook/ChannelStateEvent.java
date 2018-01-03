package nl.juraji.biliomi.model.internal.events.twitch.webhook;

import nl.juraji.biliomi.model.internal.events.twitch.TwitchEvent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 2-1-2018.
 * Biliomi
 */
@XmlRootElement(name = "ChannelStateEvent")
@XmlAccessorType(XmlAccessType.FIELD)
public class ChannelStateEvent extends TwitchEvent {

  @XmlElement(name = "ChannelId")
  private final String channelId;

  @XmlElement(name = "IsOnline")
  private final boolean isOnline;

  @XmlElement(name = "GameId")
  private final String gameId;

  public ChannelStateEvent(String channelId, boolean isOnline, String gameId) {
    this.channelId = channelId;
    this.isOnline = isOnline;
    this.gameId = gameId;
  }

  public String getChannelId() {
    return channelId;
  }

  public boolean isOnline() {
    return isOnline;
  }

  public String getGameId() {
    return gameId;
  }
}
