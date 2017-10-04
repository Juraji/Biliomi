package nl.juraji.biliomi.model.core;

import nl.juraji.biliomi.utility.factories.ModelUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

/**
 * Created by Juraji on 13-5-2017.
 * Biliomi v3
 */
@Entity
@XmlRootElement(name = "Template")
@XmlAccessorType(XmlAccessType.FIELD)
public class Template {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @XmlElement(name = "Id")
  private long id;

  @Column(unique = true)
  @NotNull
  @XmlElement(name = "TemplateKey")
  private String templateKey;

  @Column
  @XmlElement(name = "Template")
  private String template;

  @Column
  @NotNull
  @XmlElement(name = "Description")
  private String description;

  @Column
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "TemplateKeys")
  @XmlElement(name = "KeyDescriptions")
  private Map<String, String> keyDescriptions;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getTemplateKey() {
    return templateKey;
  }

  public void setTemplateKey(String templateKey) {
    this.templateKey = templateKey;
  }


  public String getTemplate() {
    return template;
  }

  public void setTemplate(String template) {
    this.template = template;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Map<String, String> getKeyDescriptions() {
    keyDescriptions = ModelUtils.initCollection(keyDescriptions);
    return keyDescriptions;
  }

  public boolean isEmpty() {
    return template == null || template.length() == 0;
  }
}
