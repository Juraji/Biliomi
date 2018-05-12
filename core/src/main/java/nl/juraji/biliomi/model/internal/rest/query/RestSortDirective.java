package nl.juraji.biliomi.model.internal.rest.query;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 27-11-2017.
 * Biliomi
 */
@XmlRootElement(name = "RestSortDirective")
@XmlAccessorType(XmlAccessType.FIELD)
public class RestSortDirective {

    @XmlElement(name = "Property")
    private String property;

    @XmlElement(name = "Descending")
    private boolean descending;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public boolean isDescending() {
        return descending;
    }

    public void setDescending(boolean descending) {
        this.descending = descending;
    }
}
