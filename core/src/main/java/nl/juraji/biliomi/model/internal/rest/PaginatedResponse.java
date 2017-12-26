package nl.juraji.biliomi.model.internal.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Juraji on 26-12-2017.
 * Biliomi
 */
@XmlRootElement(name = "PaginatedResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaginatedResponse<T> {

  @XmlElement(name = "Entities")
  private List<T> entities;

  @XmlElement(name = "TotalAvailable")
  private int totalAvailable;

  public List<T> getEntities() {
    return entities;
  }

  public void setEntities(List<T> entities) {
    this.entities = entities;
  }

  public int getTotalAvailable() {
    return totalAvailable;
  }

  public void setTotalAvailable(int totalAvailable) {
    this.totalAvailable = totalAvailable;
  }
}
