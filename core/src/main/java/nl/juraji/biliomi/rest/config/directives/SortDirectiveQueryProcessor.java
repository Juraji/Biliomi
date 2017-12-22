package nl.juraji.biliomi.rest.config.directives;

import nl.juraji.biliomi.model.internal.rest.query.RestSortDirective;
import nl.juraji.biliomi.utility.factories.marshalling.JacksonMarshaller;
import nl.juraji.biliomi.utility.types.xml.XmlElementPathBeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Created by Juraji on 27-11-2017.
 * Biliomi
 */
public class SortDirectiveQueryProcessor<T> implements QueryProcessor<List<T>> {
  public static final String PARAM_NAME = "sort";

  /**
   * Use the "sort" query parameter to sort a list of entities.
   * - Chains sorting directives in order of appearance
   * - Supports sorting on sub-properties by providing the path as "Property.SubProperty".
   *
   * @param queryParamValue The value of the "sort" query parameter
   * @param entities        The list of entities to sort
   * @return The sorted list of entities
   * @throws IOException When Jackson is unnable to unmarshal the sorting directives
   */
  @Override
  public List<T> process(String queryParamValue, List<T> entities) throws IOException {
    if (entities.size() > 1) {
      try {
        Collection<RestSortDirective> sortDirectives = null;
        Class<?> rootClass = entities.get(0).getClass();

        if (StringUtils.isNotEmpty(queryParamValue)) {
          sortDirectives = JacksonMarshaller.unmarshalCollection(queryParamValue, RestSortDirective.class);
        }

        if (sortDirectives != null && sortDirectives.size() > 0) {
          final ComparatorChain comparatorChain = new ComparatorChain();

          sortDirectives.forEach(sortDirective ->
              comparatorChain.addComparator(new XmlElementPathBeanComparator<>(sortDirective.getProperty(), rootClass), sortDirective.isDescending()));

          //noinspection unchecked ComparatorChain implements Comparator
          entities.sort(comparatorChain);
        }
      } catch (Exception e) {
        LogManager.getLogger(this.getClass()).error(e);
        throw e;
      }
    }

    return entities;
  }
}
