package nl.juraji.biliomi.model.core.settings;

import nl.juraji.biliomi.model.core.Community;
import nl.juraji.biliomi.utility.factories.ModelUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;

/**
 * Created by Juraji on 16-12-2017.
 * Biliomi
 */
@Entity
@XmlRootElement(name = "CommunitiesSettings")
@XmlAccessorType(XmlAccessType.FIELD)
public class CommunitiesSettings extends Settings {

    @Column
    @XmlElement(name = "AutoUpdateCommunities")
    private boolean autoUpdateCommunities;

    @ManyToMany(fetch = FetchType.EAGER)
    @XmlElement(name = "DefaultCommunities")
    private Set<Community> defaultCommunities;

    public boolean isAutoUpdateCommunities() {
        return autoUpdateCommunities;
    }

    public void setAutoUpdateCommunities(boolean communitiesPerGameEnabled) {
        this.autoUpdateCommunities = communitiesPerGameEnabled;
    }

    public Set<Community> getDefaultCommunities() {
        defaultCommunities = ModelUtils.initCollection(defaultCommunities);
        return defaultCommunities;
    }

    @Override
    public void setDefaultValues() {
        autoUpdateCommunities = false;
    }
}
