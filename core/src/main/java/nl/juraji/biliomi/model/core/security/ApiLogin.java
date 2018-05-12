package nl.juraji.biliomi.model.core.security;

import nl.juraji.biliomi.model.core.User;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

/**
 * Created by Juraji on 18-6-2017.
 * Biliomi v3
 */
@Embeddable
@XmlRootElement(name = "ApiLogin")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApiLogin {

    @OneToOne
    @NotNull
    @XmlElement(name = "User")
    private User user;

    @Column
    @NotNull
    @XmlTransient
    private byte[] password;

    @Column
    @NotNull
    @XmlTransient
    private byte[] salt;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }
}
