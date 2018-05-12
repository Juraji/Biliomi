package nl.juraji.biliomi.model.internal.events;

import nl.juraji.biliomi.utility.events.Event;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 2-10-2017.
 * Biliomi
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class StreamLabsDonationEvent extends Event {

    @XmlElement(name = "Username")
    private String username;

    @XmlElement(name = "FormattedAmount")
    private String formattedAmount;

    @XmlElement(name = "Message")
    private String message;

    @XmlElement(name = "Amount")
    private double amount;

    public StreamLabsDonationEvent(String username, String formattedAmount, double amount, String message) {
        this.username = username;
        this.formattedAmount = formattedAmount;
        this.message = message;
        this.amount = amount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFormattedAmount() {
        return formattedAmount;
    }

    public void setFormattedAmount(String formattedAmount) {
        this.formattedAmount = formattedAmount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
