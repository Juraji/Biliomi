package nl.juraji.biliomi.rest.services.rest.authenticate;

import nl.juraji.biliomi.model.core.VersionInfo;
import nl.juraji.biliomi.rest.config.Responses;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Juraji on 21-1-2018.
 * Biliomi
 */
@Path("/version")
public class VersionInfoRestService {

    @Inject
    private VersionInfo versionInfo;

    @GET
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVersionInfo() {
        return Responses.ok(versionInfo);
    }
}
