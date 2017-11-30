package nl.juraji.biliomi.rest.config.directives;

import com.google.common.base.CaseFormat;
import nl.juraji.biliomi.model.internal.rest.query.RestSortDirective;
import nl.juraji.biliomi.utility.factories.marshalling.JacksonMarshaller;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.TransformingComparator;
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
    try {
      Collection<RestSortDirective> sortDirectives = null;

      if (StringUtils.isNotEmpty(queryParamValue)) {
        sortDirectives = JacksonMarshaller.unmarshalCollection(queryParamValue, RestSortDirective.class);
      }

      if (sortDirectives != null && sortDirectives.size() > 0) {
        final ComparatorChain comparatorChain = new ComparatorChain();

        sortDirectives.forEach(sortDirective -> {
          // The model is in title case, but the pojo properties are in plain camel case
          String sortBy = CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_CAMEL).convert(sortDirective.getProperty());

          if (sortDirective.isCaseInsensitive()) {
            Transformer transformer = o -> (o == null ? "" : String.valueOf(o).toLowerCase());
            TransformingComparator comparator = new TransformingComparator(transformer);
            //noinspection unchecked TransformingComparator implements comparator
            comparatorChain.addComparator(new BeanComparator(sortBy, comparator), sortDirective.isDescending());
          } else {
            comparatorChain.addComparator(new BeanComparator(sortBy), sortDirective.isDescending());
          }
        });

        //noinspection unchecked ComparatorChain implements Comparator
        entities.sort(comparatorChain);
      }
    } catch (Exception e) {
      LogManager.getLogger(this.getClass()).error(e);
      throw e;
    }

    return entities;
  }
}
