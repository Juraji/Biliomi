package nl.juraji.biliomi.components.integrations.autoupdates.github.api.v3.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Juraji on 6-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "GithubRelease")
@XmlAccessorType(XmlAccessType.FIELD)
public class GithubRelease {

    @XmlElement(name = "html_url")
    private String url;

    @XmlElement(name = "tag_name")
    private String tagName;

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "draft")
    private boolean isDraft;

    @XmlElement(name = "prerelease")
    private boolean isPrerelease;

    @XmlElement(name = "published_at")
    private String publishedAt;

    @XmlElement(name = "assets")
    private List<GithubReleaseAsset> assets;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDraft() {
        return isDraft;
    }

    public void setDraft(boolean draft) {
        isDraft = draft;
    }

    public boolean isPrerelease() {
        return isPrerelease;
    }

    public void setPrerelease(boolean prerelease) {
        isPrerelease = prerelease;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public List<GithubReleaseAsset> getAssets() {
        return assets;
    }

    public void setAssets(List<GithubReleaseAsset> assets) {
        this.assets = assets;
    }
}
