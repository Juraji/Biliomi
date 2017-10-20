package nl.juraji.biliomi.rest.services.chat;

import nl.juraji.biliomi.model.chat.ModerationRecord;
import nl.juraji.biliomi.model.chat.ModerationRecordDao;
import nl.juraji.biliomi.rest.config.ModelRestService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by Juraji on 17-6-2017.
 * Biliomi v3
 */
@Path("/chat/moderationrecords")
public class ModerationRecordRestService extends ModelRestService<ModerationRecord> {

  @Inject
  private ModerationRecordDao moderationRecordDao;

  @GET
  @Path("/user/{userid}")
  @Produces(MediaType.APPLICATION_JSON)
  public List<ModerationRecord> getForUser(@PathParam("userid") Long userId) throws Exception {
    return moderationRecordDao.getRecords(userId);
  }

  @Override
  public List<ModerationRecord> getEntities() {
    return moderationRecordDao.getList();
  }

  @Override
  public ModerationRecord getEntity(long id) {
    throw new ForbiddenException();
  }

  @Override
  public ModerationRecord createEntity(ModerationRecord e) {
    throw new ForbiddenException();
  }

  @Override
  public ModerationRecord updateEntity(ModerationRecord e, long id) {
    throw new ForbiddenException();
  }

  @Override
  public boolean deleteEntity(long id) {
    throw new ForbiddenException();
  }
}
