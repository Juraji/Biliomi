package nl.juraji.biliomi.rest.services.chat;

import nl.juraji.biliomi.components.chat.hosts.HostersService;
import nl.juraji.biliomi.model.internal.rest.PaginatedResponse;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Juraji on 2-1-2018.
 * Biliomi
 */
@Path("/channel")
public class ChannelHostersRestService {

    @Inject
    private HostersService hostersService;

    @GET
    @Path("/hosters")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChannelHosters() {
        return PaginatedResponse.create(hostersService.getHostersAsUsers());
    }
}
