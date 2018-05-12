package nl.juraji.biliomi.rest.services.rest.games;

import nl.juraji.biliomi.components.games.adventures.services.AdventureRecordService;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.games.AdventureRecord;
import nl.juraji.biliomi.model.games.AdventureRecordDao;
import nl.juraji.biliomi.model.games.UserAdventureRecordStats;
import nl.juraji.biliomi.model.internal.rest.PaginatedResponse;
import nl.juraji.biliomi.rest.config.ModelRestService;
import nl.juraji.biliomi.rest.config.Responses;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by Juraji on 18-6-2017.
 * Biliomi v3
 */
@Path("/games/adventurerecords")
public class AdventureRecordRestService extends ModelRestService<AdventureRecord> {

    @Inject
    private AdventureRecordDao adventureRecordDao;

    @Inject
    private AdventureRecordService adventureRecordService;

    @Context
    private ContainerRequestContext requestContext;

    @GET
    @Path("/stats/{userid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatsForUser(@PathParam("userid") long id) {
        User user = new User();
        user.setId(id);
        UserAdventureRecordStats recordInfo = adventureRecordService.getRecordInfo(user);

        return Responses.okOrEmpty(recordInfo);
    }

    @GET
    @Path("/latest/{userid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLatestRecordsForUser(@PathParam("userid") long id) {
        User user = new User();
        user.setId(id);
        List<AdventureRecord> records = adventureRecordDao.getRecords(user, 10);

        return PaginatedResponse.create(records);
    }

    @Override
    public List<AdventureRecord> getEntities() {
        return adventureRecordDao.getList();
    }

    @Override
    public AdventureRecord getEntity(long id) {
        throw new ForbiddenException();
    }

    @Override
    public AdventureRecord createEntity(AdventureRecord e) {
        throw new ForbiddenException();
    }

    @Override
    public AdventureRecord updateEntity(AdventureRecord e, long id) {
        throw new ForbiddenException();
    }

    @Override
    public boolean deleteEntity(long id) {
        throw new ForbiddenException();
    }
}
