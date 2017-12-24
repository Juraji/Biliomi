package nl.juraji.biliomi.rest.config.providers;

import nl.juraji.biliomi.model.internal.rest.ServerErrorResponse;
import nl.juraji.biliomi.rest.config.Responses;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by Juraji on 24-12-2017.
 * Biliomi
 */
@Provider
public class ExceptionResponseMapper implements ExceptionMapper<Throwable> {

  @Inject
  private Logger logger;

  @Override
  public Response toResponse(Throwable throwable) {

    if (logger.isDebugEnabled()) {
      ServerErrorResponse response = new ServerErrorResponse();

      response.setErrorMessage(throwable.getMessage());

      if (throwable.getCause() != null) {
        response.setCausedBy(throwable.getCause().getMessage());
      }

      return Responses.serverError(response);
    } else {
      return Responses.serverError();
    }
  }
}
