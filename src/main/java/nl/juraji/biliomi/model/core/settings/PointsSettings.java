package nl.juraji.biliomi.model.core.settings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 19-4-2017.
 * Biliomi v3
 */
@Entity
@XmlRootElement(name = "PointsSettings")
@XmlAccessorType(XmlAccessType.FIELD)
public class PointsSettings extends Settings {
  public static final String DEFAULT_POINTS_NAME = "Points";
  public static final String DEFAULT_SINGULAR_POINTS_NAME = "Point";

  @Column
  @XmlElement(name = "PointsNameSingular")
  private String pointsNameSingular;

  @Column
  @XmlElement(name = "PointsNamePlural")
  private String pointsNamePlural;

  @Column
  @XmlElement(name = "TrackOnline")
  private boolean trackOnline;

  @Column
  @XmlElement(name = "TrackOffline")
  private boolean trackOffline;

  @Column
  @XmlElement(name = "OnlinePayoutInterval")
  private long onlinePayoutInterval;

  @Column
  @XmlElement(name = "OfflinePayoutInterval")
  private long offlinePayoutInterval;

  @Column
  @XmlElement(name = "OnlinePayoutAmount")
  private long onlinePayoutAmount;

  @Column
  @XmlElement(name = "OfflinePayoutAmount")
  private long offlinePayoutAmount;

  public String getPointsNameSingular() {
    return pointsNameSingular;
  }

  public void setPointsNameSingular(String pointsNameSingular) {
    this.pointsNameSingular = pointsNameSingular;
  }

  public String getPointsNamePlural() {
    return pointsNamePlural;
  }

  public void setPointsNamePlural(String pointsNamePlural) {
    this.pointsNamePlural = pointsNamePlural;
  }

  public boolean isTrackOnline() {
    return trackOnline;
  }

  public void setTrackOnline(boolean payoutStreamOnlineEnabled) {
    this.trackOnline = payoutStreamOnlineEnabled;
  }

  public boolean isTrackOffline() {
    return trackOffline;
  }

  public void setTrackOffline(boolean payoutStreamOfflineEnabled) {
    this.trackOffline = payoutStreamOfflineEnabled;
  }

  public long getOnlinePayoutInterval() {
    return onlinePayoutInterval;
  }

  public void setOnlinePayoutInterval(long payoutIntervalStreamOnline) {
    this.onlinePayoutInterval = payoutIntervalStreamOnline;
  }

  public long getOfflinePayoutInterval() {
    return offlinePayoutInterval;
  }

  public void setOfflinePayoutInterval(long payoutIntervalStreamOffline) {
    this.offlinePayoutInterval = payoutIntervalStreamOffline;
  }

  public long getOnlinePayoutAmount() {
    return onlinePayoutAmount;
  }

  public void setOnlinePayoutAmount(long payoutAmmountStreamOnline) {
    this.onlinePayoutAmount = payoutAmmountStreamOnline;
  }

  public long getOfflinePayoutAmount() {
    return offlinePayoutAmount;
  }

  public void setOfflinePayoutAmount(long payoutAmmountStreamOffline) {
    this.offlinePayoutAmount = payoutAmmountStreamOffline;
  }

  @Override
  public void setDefaultValues() {
    pointsNameSingular = DEFAULT_SINGULAR_POINTS_NAME;
    pointsNamePlural = DEFAULT_POINTS_NAME;
    trackOnline = true;
    trackOffline = false;
    onlinePayoutInterval = 900000;
    offlinePayoutInterval = 0;
    onlinePayoutAmount = 15;
    offlinePayoutAmount = 0;
  }
}
