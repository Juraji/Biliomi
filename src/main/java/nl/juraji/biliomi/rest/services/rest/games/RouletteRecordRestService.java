package nl.juraji.biliomi.rest.services.rest.games;

import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.games.RouletteRecord;
import nl.juraji.biliomi.model.games.RouletteRecordDao;
import nl.juraji.biliomi.model.games.UserRecordStats;
import nl.juraji.biliomi.components.games.roulette.RouletteRecordService;
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
@Path("/games/rouletterecords")
public class RouletteRecordRestService extends ModelRestService<RouletteRecord> {

  @Inject
  private RouletteRecordDao RouletteRecordDao;

  @Inject
  private RouletteRecordService rouletteRecordService;

  @GET
  @Path("/stats/{userid}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getStatsForUser(@PathParam("userid") long id) {
    User user = new User();
    user.setId(id);
    UserRecordStats recordInfo = rouletteRecordService.getRecordInfo(user);

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
    List<RouletteRecord> records = RouletteRecordDao.getRecords(user, 10);

    return Responses.okOrEmpty(records);
  }

  @Override
  public List<RouletteRecord> getEntities() {
    return RouletteRecordDao.getList();
  }

  @Override
  public RouletteRecord getEntity(long id) {
    throw new ForbiddenException();
  }

  @Override
  public RouletteRecord createEntity(RouletteRecord e) {
    throw new ForbiddenException();
  }

  @Override
  public RouletteRecord updateEntity(RouletteRecord e, long id) {
    throw new ForbiddenException();
  }

  @Override
  public boolean deleteEntity(long id) {
    throw new ForbiddenException();
  }
}
