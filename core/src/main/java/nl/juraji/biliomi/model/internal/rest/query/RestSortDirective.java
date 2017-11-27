package nl.juraji.biliomi.model.internal.rest.query;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.juraji.biliomi.utility.factories.marshalling.JacksonMarshaller;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Created by Juraji on 27-11-2017.
 * Biliomi
 */
@XmlRootElement(name = "RestSortDirective")
public class RestSortDirective implements ParamConverterProvider{

  @XmlElement(name = "Property")
  private String property;

  @XmlElement(name = "Descending")
  private boolean descending;

  public String getProperty() {
    return property;
  }

  public void setProperty(String property) {
    this.property = property;
  }

  public boolean isDescending() {
    return descending;
  }

  public void setDescending(boolean descending) {
    this.descending = descending;
  }

  @Override
  public <T> ParamConverter<T> getConverter(Class<T> aClass, Type type, Annotation[] annotations) {
    return new ParamConverter<T>() {
      @Override
      public T fromString(String s) {
        try {
          return JacksonMarshaller.unmarshal(s, aClass);
        } catch (IOException e) {
          return null;
        }
      }

      @Override
      public String toString(T t) {
        try {
          return JacksonMarshaller.marshal(t);
        } catch (JsonProcessingException e) {
          return "{}";
        }
      }
    };
  }
}
