package nl.juraji.biliomi.rest.services.rest.core;

import nl.juraji.biliomi.model.core.CommandHistoryRecord;
import nl.juraji.biliomi.model.core.CommandHistoryRecordDao;
import nl.juraji.biliomi.rest.config.ModelRestService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by Juraji on 17-6-2017.
 * Biliomi v3
 */
@Path("/core/commandhistoryrecords")
public class CommandHistoryRecordRestService extends ModelRestService<CommandHistoryRecord> {

  @Inject
  private CommandHistoryRecordDao commandHistoryRecordDao;

  @GET
  @Path("/latest/{command}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getHistoryForCommand(@PathParam("command") String command) {
    List<CommandHistoryRecord> records = commandHistoryRecordDao.getLatestHistoryForCommand(command);
    return toPaginatedResponse(records);
  }

  @Override
  public List<CommandHistoryRecord> getEntities() {
    return commandHistoryRecordDao.getList();
  }

  @Override
  public CommandHistoryRecord getEntity(long id) {
    return commandHistoryRecordDao.get(id);
  }

  @Override
  public CommandHistoryRecord createEntity(CommandHistoryRecord e) {
    throw new ForbiddenException();
  }

  @Override
  public CommandHistoryRecord updateEntity(CommandHistoryRecord e, long id) {
    throw new ForbiddenException();
  }

  @Override
  public boolean deleteEntity(long id) {
    throw new ForbiddenException();
  }
}
