package nl.juraji.biliomi.rest.config.directives;

/**
 * Created by Juraji on 27-11-2017.
 * Biliomi
 */
public interface QueryProcessor<T> {
  T process(String queryParamValue, T object) throws Exception;
}
