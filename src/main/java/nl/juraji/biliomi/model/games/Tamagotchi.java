package nl.juraji.biliomi.model.games;

import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.components.games.tamagotchi.services.Gender;
import nl.juraji.biliomi.utility.factories.ModelUtils;
import nl.juraji.biliomi.utility.types.hibernatetypes.DateTimeISO8601Type;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;

/**
 * Created by Juraji on 28-5-2017.
 * Biliomi v3
 */
@Entity
@XmlRootElement(name = "Tamagotchi")
@XmlAccessorType(XmlAccessType.FIELD)
public class Tamagotchi {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @XmlElement(name = "Id")
  private long id;

  @Column
  @NotNull
  @XmlElement(name = "Name")
  private String name;

  @Column
  @NotNull
  @XmlElement(name = "Species")
  private String species;

  @ManyToOne
  @NotNull
  @XmlElement(name = "Owner")
  private User owner;

  @Column
  @NotNull
  @Enumerated(EnumType.STRING)
  @XmlElement(name = "Gender")
  private Gender gender;

  @Column
  @XmlElement(name = "FoodStack")
  private double foodStack;

  @Column
  @XmlElement(name = "MoodLevel")
  private double moodLevel;

  @Column
  @XmlElement(name = "HygieneLevel")
  private double hygieneLevel;

  @Column
  @XmlElement(name = "Affection")
  private int affection;

  @Column
  @ColumnDefault("FALSE")
  @XmlElement(name = "Deceased")
  private boolean deceased;

  @Column
  @NotNull
  @Type(type = DateTimeISO8601Type.TYPE)
  @XmlElement(name = "DateOfBirth")
  private DateTime dateOfBirth;

  @Column
  @Type(type = DateTimeISO8601Type.TYPE)
  @XmlElement(name = "DateOfDeath")
  private DateTime dateOfDeath;

  @Column
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "TamagotchiToys", joinColumns = @JoinColumn(name = "tamagotchi_id"))
  @XmlElement(name = "Toys")
  private Set<TamagotchiToy> toys;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSpecies() {
    return species;
  }

  public void setSpecies(String species) {
    this.species = species;
  }

  public User getOwner() {
    return owner;
  }

  public void setOwner(User owner) {
    this.owner = owner;
  }

  public Gender getGender() {
    return gender;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }

  public double getFoodStack() {
    return foodStack;
  }

  public void setFoodStack(double food) {
    this.foodStack = food;
  }

  public double getMoodLevel() {
    return moodLevel;
  }

  public void setMoodLevel(double mood) {
    this.moodLevel = mood;
  }

  public double getHygieneLevel() {
    return hygieneLevel;
  }

  public void setHygieneLevel(double hygiene) {
    this.hygieneLevel = hygiene;
  }

  public int getAffection() {
    return affection;
  }

  public void setAffection(int affection) {
    this.affection = affection;
  }

  public boolean isDeceased() {
    return deceased;
  }

  public void setDeceased(boolean deceased) {
    this.deceased = deceased;
  }

  public DateTime getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(DateTime dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public DateTime getDateOfDeath() {
    return dateOfDeath;
  }

  public void setDateOfDeath(DateTime dateOfDeath) {
    this.dateOfDeath = dateOfDeath;
  }

  public Set<TamagotchiToy> getToys() {
    toys = ModelUtils.initCollection(toys);
    return toys;
  }
}
