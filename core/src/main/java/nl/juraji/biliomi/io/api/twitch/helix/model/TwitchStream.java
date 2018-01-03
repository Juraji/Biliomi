package nl.juraji.biliomi.io.api.twitch.helix.model;

import org.joda.time.DateTime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Juraji on 3-1-2018.
 * Biliomi
 */
@XmlRootElement(name = "TwitchStream")
@XmlAccessorType(XmlAccessType.FIELD)
public class TwitchStream {

  @XmlElement(name = "id")
  private String id;

  @XmlElement(name = "user_id")
  private String userId;

  @XmlElement(name = "game_id")
  private String gameId;

  @XmlElement(name = "community_ids")
  private List<String> communityIds;

  @XmlElement(name = "type")
  private TwitchStreamType type;

  @XmlElement(name = "title")
  private String title;

  @XmlElement(name = "viewer_count")
  private Integer viewerCount;

  @XmlElement(name = "started_at")
  private DateTime startedAt;

  @XmlElement(name = "language")
  private String language;

  @XmlElement(name = "thumbnail_url")
  private String thumbnailUrl;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getGameId() {
    return gameId;
  }

  public void setGameId(String gameId) {
    this.gameId = gameId;
  }

  public List<String> getCommunityIds() {
    return communityIds;
  }

  public void setCommunityIds(List<String> communityIds) {
    this.communityIds = communityIds;
  }

  public TwitchStreamType getType() {
    return type;
  }

  public void setType(TwitchStreamType type) {
    this.type = type;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Integer getViewerCount() {
    return viewerCount;
  }

  public void setViewerCount(Integer viewerCount) {
    this.viewerCount = viewerCount;
  }

  public DateTime getStartedAt() {
    return startedAt;
  }

  public void setStartedAt(DateTime startedAt) {
    this.startedAt = startedAt;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getThumbnailUrl() {
    return thumbnailUrl;
  }

  public void setThumbnailUrl(String thumbnailUrl) {
    this.thumbnailUrl = thumbnailUrl;
  }
}
