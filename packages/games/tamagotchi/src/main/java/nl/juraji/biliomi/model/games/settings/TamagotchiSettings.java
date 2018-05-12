package nl.juraji.biliomi.model.games.settings;

import nl.juraji.biliomi.model.core.settings.Settings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 28-5-2017.
 * Biliomi v3
 */
@Entity
@XmlRootElement(name = "TamagotchiSettings")
@XmlAccessorType(XmlAccessType.FIELD)
public class TamagotchiSettings extends Settings {

    @Column
    @XmlElement(name = "NewPrice")
    private long newPrice;

    @Column
    @XmlElement(name = "FoodPrice")
    private long foodPrice;

    @Column
    @XmlElement(name = "SoapPrice")
    private long soapPrice;

    @Column
    @XmlElement(name = "MaxFood")
    private double maxFood;

    @Column
    @XmlElement(name = "MaxMood")
    private double maxMood;

    @Column
    @XmlElement(name = "MaxHygiene")
    private double maxHygiene;

    @Column
    @XmlElement(name = "NameMaxLength")
    private int nameMaxLength;

    @Column
    @XmlElement(name = "BotTamagotchiEnabled")
    private boolean botTamagotchiEnabled;

    public long getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(long newPrice) {
        this.newPrice = newPrice;
    }

    public long getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(long candyPrice) {
        this.foodPrice = candyPrice;
    }

    public long getSoapPrice() {
        return soapPrice;
    }

    public void setSoapPrice(long soapPrice) {
        this.soapPrice = soapPrice;
    }

    public double getMaxFood() {
        return maxFood;
    }

    public void setMaxFood(double maxFood) {
        this.maxFood = maxFood;
    }

    public double getMaxMood() {
        return maxMood;
    }

    public void setMaxMood(double maxMood) {
        this.maxMood = maxMood;
    }

    public double getMaxHygiene() {
        return maxHygiene;
    }

    public void setMaxHygiene(double maxHygiene) {
        this.maxHygiene = maxHygiene;
    }

    public int getNameMaxLength() {
        return nameMaxLength;
    }

    public void setNameMaxLength(int nameMaxLength) {
        this.nameMaxLength = nameMaxLength;
    }

    public boolean isBotTamagotchiEnabled() {
        return botTamagotchiEnabled;
    }

    public void setBotTamagotchiEnabled(boolean botTamagotchiEnabled) {
        this.botTamagotchiEnabled = botTamagotchiEnabled;
    }

    @Override
    public void setDefaultValues() {
        newPrice = 1000;
        foodPrice = 5;
        soapPrice = 10;
        maxFood = 30.0;
        maxMood = 14.0;
        maxHygiene = 7.0;
        nameMaxLength = 15;
        botTamagotchiEnabled = true;
    }
}
