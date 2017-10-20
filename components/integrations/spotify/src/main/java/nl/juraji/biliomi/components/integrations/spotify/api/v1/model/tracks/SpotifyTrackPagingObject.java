package nl.juraji.biliomi.components.integrations.spotify.api.v1.model.tracks;

import nl.juraji.biliomi.components.integrations.spotify.api.v1.model.SpotifyPagingObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 1-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "SpotifyTrackPagingObject")
@XmlAccessorType(XmlAccessType.FIELD)
public class SpotifyTrackPagingObject extends SpotifyPagingObject<SpotifyTrack> {
}
