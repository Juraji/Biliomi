package nl.juraji.biliomi.rest.config.directives;

import nl.juraji.biliomi.model.internal.rest.query.RestFilterDirective;
import nl.juraji.biliomi.utility.factories.marshalling.JacksonMarshaller;
import nl.juraji.biliomi.utility.types.xml.XmlElementPathBeanPredicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Juraji on 22-12-2017.
 * Biliomi
 */
public class FilterDirectiveQueryProcessor<T> implements QueryProcessor<List<T>> {
  public static final String PARAM_NAME = "filter";

  @Override
  public List<T> process(String queryParamValue, List<T> entities) throws Exception {
    if (entities.size() != 0) {
      try {
        Collection<RestFilterDirective> filterDirectives = null;
        //noinspection unchecked This error is inevitable depending on user input
        Class<T> rootClass = (Class<T>) entities.get(0).getClass();

        if (StringUtils.isNotEmpty(queryParamValue)) {
          filterDirectives = JacksonMarshaller.unmarshalCollection(queryParamValue, RestFilterDirective.class);
        }

        if (filterDirectives != null && filterDirectives.size() > 0) {
          AtomicReference<Predicate<T>> predicateChain = new AtomicReference<>();

          filterDirectives.forEach(filterDirective -> {
            Predicate<T> predicate = XmlElementPathBeanPredicate.forFilterDirective(filterDirective, rootClass);

            if (predicateChain.get() == null) {
              predicateChain.set(predicate);
            } else {
              if (filterDirective.isOrPrevious()) {
                predicateChain.updateAndGet(p -> p.or(predicate));
              } else {
                predicateChain.updateAndGet(p -> p.and(predicate));
              }
            }
          });

          if (predicateChain.get() != null) {
            entities = entities.stream()
                .filter(predicateChain.get())
                .collect(Collectors.toList());
          }
        }
      } catch (Exception e) {
        LogManager.getLogger(this.getClass()).error(e);
        throw e;
      }
    }

    return entities;
  }
}
