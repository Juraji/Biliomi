package nl.juraji.biliomi.model.core;

import nl.juraji.biliomi.utility.factories.ModelUtils;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;

/**
 * Created by Juraji on 10-4-2017.
 * biliomi
 */
@Entity
@XmlRootElement(name = "Command")
@XmlAccessorType(XmlAccessType.FIELD)
@Inheritance(strategy = InheritanceType.JOINED)
public class Command {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @XmlElement(name = "Id")
  private long id;

  @Column(unique = true)
  @NotNull
  @XmlElement(name = "Command")
  private String command;

  @Column
  @NotNull
  @ColumnDefault("0")
  @XmlElement(name = "Price")
  private long price;

  @Column
  @NotNull
  @ColumnDefault("0")
  @XmlElement(name = "Cooldown")
  private long cooldown;

  @Column
  @ColumnDefault("FALSE")
  @XmlElement(name = "ModeratorCanAlwaysActivate")
  private boolean moderatorCanActivate;

  @Column
  @ColumnDefault("FALSE")
  @XmlElement(name = "SystemCommand")
  private boolean systemCommand;

  @ManyToOne
  @NotNull
  @XmlElement(name = "UserGroup")
  private UserGroup userGroup;

  @Column
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "CommandAliasses")
  @XmlElement(name = "Aliasses")
  private Set<String> aliasses;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public long getPrice() {
    return price;
  }

  public void setPrice(long price) {
    this.price = price;
  }

  public long getCooldown() {
    return cooldown;
  }

  public void setCooldown(long cooldown) {
    this.cooldown = cooldown;
  }

  public boolean isModeratorCanActivate() {
    return moderatorCanActivate;
  }

  public void setModeratorCanActivate(boolean moderatorCanAlwaysActivate) {
    this.moderatorCanActivate = moderatorCanAlwaysActivate;
  }

  public boolean isSystemCommand() {
    return systemCommand;
  }

  public void setSystemCommand(boolean systemCommand) {
    this.systemCommand = systemCommand;
  }

  public UserGroup getUserGroup() {
    return userGroup;
  }

  public void setUserGroup(UserGroup userGroup) {
    this.userGroup = userGroup;
  }

  public Set<String> getAliasses() {
    aliasses = ModelUtils.initCollection(aliasses);
    return aliasses;
  }
}