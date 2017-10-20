package nl.juraji.biliomi.model.chat;

import nl.juraji.biliomi.model.core.UserGroup;
import nl.juraji.biliomi.model.core.settings.Settings;
import nl.juraji.biliomi.utility.factories.ModelUtils;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Juraji on 13-5-2017.
 * Biliomi v3
 */
@Entity
@XmlRootElement(name = "ChatModeratorSettings")
@XmlAccessorType(XmlAccessType.FIELD)
public class ChatModeratorSettings extends Settings {

  @Column
  @XmlElement(name = "LinksAllowed")
  private boolean linksAllowed;

  @Column
  @XmlElement(name = "LinkPermitExpireTime")
  private long linkPermitDuration;

  @Column
  @XmlElement(name = "ExcessiveCapsAllowed")
  private boolean excessiveCapsAllowed;

  @Column
  @XmlElement(name = "CapsTrigger")
  private int capsTrigger;

  @Column
  @XmlElement(name = "CapsTriggerRatio")
  private double capsTriggerRatio;

  @Column
  @XmlElement(name = "RepeatedCharactersAllowed")
  private boolean repeatedCharactersAllowed;

  @Column
  @XmlElement(name = "RepeatedCharacterTrigger")
  private int repeatedCharacterTrigger;

  @Column
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "ChatModeratorLinkWhitelist")
  @XmlElement(name = "LinkWhitelist")
  private List<String> linkWhitelist;

  @ManyToOne(fetch = FetchType.EAGER)
  @XmlElement(name = "ExemptedGroup")
  private UserGroup exemptedGroup;

  @Column
  @Enumerated(EnumType.STRING)
  @XmlElement(name = "FirstStrike")
  private ModerationAction firstStrike;

  @Column
  @Enumerated(EnumType.STRING)
  @XmlElement(name = "SecondStrike")
  private ModerationAction secondStrike;

  @Column
  @Enumerated(EnumType.STRING)
  @XmlElement(name = "ThirdStrike")
  private ModerationAction thirdStrike;

  public boolean isLinksAllowed() {
    return linksAllowed;
  }

  public void setLinksAllowed(boolean linksAllowed) {
    this.linksAllowed = linksAllowed;
  }

  public long getLinkPermitDuration() {
    return linkPermitDuration;
  }

  public void setLinkPermitDuration(long linkPermitExpireTime) {
    this.linkPermitDuration = linkPermitExpireTime;
  }

  public boolean isExcessiveCapsAllowed() {
    return excessiveCapsAllowed;
  }

  public void setExcessiveCapsAllowed(boolean excessiveCapsAllowed) {
    this.excessiveCapsAllowed = excessiveCapsAllowed;
  }

  public int getCapsTrigger() {
    return capsTrigger;
  }

  public void setCapsTrigger(int capsTrigger) {
    this.capsTrigger = capsTrigger;
  }

  public double getCapsTriggerRatio() {
    return capsTriggerRatio;
  }

  public void setCapsTriggerRatio(double capsTriggerRatio) {
    this.capsTriggerRatio = capsTriggerRatio;
  }

  public boolean isRepeatedCharactersAllowed() {
    return repeatedCharactersAllowed;
  }

  public void setRepeatedCharactersAllowed(boolean repeatedCharactersAllowed) {
    this.repeatedCharactersAllowed = repeatedCharactersAllowed;
  }

  public int getRepeatedCharacterTrigger() {
    return repeatedCharacterTrigger;
  }

  public void setRepeatedCharacterTrigger(int repeatedCharacterTrigger) {
    this.repeatedCharacterTrigger = repeatedCharacterTrigger;
  }

  public List<String> getLinkWhitelist() {
    linkWhitelist = ModelUtils.initCollection(linkWhitelist);
    return linkWhitelist;
  }

  public UserGroup getExemptedGroup() {
    return exemptedGroup;
  }

  public void setExemptedGroup(UserGroup exemptedGroup) {
    this.exemptedGroup = exemptedGroup;
  }

  public ModerationAction getFirstStrike() {
    return firstStrike;
  }

  public void setFirstStrike(ModerationAction firstStrike) {
    this.firstStrike = firstStrike;
  }

  public ModerationAction getSecondStrike() {
    return secondStrike;
  }

  public void setSecondStrike(ModerationAction secondStrike) {
    this.secondStrike = secondStrike;
  }

  public ModerationAction getThirdStrike() {
    return thirdStrike;
  }

  public void setThirdStrike(ModerationAction thirdStrike) {
    this.thirdStrike = thirdStrike;
  }

  @Override
  public void setDefaultValues() {
    linksAllowed = true;
    linkPermitDuration = 60000;
    excessiveCapsAllowed = true;
    capsTrigger = 20;
    capsTriggerRatio = 0.4;
    repeatedCharactersAllowed = true;
    repeatedCharacterTrigger = 15;
    firstStrike = ModerationAction.WARN;
    secondStrike = ModerationAction.PURGE;
    thirdStrike = ModerationAction.PURGE;
  }
}
