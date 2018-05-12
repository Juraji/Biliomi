package nl.juraji.biliomi.components.integrations.spotify.api.v1.model.playlist;

import nl.juraji.biliomi.components.integrations.spotify.api.v1.model.tracks.SpotifyTrackPagingObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 30-9-2017.
 * Biliomi
 */
@XmlRootElement(name = "SpotifyPlaylist")
@XmlAccessorType(XmlAccessType.FIELD)
public class SpotifyPlaylist {

    @XmlElement(name = "id")
    private String id;

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "public")
    private boolean isPublic;

    @XmlElement(name = "tracks")
    private SpotifyTrackPagingObject tracks;

    @XmlElement(name = "uri")
    private String uri;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public SpotifyTrackPagingObject getTracks() {
        return tracks;
    }

    public void setTracks(SpotifyTrackPagingObject tracks) {
        this.tracks = tracks;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
