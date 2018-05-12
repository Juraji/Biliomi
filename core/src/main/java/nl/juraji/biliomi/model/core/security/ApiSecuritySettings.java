package nl.juraji.biliomi.model.core.security;

import nl.juraji.biliomi.model.core.settings.Settings;
import nl.juraji.biliomi.utility.factories.ModelUtils;
import nl.juraji.biliomi.utility.types.TokenGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.*;
import java.util.Set;

/**
 * Created by Juraji on 16-6-2017.
 * Biliomi v3
 */
@Entity
@XmlRootElement(name = "ApiSecuritySettings")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApiSecuritySettings extends Settings {

    @Column
    @NotNull
    @XmlTransient
    private byte[] secret;

    @Column
    @XmlElement(name = "Logins")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ApiLogin", joinColumns = @JoinColumn(name = "settings_type"))
    private Set<ApiLogin> logins;

    public byte[] getSecret() {
        return secret;
    }

    public Set<ApiLogin> getLogins() {
        logins = ModelUtils.initCollection(logins);
        return logins;
    }

    @Override
    public void setDefaultValues() {
        secret = DatatypeConverter.parseBase64Binary(new TokenGenerator(20).generate());
    }
}
