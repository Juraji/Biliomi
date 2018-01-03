package nl.juraji.biliomi.rest.services.rest.core;

import nl.juraji.biliomi.components.system.users.UsersService;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.core.UserDao;
import nl.juraji.biliomi.rest.config.ModelRestService;
import nl.juraji.biliomi.rest.config.Responses;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by Juraji on 13-6-2017.
 * Biliomi v3
 */
@Path("/core/users")
public class UserRestService extends ModelRestService<User> {

  @Inject
  private UserDao userDao;

  @Inject
  private UsersService usersService;

  @Override
  public List<User> getEntities() {
    return userDao.getList();
  }

  @Override
  public User getEntity(long id) {
    return userDao.get(id);
  }

  @Override
  public User createEntity(User e) {
    throw new ForbiddenException();
  }

  @Override
  public User updateEntity(User e, long id) {
    User user = userDao.get(id);

    if (user == null) {
      return null;
    }

    // Only some of the properties can be changed
    user.setUserGroup(e.getUserGroup());
    user.setTitle(e.getTitle());
    user.setPoints(e.getPoints());
    user.setRecordedTime(e.getRecordedTime());
    user.setBlacklistedSince(e.getBlacklistedSince());

    // These can only be changed if
    if (user.isFollower() && e.getFollowDate() != null) {
      user.setFollowDate(e.getFollowDate());
    }
    if (user.isSubscriber() && e.getSubscribeDate() != null) {
      user.setFollowDate(e.getSubscribeDate());
    }

    userDao.save(user);

    return user;
  }

  @Override
  public boolean deleteEntity(long id) {
    throw new ForbiddenException();
  }

  @GET
  @Path("/latest/followers")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getLatestFollowers() {
    List<User> followers = userDao.getFollowers(20);
    return toPaginatedResponse(followers);
  }

  @GET
  @Path("/latest/subscribers")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getLatestSubscribers() {
    List<User> subscribers = userDao.getSubscribers(20);
    return toPaginatedResponse(subscribers);
  }

  @GET
  @Path("/byusername/{username}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getUserByUsername(@PathParam("username") String username) {
    User user = usersService.getUser(username);
    return Responses.okOrEmpty(user);
  }
}
