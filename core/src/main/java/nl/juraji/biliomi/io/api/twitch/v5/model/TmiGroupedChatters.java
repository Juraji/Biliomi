package nl.juraji.biliomi.io.api.twitch.v5.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Juraji on 20-4-2017.
 * Biliomi v3
 */
@XmlRootElement(name = "TmiGroupedChatters")
@XmlAccessorType(XmlAccessType.FIELD)
public class TmiGroupedChatters {

    @XmlElement(name = "moderators")
    private List<String> moderators;

    @XmlElement(name = "staff")
    private List<String> staff;

    @XmlElement(name = "admins")
    private List<String> admins;

    @XmlElement(name = "global_mods")
    private List<String> globalMods;

    @XmlElement(name = "viewers")
    private List<String> viewers;

    public List<String> getModerators() {
        return moderators;
    }

    public void setModerators(List<String> moderators) {
        this.moderators = moderators;
    }

    public List<String> getStaff() {
        return staff;
    }

    public void setStaff(List<String> staff) {
        this.staff = staff;
    }

    public List<String> getAdmins() {
        return admins;
    }

    public void setAdmins(List<String> admins) {
        this.admins = admins;
    }

    public List<String> getGlobalMods() {
        return globalMods;
    }

    public void setGlobalMods(List<String> globalMods) {
        this.globalMods = globalMods;
    }

    public List<String> getViewers() {
        return viewers;
    }

    public void setViewers(List<String> viewers) {
        this.viewers = viewers;
    }

    public List<String> getAll() {
        List<String> all = new ArrayList<>();
        all.addAll(moderators);
        all.addAll(staff);
        all.addAll(admins);
        all.addAll(globalMods);
        all.addAll(viewers);
        return all;
    }
}
