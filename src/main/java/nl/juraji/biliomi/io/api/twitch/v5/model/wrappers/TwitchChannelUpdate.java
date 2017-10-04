package nl.juraji.biliomi.io.api.twitch.v5.model.wrappers;

import nl.juraji.biliomi.io.api.twitch.v5.model.TwitchChannel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 19-4-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "TwitchChannelUpdate")
@XmlAccessorType(XmlAccessType.FIELD)
public class TwitchChannelUpdate extends TwitchList {

  @XmlElement(name = "channel")
  private TwitchChannel channel;

  public TwitchChannel getChannel() {
    return channel;
  }

  public void setChannel(TwitchChannel channel) {
    this.channel = channel;
  }
}
