package nl.juraji.biliomi.io.api.patreon.v1.model.users;

import nl.juraji.biliomi.io.api.patreon.v1.model.PatreonObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 11-10-2017.
 * Biliomi
 */
@XmlRootElement(name = "PatreonUser")
@XmlAccessorType(XmlAccessType.FIELD)
public class PatreonUser extends PatreonObject<PatreonUserAttributes, PatreonUserRelationShips> {
}
