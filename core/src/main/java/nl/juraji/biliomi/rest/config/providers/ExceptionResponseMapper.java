package nl.juraji.biliomi.rest.config.providers;

import nl.juraji.biliomi.BiliomiContainer;
import nl.juraji.biliomi.model.internal.rest.ServerErrorResponse;
import nl.juraji.biliomi.rest.config.Responses;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by Juraji on 24-12-2017.
 * Biliomi
 */
@Provider
public class ExceptionResponseMapper implements ExceptionMapper<Throwable> {

  @Override
  public Response toResponse(Throwable throwable) {
    if (BiliomiContainer.getParameters().isDebugMode()) {
      ServerErrorResponse response = new ServerErrorResponse();

      response.setErrorMessage(throwable.getMessage());
      response.setStackTrace(throwable.getStackTrace());

      if (throwable.getCause() != null) {
        response.setCausedBy(throwable.getCause().getMessage());
      }

      return Responses.serverError(response);
    } else {
      return Responses.serverError();
    }
  }
}
