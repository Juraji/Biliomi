package nl.juraji.biliomi.io.api.steam.v1.model.library;

import nl.juraji.biliomi.io.api.steam.v1.model.wrappers.SteamResponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Juraji on 25-5-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "SteamLibraryResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class SteamLibraryResponse extends SteamResponse<SteamLibrary> {
}
