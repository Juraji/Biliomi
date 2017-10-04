package nl.juraji.biliomi.io.api.spotify.v1.model.playlist;

import nl.juraji.biliomi.io.api.spotify.v1.model.SpotifyPagingObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 1-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "SpotifyPlaylistPagingObject")
@XmlAccessorType(XmlAccessType.FIELD)
public class SpotifyPlaylistPagingObject extends SpotifyPagingObject<SpotifyPlaylist> {
}
