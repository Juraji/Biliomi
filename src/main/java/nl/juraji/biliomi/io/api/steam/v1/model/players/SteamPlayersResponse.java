package nl.juraji.biliomi.io.api.steam.v1.model.players;

import nl.juraji.biliomi.io.api.steam.v1.model.wrappers.SteamResponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 5-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "SteamPlayersResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class SteamPlayersResponse extends SteamResponse<SteamPlayers> {
}
