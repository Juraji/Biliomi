package nl.juraji.biliomi.components.integrations.spotify.api.v1.model.tracks;

import nl.juraji.biliomi.utility.factories.ModelUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;

/**
 * Created by Juraji on 1-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "SpotifyTrackUriList")
@XmlAccessorType(XmlAccessType.FIELD)
public class SpotifyTrackUriList {
    @XmlElement(name = "uris")
    public Set<String> uris;

    public Set<String> getUris() {
        uris = ModelUtils.initCollection(uris);
        return uris;
    }
}
