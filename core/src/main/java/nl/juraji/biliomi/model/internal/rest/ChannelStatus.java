package nl.juraji.biliomi.model.internal.rest;

import nl.juraji.biliomi.model.core.Game;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 26-6-2017.
 * Biliomi v3
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ChannelStatus {

    @XmlElement(name = "LogoUri")
    private String logoUri;

    @XmlElement(name = "PreviewUri")
    private String previewUri;

    @XmlElement(name = "Affiliate")
    private boolean affiliate;

    @XmlElement(name = "Partner")
    private boolean partner;

    @XmlElement(name = "ChannelName")
    private String channelName;

    @XmlElement(name = "IsOnline")
    private boolean isOnline;

    @XmlElement(name = "Status")
    private String status;

    @XmlElement(name = "StatusWithoutTemplate")
    private String statusWithoutTemplate;

    @XmlElement(name = "CurrentGame")
    private Game game;

    @XmlElement(name = "FollowerCount")
    private long followerCount;

    @XmlElement(name = "SubscriberCount")
    private long subscriberCount;

    public String getLogoUri() {
        return logoUri;
    }

    public void setLogoUri(String logoUri) {
        this.logoUri = logoUri;
    }

    public String getPreviewUri() {
        return previewUri;
    }

    public void setPreviewUri(String previewUri) {
        this.previewUri = previewUri;
    }

    public boolean isAffiliate() {
        return affiliate;
    }

    public void setAffiliate(boolean affiliate) {
        this.affiliate = affiliate;
    }

    public boolean isPartner() {
        return partner;
    }

    public void setPartner(boolean partner) {
        this.partner = partner;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusWithoutTemplate() {
        return statusWithoutTemplate;
    }

    public void setStatusWithoutTemplate(String statusWithoutTemplate) {
        this.statusWithoutTemplate = statusWithoutTemplate;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public long getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(long followerCount) {
        this.followerCount = followerCount;
    }

    public long getSubscriberCount() {
        return subscriberCount;
    }

    public void setSubscriberCount(long subscriberCount) {
        this.subscriberCount = subscriberCount;
    }
}
