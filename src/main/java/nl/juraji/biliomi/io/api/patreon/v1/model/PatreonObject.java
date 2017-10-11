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
public abstract class PatreonObject<T, U> {

  @XmlElement(name = "type")
  private String type;

  @XmlElement(name = "id")
  private String id;

  @XmlElement(name = "attributes")
  private T attributes;

  @XmlElement(name = "relationships")
  private U relationships;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public T getAttributes() {
    return attributes;
  }

  public void setAttributes(T attributes) {
    this.attributes = attributes;
  }

  public U getRelationships() {
    return relationships;
  }

  public void setRelationships(U relationships) {
    this.relationships = relationships;
  }
}
