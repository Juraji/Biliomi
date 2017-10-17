package nl.juraji.biliomi.model.core;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 10-4-2017.
 * biliomi
 */
@Entity
@XmlRootElement(name = "UserGroup")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserGroup {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @XmlElement(name = "Id")
  private long id;

  @Column(unique = true)
  @NotNull
  @XmlElement(name = "Name")
  private String name;

  @Column(unique = true)
  @NotNull
  @XmlElement(name = "Weight")
  private int weight;

  @Column
  @XmlElement(name = "DefaultGroup")
  private boolean defaultGroup;

  @Column
  @XmlElement(name = "LevelUpHours")
  private Integer levelUpHours;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String groupName) {
    this.name = groupName;
  }

  public int getWeight() {
    return weight;
  }

  public void setWeight(int groupWeight) {
    this.weight = groupWeight;
  }

  public boolean isDefaultGroup() {
    return defaultGroup;
  }

  public void setDefaultGroup(boolean systemGroup) {
    this.defaultGroup = systemGroup;
  }

  public Integer getLevelUpHours() {
    return levelUpHours;
  }

  public void setLevelUpHours(Integer levelUpHours) {
    this.levelUpHours = levelUpHours;
  }

  public boolean isInGroup(UserGroup targetGroup) {
    return weight <= targetGroup.getWeight();
  }

  public boolean isNotInGroup(UserGroup targetGroup) {
    return weight > targetGroup.getWeight();
  }
}