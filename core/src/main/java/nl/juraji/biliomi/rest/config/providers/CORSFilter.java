package nl.juraji.biliomi.rest.config.providers;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Created by Juraji on 15-6-2017.
 * Biliomi v3
 */
@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class CORSFilter implements ContainerResponseFilter {
  @Override
  public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {
    MultivaluedMap<String, Object> headers = containerResponseContext.getHeaders();

    headers.add("Access-Control-Allow-Origin", containerRequestContext.getHeaders().getFirst("Origin"));
    headers.add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
    headers.add("Access-Control-Allow-Credentials", "true");
    headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
  }
}
