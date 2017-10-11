package nl.juraji.biliomi.io.api.patreon.v1.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 11-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "PatreonDataWrapper")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class PatreonDataWrapper<T> {

  @XmlElement(name = "data")
  private T data;

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }
}
