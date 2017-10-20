package nl.juraji.biliomi.model.integrations;

import nl.juraji.biliomi.model.core.settings.Settings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 5-10-2017.
 * Biliomi
 */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SteamSettings extends Settings {

  @Column
  @XmlElement(name = "AutoUpdateChannelGame")
  private boolean autoUpdateChannelGame;

  public boolean isAutoUpdateChannelGame() {
    return autoUpdateChannelGame;
  }

  public void setAutoUpdateChannelGame(boolean autoUpdateChannelGame) {
    this.autoUpdateChannelGame = autoUpdateChannelGame;
  }

  @Override
  public void setDefaultValues() {
    this.autoUpdateChannelGame = false;
  }
}
