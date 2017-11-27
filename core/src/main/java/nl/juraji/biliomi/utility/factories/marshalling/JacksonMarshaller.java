package nl.juraji.biliomi.utility.factories.marshalling;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by Juraji on 15-6-2017.
 * Biliomi v3
 */
public final class JacksonMarshaller {

  private static final ObjectMapper mapper = new ObjectMapper();

  static {
    // Add the JodaModule to the object mapper
    // And set the serializer to use toString, which results in ISO8601 dates
    mapper.registerModule(new JodaModule());
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);

    // Add the JaxbAnnotationModule to the object mapper
    // To be have Jackson introspect the Jaxb annotations like @XmlElement
    mapper.registerModule(new JaxbAnnotationModule());
  }

  public static String marshal(Object object) throws JsonProcessingException {
    return getObjectMapper().writeValueAsString(object);
  }

  public static <T> Collection<T> unmarshalCollection(String json, Class<T> elementClass) throws IOException {
    return getObjectMapper().readValue(json, mapper.getTypeFactory().constructCollectionType(Collection.class, elementClass));
  }

  public static <T> T unmarshal(String json, Class<T> type) throws IOException {
    return getObjectMapper().readValue(json, type);
  }

  public static <T> T convertJsonNode(JsonNode node, Class<T> type) {
    return getObjectMapper().convertValue(node, type);
  }

  public static ObjectMapper getObjectMapper() {
    return mapper;
  }
}
