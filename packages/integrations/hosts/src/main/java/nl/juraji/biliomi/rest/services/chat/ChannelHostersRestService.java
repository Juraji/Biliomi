package nl.juraji.biliomi.rest.services.chat;

import nl.juraji.biliomi.components.chat.hosts.HostersService;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.internal.rest.PaginatedResponse;
import nl.juraji.biliomi.rest.config.Responses;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by Juraji on 2-1-2018.
 * Biliomi
 */
@Path("/info/channel")
public class ChannelHostersRestService {

  @Inject
  private HostersService hostersService;

  @GET
  @Path("/hosters")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getChannelHosters() {
    List<User> hosters = hostersService.getHostersAsUsers();

    if (hosters.size() > 0) {
      PaginatedResponse<User> response = new PaginatedResponse<>();
      response.setEntities(hosters);
      response.setTotalAvailable(hosters.size());

      return Responses.ok(response);
    } else {
      return Responses.noContent();
    }
  }
}
