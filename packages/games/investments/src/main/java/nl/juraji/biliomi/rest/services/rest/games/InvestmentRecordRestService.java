package nl.juraji.biliomi.rest.services.rest.games;

import nl.juraji.biliomi.components.games.investments.InvestmentService;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.games.InvestmentRecord;
import nl.juraji.biliomi.model.games.InvestmentRecordDao;
import nl.juraji.biliomi.model.games.UserInvestRecordStats;
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
@Path("/games/investmentrecords")
public class InvestmentRecordRestService extends ModelRestService<InvestmentRecord> {

  @Inject
  private InvestmentRecordDao investmentRecordDao;

  @Inject
  private InvestmentService investmentService;

  @GET
  @Path("/stats/{userid}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getStatsForUser(@PathParam("userid") long id) {
    User user = new User();
    user.setId(id);
    UserInvestRecordStats recordInfo = investmentService.getRecordInfo(user);

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
    List<InvestmentRecord> records = investmentRecordDao.getRecords(user, 10);

    return Responses.okOrEmpty(records);
  }

  @Override
  public List<InvestmentRecord> getEntities() {
    return investmentRecordDao.getList();
  }

  @Override
  public InvestmentRecord getEntity(long id) {
    throw new ForbiddenException();
  }

  @Override
  public InvestmentRecord createEntity(InvestmentRecord e) {
    throw new ForbiddenException();
  }

  @Override
  public InvestmentRecord updateEntity(InvestmentRecord e, long id) {
    throw new ForbiddenException();
  }

  @Override
  public boolean deleteEntity(long id) {
    throw new ForbiddenException();
  }
}
