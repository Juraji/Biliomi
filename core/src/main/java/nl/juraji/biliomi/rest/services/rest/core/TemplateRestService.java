package nl.juraji.biliomi.rest.services.rest.core;

import nl.juraji.biliomi.model.core.Template;
import nl.juraji.biliomi.model.core.TemplateDao;
import nl.juraji.biliomi.rest.config.ModelRestService;
import nl.juraji.biliomi.rest.config.Responses;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by Juraji on 17-6-2017.
 * Biliomi v3
 */
@Path("/core/templates")
public class TemplateRestService extends ModelRestService<Template> {

    @Inject
    private TemplateDao templateDao;

    @Override
    public List<Template> getEntities() {
        return templateDao.getList();
    }

    @Override
    public Template getEntity(long id) {
        return templateDao.get(id);
    }

    @Override
    public Template createEntity(Template e) {
        throw new ForbiddenException();
    }

    @Override
    public Template updateEntity(Template e, long id) {
        Template template = templateDao.get(id);

        if (template == null) {
            return null;
        }

        if (StringUtils.isEmpty(e.getTemplate())) {
            template.setTemplate(null);
        } else {
            template.setTemplate(e.getTemplate());
        }

        templateDao.save(template);
        return template;
    }

    @Override
    public boolean deleteEntity(long id) {
        throw new ForbiddenException();
    }

    @GET
    @Path("/bykey/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTemplateByKey(@PathParam("key") String key) {
        Template template = templateDao.getByKey(key);

        if (template == null) {
            return Responses.noContent();
        }

        return Responses.ok(template);
    }
}
