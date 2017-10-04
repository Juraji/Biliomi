package nl.juraji.biliomi.io.api.twitch.v5.model.wrappers;

import nl.juraji.biliomi.io.api.twitch.v5.model.TwitchUser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Juraji on 20-4-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "TwitchUserLogins")
@XmlAccessorType(XmlAccessType.FIELD)
public class TwitchUserLogins extends TwitchList {

  @XmlElement(name = "users")
  private List<TwitchUser> users;

  public List<TwitchUser> getUsers() {
    return users;
  }

  public void setUsers(List<TwitchUser> users) {
    this.users = users;
  }
}
