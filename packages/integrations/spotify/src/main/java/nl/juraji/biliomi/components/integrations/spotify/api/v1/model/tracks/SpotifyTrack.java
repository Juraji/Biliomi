package nl.juraji.biliomi.components.integrations.spotify.api.v1.model.tracks;

import nl.juraji.biliomi.utility.factories.ModelUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;

/**
 * Created by Juraji on 30-9-2017.
 * Biliomi
 */
@XmlRootElement(name = "SpotifyTrack")
@XmlAccessorType(XmlAccessType.FIELD)
public class SpotifyTrack {

    @XmlElement(name = "id")
    private String id;

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "album")
    private SpotifyAlbum album;

    @XmlElement(name = "artists")
    private Set<SpotifyArtist> artists;

    @XmlElement(name = "duration_ms")
    private long durationMs;

    @XmlElement(name = "uri")
    private String uri;

    @XmlElement(name = "is_playable")
    private boolean isPlayable = true;

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

    public SpotifyAlbum getAlbum() {
        return album;
    }

    public void setAlbum(SpotifyAlbum album) {
        this.album = album;
    }

    public Set<SpotifyArtist> getArtists() {
        return artists;
    }

    public void setArtists(Set<SpotifyArtist> artists) {
        this.artists = artists;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public boolean isPlayable() {
        return isPlayable;
    }

    public void setPlayable(boolean playable) {
        isPlayable = playable;
    }

    public String getCombinedArtists() {
        artists = ModelUtils.initCollection(artists);
        return artists.stream()
                .map(SpotifyArtist::getName)
                .reduce((l, r) -> l + " & " + r)
                .orElse("Unknown Artist");
    }
}
