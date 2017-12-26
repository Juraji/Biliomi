package nl.juraji.biliomi.rest.config.directives;

import java.util.Map;

/**
 * Created by Juraji on 27-11-2017.
 * Biliomi
 */
public interface QueryProcessor<T> {
  T process(T object, Map<String, Object> queryParams) throws Exception;
}
