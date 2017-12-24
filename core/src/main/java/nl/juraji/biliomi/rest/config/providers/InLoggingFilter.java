package nl.juraji.biliomi.rest.config.providers;

import nl.juraji.biliomi.rest.RestServerController;
import nl.juraji.biliomi.utility.cdi.annotations.modifiers.LoggerFor;
import org.apache.logging.log4j.Logger;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

/**
 * Created by Juraji on 24-12-2017.
 * Biliomi
 */
@Provider
@Priority(0) // Absolute first
public class InLoggingFilter implements ContainerRequestFilter {

  @Inject
  @LoggerFor(RestServerController.class)
  private Logger logger;

  @Override
  public void filter(ContainerRequestContext containerRequestContext) {
    if (logger.isDebugEnabled()) {
      String method = containerRequestContext.getRequest().getMethod();
      String uri = containerRequestContext.getUriInfo().getRequestUri().toString();
      logger.debug("Incoming request: {} {}", method, uri);
    }
  }
}
