package nl.juraji.biliomi.model.core;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 4-5-2017.
 * Biliomi v3
 */
@Entity
@XmlRootElement(name = "CustomCommand")
@XmlAccessorType(XmlAccessType.FIELD)
@OnDelete(action = OnDeleteAction.CASCADE)
public class CustomCommand extends Command {

    @Column
    @NotNull
    @XmlElement(name = "Message")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
