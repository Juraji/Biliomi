package nl.juraji.biliomi.components.integrations.steam.api.v1.model.wrappers;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 5-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "SteamResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class SteamResponse<T> {

    @XmlElement(name = "response")
    private T response;

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }
}
