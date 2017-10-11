package nl.juraji.biliomi.io.api.patreon.v1.model.responses;

import nl.juraji.biliomi.io.api.patreon.v1.model.PatreonDataWrapper;
import nl.juraji.biliomi.io.api.patreon.v1.model.users.PatreonUser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 11-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "PatreonUserResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class PatreonUserResponse extends PatreonDataWrapper<PatreonUser> {
}
