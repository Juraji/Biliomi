package nl.juraji.biliomi.model.internal.rest.query;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 22-12-2017.
 * Biliomi
 */
@XmlRootElement(name = "RestFilterDirective")
@XmlAccessorType(XmlAccessType.FIELD)
public class RestFilterDirective {
    @XmlElement(name = "Property")
    private String property;

    @XmlElement(name = "Operator")
    private RestFilterOperator operator;

    @XmlElement(name = "Value")
    private Object value;

    @XmlElement(name = "Negative")
    private boolean negative = false;

    @XmlElement(name = "OrPrevious")
    private boolean orPrevious = false;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public RestFilterOperator getOperator() {
        return operator;
    }

    public void setOperator(RestFilterOperator operator) {
        this.operator = operator;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isNegative() {
        return negative;
    }

    public void setNegative(boolean negative) {
        this.negative = negative;
    }

    public boolean isOrPrevious() {
        return orPrevious;
    }

    public void setOrPrevious(boolean orPrevious) {
        this.orPrevious = orPrevious;
    }
}
