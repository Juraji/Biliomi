package nl.juraji.biliomi.rest.services.chat;

import nl.juraji.biliomi.model.chat.UserGreeting;
import nl.juraji.biliomi.model.chat.UserGreetingDao;
import nl.juraji.biliomi.rest.config.ModelRestService;

import javax.inject.Inject;
import javax.ws.rs.Path;
import java.util.List;

/**
 * Created by Juraji on 17-6-2017.
 * Biliomi v3
 */
@Path("/chat/usergreetings")
public class UserGreetingRestService extends ModelRestService<UserGreeting> {

  @Inject
  private UserGreetingDao userGreetingDao;

  @Override
  public List<UserGreeting> getEntities() {
    return userGreetingDao.getList();
  }

  @Override
  public UserGreeting getEntity(long id) {
    return userGreetingDao.get(id);
  }

  @Override
  public UserGreeting createEntity(UserGreeting e) {
    userGreetingDao.save(e);
    return e;
  }

  @Override
  public UserGreeting updateEntity(UserGreeting e, long id) {
    UserGreeting greeting = userGreetingDao.get(id);

    if (greeting == null) {
      return null;
    }

    greeting.setMessage(e.getMessage());
    userGreetingDao.save(greeting);
    return greeting;
  }

  @Override
  public boolean deleteEntity(long id) {
    UserGreeting greeting = userGreetingDao.get(id);

    if (greeting == null) {
      return false;
    }

    userGreetingDao.delete(greeting);
    return true;
  }
}
