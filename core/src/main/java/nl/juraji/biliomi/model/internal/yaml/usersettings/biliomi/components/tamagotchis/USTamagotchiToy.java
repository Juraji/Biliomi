package nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi.components.tamagotchis;

/**
 * Created by Juraji on 9-10-2017.
 * Biliomi
 */
public class USTamagotchiToy {
  private String name;
  private int durationDays;
  private double foodModifier;
  private double moodModifier;
  private double hygieneModifier;
  private int cost;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getDurationDays() {
    return durationDays;
  }

  public void setDurationDays(int durationDays) {
    this.durationDays = durationDays;
  }

  public double getFoodModifier() {
    return foodModifier;
  }

  public void setFoodModifier(double foodModifier) {
    this.foodModifier = foodModifier;
  }

  public double getMoodModifier() {
    return moodModifier;
  }

  public void setMoodModifier(double moodModifier) {
    this.moodModifier = moodModifier;
  }

  public double getHygieneModifier() {
    return hygieneModifier;
  }

  public void setHygieneModifier(double hygieneModifier) {
    this.hygieneModifier = hygieneModifier;
  }

  public int getCost() {
    return cost;
  }

  public void setCost(int cost) {
    this.cost = cost;
  }
}
