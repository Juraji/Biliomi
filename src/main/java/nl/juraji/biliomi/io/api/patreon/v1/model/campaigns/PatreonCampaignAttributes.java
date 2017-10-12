package nl.juraji.biliomi.io.api.patreon.v1.model.campaigns;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 12-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "PatreonCampaignAttributes")
@XmlAccessorType(XmlAccessType.FIELD)
public class PatreonCampaignAttributes {
  private String created_at;
  private String creation_name;
  private String image_small_url;
  private String image_url;
  private boolean is_monthly;
  private int patron_count;
  private String pay_per_name;
  private long pledge_sum;
  private String summary;

  public String getCreated_at() {
    return created_at;
  }

  public void setCreated_at(String created_at) {
    this.created_at = created_at;
  }

  public String getCreation_name() {
    return creation_name;
  }

  public void setCreation_name(String creation_name) {
    this.creation_name = creation_name;
  }

  public String getImage_small_url() {
    return image_small_url;
  }

  public void setImage_small_url(String image_small_url) {
    this.image_small_url = image_small_url;
  }

  public String getImage_url() {
    return image_url;
  }

  public void setImage_url(String image_url) {
    this.image_url = image_url;
  }

  public boolean isIs_monthly() {
    return is_monthly;
  }

  public void setIs_monthly(boolean is_monthly) {
    this.is_monthly = is_monthly;
  }

  public int getPatron_count() {
    return patron_count;
  }

  public void setPatron_count(int patron_count) {
    this.patron_count = patron_count;
  }

  public String getPay_per_name() {
    return pay_per_name;
  }

  public void setPay_per_name(String pay_per_name) {
    this.pay_per_name = pay_per_name;
  }

  public long getPledge_sum() {
    return pledge_sum;
  }

  public void setPledge_sum(long pledge_sum) {
    this.pledge_sum = pledge_sum;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }
}
