package nl.juraji.biliomi.rest.config.directives;

import nl.juraji.biliomi.utility.calculate.NumberConverter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by Juraji on 23-12-2017.
 * Biliomi
 */
public class LimitQueryProcessor<T> implements QueryProcessor<List<T>> {
  public static final String PARAM_NAME = "limit";

  @Override
  public List<T> process(String queryParamValue, List<T> entities) throws Exception {
    if (StringUtils.isNotEmpty(queryParamValue) && entities.size() > 0) {
      Integer limit = NumberConverter.asNumber(queryParamValue).toInteger();

      if (limit != null) {
        entities = entities.subList(0, limit - 1);
      }
    }

    return entities;
  }
}
