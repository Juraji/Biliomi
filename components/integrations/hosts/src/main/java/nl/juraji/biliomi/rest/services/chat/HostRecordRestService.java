package nl.juraji.biliomi.rest.services.chat;

import nl.juraji.biliomi.model.chat.HostRecord;
import nl.juraji.biliomi.model.chat.HostRecordDao;
import nl.juraji.biliomi.model.core.Direction;
import nl.juraji.biliomi.rest.config.ModelRestService;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Created by Juraji on 18-6-2017.
 * Biliomi v3
 */
@Path("/chat/hostrecords")
public class HostRecordRestService extends ModelRestService<HostRecord> {

  @Inject
  private HostRecordDao hostRecordDao;

  @QueryParam("direction")
  private Direction direction;

  @Override
  public List<HostRecord> getEntities() {

    if (direction != null) {
      return hostRecordDao.getListByDirection(direction);
    } else {
      return hostRecordDao.getList();
    }
  }

  @Override
  public HostRecord getEntity(long id) {
    throw new ForbiddenException();
  }

  @Override
  public HostRecord createEntity(HostRecord e) {
    hostRecordDao.save(e);
    return e;
  }

  @Override
  public HostRecord updateEntity(HostRecord e, long id) {
    throw new ForbiddenException();
  }

  @Override
  public boolean deleteEntity(long id) {
    HostRecord hostRecord = hostRecordDao.get(id);

    if (hostRecord == null) {
      return false;
    }

    hostRecordDao.delete(hostRecord);
    return true;
  }
}
