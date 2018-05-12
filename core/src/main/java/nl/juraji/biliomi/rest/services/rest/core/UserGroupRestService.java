package nl.juraji.biliomi.rest.services.rest.core;

import nl.juraji.biliomi.model.core.UserGroup;
import nl.juraji.biliomi.model.core.UserGroupDao;
import nl.juraji.biliomi.rest.config.ModelRestService;
import nl.juraji.biliomi.rest.config.Responses;
import nl.juraji.biliomi.utility.calculate.MathUtils;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by Juraji on 17-6-2017.
 * Biliomi v3
 */
@Path("/core/usergroups")
public class UserGroupRestService extends ModelRestService<UserGroup> {

    @Inject
    private UserGroupDao userGroupDao;

    @GET
    @Path("/default")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDefault() {
        UserGroup defaultGroup = userGroupDao.getDefaultGroup();

        if (defaultGroup == null) {
            return Responses.noContent();
        } else {
            return Responses.ok(defaultGroup);
        }
    }

    @Override
    public List<UserGroup> getEntities() {
        return userGroupDao.getList();
    }

    @Override
    public UserGroup getEntity(long id) {
        return userGroupDao.get(id);
    }

    @Override
    public UserGroup createEntity(UserGroup e) {
        if (e != null) {
            e.setWeight(MathUtils.minMax(e.getWeight(), 0, 1000));
            e.setDefaultGroup(false);
        }

        userGroupDao.save(e);
        return e;
    }

    @Override
    public UserGroup updateEntity(UserGroup e, long id) {
        UserGroup userGroup = userGroupDao.get(id);

        if (userGroup == null) {
            return null;
        }

        userGroup.setName(e.getName());

        // These can only be changed on non-default groups
        if (!userGroup.isDefaultGroup()) {
            userGroup.setWeight(e.getWeight());
            userGroup.setLevelUpHours(e.getLevelUpHours());
        }

        userGroupDao.save(userGroup);
        return userGroup;
    }

    @Override
    public boolean deleteEntity(long id) {
        UserGroup userGroup = userGroupDao.get(id);

        if (userGroup == null || userGroup.isDefaultGroup()) {
            return false;
        }

        userGroupDao.delete(userGroup);
        return true;
    }
}
