package nl.juraji.biliomi.rest.config;

import nl.juraji.biliomi.model.core.settings.Settings;
import nl.juraji.biliomi.components.system.settings.SettingsService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Juraji on 17-6-2017.
 * Biliomi v3
 */
public abstract class SettingsModelRestService<T extends Settings> {

  @Inject
  protected SettingsService settingsService;

  public abstract T getEntity();

  public abstract T updateEntity(T e);

  /**
   * Get the Settings type entity
   *
   * @return An OK response with the entity or NO_CONTENT on empty result
   * @throws Exception When an internal error occurs
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response restGetEntity() throws Exception {
    T entity = getEntity();

    if (entity == null) {
      return Responses.noContent();
    }

    return Responses.ok(entity);
  }

  /**
   * Update the Settings type entity
   *
   * @param e  The entity body with updated values
   * @return An OK response with the entity or NOT_MODIFIED when no modification took place
   * @throws Exception When an internal error occurs
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response restUpdateEntity(T e) throws Exception {
    T entity = updateEntity(e);

    if (entity == null) {
      return Responses.notModified();
    }

    return Responses.ok(entity);
  }
}
