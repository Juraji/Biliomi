package nl.juraji.biliomi.model.internal.rest;

import nl.juraji.biliomi.rest.config.Responses;

import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Juraji on 26-12-2017.
 * Biliomi
 */
@XmlRootElement(name = "PaginatedResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaginatedResponse<T> {

    @XmlElement(name = "Entities")
    private List<T> entities;

    @XmlElement(name = "TotalAvailable")
    private int totalAvailable;

    public static <T> Response create(List<T> list) {
        return create(list, (list == null ? 0 : list.size()));
    }

    public static <T> Response create(List<T> list, int total) {
        if (list != null && !list.isEmpty()) {
            PaginatedResponse<T> response = new PaginatedResponse<>();
            response.setEntities(list);
            response.setTotalAvailable(total);

            return Responses.ok(response);
        } else {
            return Responses.noContent();
        }
    }

    public List<T> getEntities() {
        return entities;
    }

    public void setEntities(List<T> entities) {
        this.entities = entities;
    }

    public int getTotalAvailable() {
        return totalAvailable;
    }

    public void setTotalAvailable(int totalAvailable) {
        this.totalAvailable = totalAvailable;
    }
}
