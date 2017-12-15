package nl.juraji.biliomi.rest.services.rest.core;

import nl.juraji.biliomi.components.system.channel.CommunitiesService;
import nl.juraji.biliomi.model.core.Community;
import nl.juraji.biliomi.rest.config.ModelRestService;
import nl.juraji.biliomi.rest.config.Responses;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by Juraji on 15-12-2017.
 * Biliomi
 */
@Path("/core/communities")
public class CommunityRestService extends ModelRestService<Community> {

  @Inject
  private CommunitiesService communitiesService;

  @Override
  public List<Community> getEntities() {
    return communitiesService.getCommunities();
  }

  @Override
  public Community getEntity(long id) {
    return communitiesService.getCommunity(id);
  }

  @Override
  public Community createEntity(Community e) {
    throw new ForbiddenException();
  }

  @Override
  public Community updateEntity(Community e, long id) {
    throw new ForbiddenException();
  }

  @Override
  public boolean deleteEntity(long id) {
    throw new ForbiddenException();
  }

  @GET
  @Path("/search/{communityname}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response searchCommunity(@PathParam("communityname") String communityName) {
    return Responses.okOrEmpty(communitiesService.getCommunityByName(communityName));
  }
}
