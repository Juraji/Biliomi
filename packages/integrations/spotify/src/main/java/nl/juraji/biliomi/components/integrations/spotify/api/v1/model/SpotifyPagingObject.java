package nl.juraji.biliomi.components.integrations.spotify.api.v1.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;

/**
 * Created by Juraji on 30-9-2017.
 * Biliomi
 */
@XmlRootElement(name = "SpotifyPagingObject")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class SpotifyPagingObject<T> {

    @XmlElement(name = "items")
    private Set<T> items;

    @XmlElement(name = "next")
    private String next;

    @XmlElement(name = "total")
    private int total;

    public Set<T> getItems() {
        return items;
    }

    public void setItems(Set<T> items) {
        this.items = items;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
