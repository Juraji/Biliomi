package nl.juraji.biliomi.rest.config;


import javax.ws.rs.core.Response;
import java.util.Collection;

/**
 * Created by Juraji on 16-6-2017.
 * Biliomi v3
 */
public final class Responses {

    /**
     * Returned when the request succeeded
     *
     * @return A Response object
     */
    public static Response ok() {
        return Response.ok().build();
    }

    /**
     * Returned when the request succeeded
     *
     * @param entity The involved entity
     * @return A Response object
     */
    public static Response ok(Object entity) {
        return Response.ok(entity).build();
    }

    /**
     * Returned when the request succeeded
     *
     * @param entity The involved entity
     * @return A Response object
     */
    public static Response okOrEmpty(Object entity) {
        if (entity == null || (Collection.class.isAssignableFrom(entity.getClass()) && ((Collection) entity).isEmpty())) {
            return noContent();
        }

        return ok(entity);
    }

    /**
     * When a request succeeded but yielded epmty content
     *
     * @return A Response object
     */
    public static Response noContent() {
        return Response.noContent().build();
    }

    /**
     * When no entities were modified or no action was taken
     *
     * @return A Response object
     */
    public static Response notModified() {
        return Response.notModified().build();
    }

    /**
     * When the request contains invalid data, like missing notnull values or nonexistent ids
     *
     * @return A Response object
     */
    public static Response badRequest() {
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    /**
     * When a request to save an entity yields a constraint violation due to unique values
     *
     * @return A Response object
     */
    public static Response alreadyExists() {
        return Response.status(Response.Status.CONFLICT).build();
    }

    public static Response serverError() {
        return serverError(null);
    }

    public static Response serverError(Object entity) {
        if (entity == null) {
            return Response.serverError().build();
        } else {
            return Response.status(500).entity(entity).build();
        }
    }
}
