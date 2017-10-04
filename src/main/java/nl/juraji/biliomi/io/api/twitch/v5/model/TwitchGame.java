package nl.juraji.biliomi.io.api.twitch.v5.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 20-4-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "TwitchGame")
@XmlAccessorType(XmlAccessType.FIELD)
public class TwitchGame {

  @XmlElement(name = "_id")
  private Integer id;

  @XmlElement(name = "box")
  private TwitchImageInfo box;

  @XmlElement(name = "giantbomb_id")
  private Integer giantBombId;

  @XmlElement(name = "logo")
  private TwitchImageInfo logo;

  @XmlElement(name = "name")
  private String name;

  @XmlElement(name = "popularity")
  private Integer popularity;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public TwitchImageInfo getBox() {
    return box;
  }

  public void setBox(TwitchImageInfo box) {
    this.box = box;
  }

  public Integer getGiantBombId() {
    return giantBombId;
  }

  public void setGiantBombId(Integer giantBombId) {
    this.giantBombId = giantBombId;
  }

  public TwitchImageInfo getLogo() {
    return logo;
  }

  public void setLogo(TwitchImageInfo logo) {
    this.logo = logo;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getPopularity() {
    return popularity;
  }

  public void setPopularity(Integer popularity) {
    this.popularity = popularity;
  }
}
