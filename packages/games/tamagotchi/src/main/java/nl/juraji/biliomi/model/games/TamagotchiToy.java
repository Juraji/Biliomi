package nl.juraji.biliomi.model.games;

import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 30-5-2017.
 * Biliomi v3
 */
@Embeddable
@XmlRootElement(name = "TamagotchiToy")
@XmlAccessorType(XmlAccessType.FIELD)
public class TamagotchiToy {

    @Column
    @XmlElement(name = "ToyName")
    private String toyName;

    @Column
        @XmlElement(name = "ExpiresAt")
    private DateTime expiresAt;

    @Column
    @XmlElement(name = "FoodModifier")
    private double foodModifier;

    @Column
    @XmlElement(name = "MoodModifier")
    private double moodModifier;

    @Column
    @XmlElement(name = "HygieneModifier")
    private double hygieneModifier;

    // Used when buying or listing toy (process variable)
    @Transient
    @XmlElement(name = "Cost")
    private long cost;

    public String getToyName() {
        return toyName;
    }

    public void setToyName(String toyName) {
        this.toyName = toyName;
    }

    public DateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(DateTime expiresAt) {
        this.expiresAt = expiresAt;
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

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }
}
