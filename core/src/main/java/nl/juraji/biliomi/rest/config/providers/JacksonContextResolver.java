package nl.juraji.biliomi.rest.config.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.juraji.biliomi.utility.factories.marshalling.JacksonMarshaller;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * Created by Juraji on 14-6-2017.
 * Biliomi v3
 */
@Provider
@Priority(Priorities.ENTITY_CODER)
public class JacksonContextResolver implements ContextResolver<ObjectMapper> {

    private final ObjectMapper mapper = JacksonMarshaller.getObjectMapper();

    @Override
    public ObjectMapper getContext(Class<?> aClass) {
        return mapper;
    }
}
