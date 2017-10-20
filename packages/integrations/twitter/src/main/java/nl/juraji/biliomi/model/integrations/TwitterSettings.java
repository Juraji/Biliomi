package nl.juraji.biliomi.model.integrations;

import nl.juraji.biliomi.model.core.settings.Settings;
import nl.juraji.biliomi.utility.factories.ModelUtils;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;

/**
 * Created by Juraji on 11-9-2017.
 * Biliomi v3
 */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TwitterSettings extends Settings {

  @Column
  @XmlElement(name = "TrackedKeywords")
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "TwitterSettingsTrackedKeywords", joinColumns = @JoinColumn(name = "settings_type"))
  private Set<String> trackedKeywords;

  public Set<String> getTrackedKeywords() {
    trackedKeywords = ModelUtils.initCollection(trackedKeywords);
    return trackedKeywords;
  }

  @Override
  public void setDefaultValues() {
  }
}
