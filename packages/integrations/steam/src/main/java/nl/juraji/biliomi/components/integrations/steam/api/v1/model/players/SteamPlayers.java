package nl.juraji.biliomi.components.integrations.steam.api.v1.model.players;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Juraji on 5-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "SteamPlayers")
@XmlAccessorType(XmlAccessType.FIELD)
public class SteamPlayers {

    @XmlElement(name = "players")
    private List<SteamPlayer> players;

    public List<SteamPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<SteamPlayer> players) {
        this.players = players;
    }
}
