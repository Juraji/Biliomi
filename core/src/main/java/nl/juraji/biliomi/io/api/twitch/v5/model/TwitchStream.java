package nl.juraji.biliomi.io.api.twitch.v5.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 20-4-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "TwitchStream")
@XmlAccessorType(XmlAccessType.FIELD)
public class TwitchStream {

    @XmlElement(name = "_id")
    private Long id;

    @XmlElement(name = "game")
    private String game;

    @XmlElement(name = "viewers")
    private Integer viewers;

    @XmlElement(name = "video_height")
    private Integer videoHeight;

    @XmlElement(name = "average_fps")
    private Integer averageFps;

    @XmlElement(name = "delay")
    private Integer delay;

    @XmlElement(name = "created_at")
    private String createdAt;

    @XmlElement(name = "is_playlist")
    private Boolean isPlaylist;

    @XmlElement(name = "preview")
    private TwitchImageInfo preview;

    @XmlElement(name = "channel")
    private TwitchChannel channel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public Integer getViewers() {
        return viewers;
    }

    public void setViewers(Integer viewers) {
        this.viewers = viewers;
    }

    public Integer getVideoHeight() {
        return videoHeight;
    }

    public void setVideoHeight(Integer videoHeight) {
        this.videoHeight = videoHeight;
    }

    public Integer getAverageFps() {
        return averageFps;
    }

    public void setAverageFps(Integer averageFps) {
        this.averageFps = averageFps;
    }

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getPlaylist() {
        return isPlaylist;
    }

    public void setPlaylist(Boolean playlist) {
        isPlaylist = playlist;
    }

    public TwitchImageInfo getPreview() {
        return preview;
    }

    public void setPreview(TwitchImageInfo preview) {
        this.preview = preview;
    }

    public TwitchChannel getChannel() {
        return channel;
    }

    public void setChannel(TwitchChannel channel) {
        this.channel = channel;
    }
}
