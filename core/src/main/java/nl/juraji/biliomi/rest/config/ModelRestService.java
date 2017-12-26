package nl.juraji.biliomi.rest.config;

import nl.juraji.biliomi.model.internal.rest.PaginatedResponse;
import nl.juraji.biliomi.rest.config.directives.FilterDirectiveQueryProcessor;
import nl.juraji.biliomi.rest.config.directives.LimitQueryProcessor;
import nl.juraji.biliomi.rest.config.directives.SortDirectiveQueryProcessor;
import nl.juraji.biliomi.utility.estreams.EStream;
import nl.juraji.biliomi.utility.types.collections.CIMap;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by Juraji on 13-6-2017.
 * Biliomi v3
 */
public abstract class ModelRestService<T> {

  public abstract List<T> getEntities();

  public abstract T getEntity(long id);

  public abstract T createEntity(T e);

  public abstract T updateEntity(T e, long id);

  public abstract boolean deleteEntity(long id);

  /**
   * Get a list of entities
   * (path "/" is implied by Jersey)
   *
   * @return An OK response with a list of entities or NO_CONTENT on empty result
   * @throws Exception When an internal error occurs
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response restGetEntities(@QueryParam(SortDirectiveQueryProcessor.PARAM_NAME_SORT) String sortDirectiveQuery,
                                  @QueryParam(FilterDirectiveQueryProcessor.PARAM_NAME_FILTER) String filterDirectiveQuery,
                                  @QueryParam(LimitQueryProcessor.PARAM_NAME_LIMIT) Integer limitQuery,
                                  @QueryParam(LimitQueryProcessor.PARAM_NAME_OFFSET) Integer offsetQuery) throws Exception {

    CIMap<Object> queryParams = new CIMap<>();
    queryParams.putIfNotNull(SortDirectiveQueryProcessor.PARAM_NAME_SORT, sortDirectiveQuery);
    queryParams.putIfNotNull(FilterDirectiveQueryProcessor.PARAM_NAME_FILTER, filterDirectiveQuery);
    queryParams.putIfNotNull(LimitQueryProcessor.PARAM_NAME_LIMIT, limitQuery);
    queryParams.putIfNotNull(LimitQueryProcessor.PARAM_NAME_OFFSET, offsetQuery);

    List<T> entities = getEntities();
    int total = entities.size();

    if (queryParams.containsKey(FilterDirectiveQueryProcessor.PARAM_NAME_FILTER)) {
      entities = new FilterDirectiveQueryProcessor<T>().process(entities, queryParams);
      total = entities.size();
    }

    if (queryParams.containsKey(SortDirectiveQueryProcessor.PARAM_NAME_SORT)) {
      entities = new SortDirectiveQueryProcessor<T>().process(entities, queryParams);
    }

    if (queryParams.containsKey(LimitQueryProcessor.PARAM_NAME_LIMIT)) {
      entities = new LimitQueryProcessor<T>().process(entities, queryParams);
    }

    return toPaginatedResponse(entities, total);
  }

  /**
   * Get a single entity identified with the given id in the path
   *
   * @param id The database identifier for the requested entity
   * @return An OK response with the entity or NO_CONTENT on empty result
   */
  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response restGetEntity(@PathParam("id") long id) {
    return Responses.okOrEmpty(getEntity(id));
  }

  /**
   * Create a new entity, saving it to the database
   *
   * @param e The entity to create
   * @return An OK response with the new entity or NOT_MODIFIED on creation failure
   * @throws Exception When an internal error occurs
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response restCreateEntity(T e) throws Exception {
    if (e == null || checkNotNullProperties(e)) {
      return Responses.badRequest();
    }

    Object id = PropertyUtils.getProperty(e, "id");
    if (id != null) {
      if (Long.class.isAssignableFrom(id.getClass()) && (long) id > 0) {
        return Responses.badRequest();
      }
    }

    try {
      T entity = createEntity(e);

      if (entity == null) {
        return Responses.notModified();
      }

      return Responses.ok(entity);
    } catch (ConstraintViolationException err) {
      return Responses.alreadyExists();
    }
  }

  /**
   * Update an entity
   *
   * @param e  The entity body with updated values
   * @param id The database identifier for the updated entity
   * @return An OK response with the entity or NOT_MODIFIED when no modification took place
   */
  @PUT
  @Path("/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response restUpdateEntity(T e, @PathParam("id") long id) {
    if (e == null || checkNotNullProperties(e)) {
      return Responses.badRequest();
    }

    T entity = updateEntity(e, id);

    if (entity == null) {
      return Responses.notModified();
    }

    return Responses.ok(entity);
  }

  /**
   * Delete an entity
   *
   * @param id The database identifier for the entity to delete
   * @return An OK response with the entity or NOT_MODIFIED when no modification took place
   */
  @DELETE
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response restDeleteEntity(@PathParam("id") long id) {
    boolean success = false;

    try {
      success = deleteEntity(id);
    } catch (HibernateException ignored) {
      // Deletion failed, send Not Modified
    }

    if (success) {
      return Responses.ok();
    } else {
      return Responses.notModified();
    }
  }

  /**
   * Check if any of the @NotNull properties is null or empty
   *
   * @param object The object to check
   * @return True if a NotNull proeprty is null or empty, else False
   */
  protected boolean checkNotNullProperties(T object) {
    return EStream.from(object.getClass().getDeclaredFields())
        .filter(field -> field.isAnnotationPresent(NotNull.class))
        .anyMatch(field -> {
          Object property = PropertyUtils.getProperty(object, field.getName());
          return property == null || (String.class.isAssignableFrom(property.getClass()) && StringUtils.isEmpty((String) property));
        });
  }

  protected Response toPaginatedResponse(List<T> entities) {
    return toPaginatedResponse(entities, entities.size());
  }

  protected Response toPaginatedResponse(List<T> entities, int totalAvailable) {

    if (entities.isEmpty()) {
      return Responses.noContent();
    } else {
      PaginatedResponse<T> response = new PaginatedResponse<>();

      response.setEntities(entities);
      response.setTotalAvailable(totalAvailable);

      return Responses.ok(response);
    }
  }
}
