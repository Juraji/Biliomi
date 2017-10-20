package nl.juraji.biliomi.components.integrations.streamlabs.api.streamlabs.socket.model.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

/**
 * Created by Juraji on 2-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "DonationMessage")
@XmlAccessorType(XmlAccessType.FIELD)
public class DonationMessage {

  @XmlElement(name = "id")
  private long id;

  @XmlElement(name = "name")
  private String name;

  @XmlElement(name = "amount")
  private double amount;

  @XmlElement(name = "formattedAmount")
  private String formattedAmount;

  @XmlElement(name = "message")
  private String message;

  @XmlElement(name = "currency")
  private String currency;

  @XmlElement(name = "to")
  private Map<String, String> to;

  @XmlElement(name = "from")
  private String from;

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

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
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

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public Map<String, String> getTo() {
    return to;
  }

  public void setTo(Map<String, String> to) {
    this.to = to;
  }

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }
}
