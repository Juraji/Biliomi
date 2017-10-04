package nl.juraji.biliomi.rest.services.rest.games;

import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.games.KillRecord;
import nl.juraji.biliomi.model.games.KillRecordDao;
import nl.juraji.biliomi.model.games.UserKDRRecordStats;
import nl.juraji.biliomi.components.games.killgame.KillRecordService;
import nl.juraji.biliomi.rest.config.ModelRestService;
import nl.juraji.biliomi.rest.config.Responses;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by Juraji on 18-6-2017.
 * Biliomi v3
 */
@Path("/games/killrecords")
public class KillRecordRestService extends ModelRestService<KillRecord> {

  @Inject
  private KillRecordDao killRecordDao;

  @Inject
  private KillRecordService killRecordService;

  @GET
  @Path("/stats/{userid}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getStatsForUser(@PathParam("userid") long id) {
    User user = new User();
    user.setId(id);
    UserKDRRecordStats recordInfo = killRecordService.getKDR(user);

    if (recordInfo == null) {
      return Responses.noContent();
    } else {
      return Responses.ok(recordInfo);
    }
  }

  @GET
  @Path("/latest/{userid}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getLatestRecordsForUser(@PathParam("userid") long id) {
    User user = new User();
    user.setId(id);
    List<KillRecord> records = killRecordDao.getRecords(user, 10);

    return Responses.okOrEmpty(records);
  }

  @Override
  public List<KillRecord> getEntities() {
    return killRecordDao.getList();
  }

  @Override
  public KillRecord getEntity(long id) {
    throw new ForbiddenException();
  }

  @Override
  public KillRecord createEntity(KillRecord e) {
    throw new ForbiddenException();
  }

  @Override
  public KillRecord updateEntity(KillRecord e, long id) {
    throw new ForbiddenException();
  }

  @Override
  public boolean deleteEntity(long id) {
    throw new ForbiddenException();
  }
}
