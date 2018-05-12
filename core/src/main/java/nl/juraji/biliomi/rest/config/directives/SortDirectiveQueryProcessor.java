package nl.juraji.biliomi.rest.config.directives;

import nl.juraji.biliomi.model.internal.rest.query.RestSortDirective;
import nl.juraji.biliomi.utility.factories.marshalling.JacksonMarshaller;
import nl.juraji.biliomi.utility.types.xml.XmlElementPathBeanComparator;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Juraji on 27-11-2017.
 * Biliomi
 */
public class SortDirectiveQueryProcessor<T> implements QueryProcessor<List<T>> {
    public static final String PARAM_NAME_SORT = "sort";

    public static <T> Comparator<T> buildComparatorChain(Collection<RestSortDirective> sortDirectives, Class<T> rootClass) {
        AtomicReference<Comparator<T>> comparatorChainRef = new AtomicReference<>();

        if (sortDirectives != null && sortDirectives.size() > 0) {

            sortDirectives.forEach(sortDirective -> {
                Comparator<T> comparator = XmlElementPathBeanComparator.forSortDirective(sortDirective, rootClass);

                if (comparatorChainRef.get() == null) {
                    comparatorChainRef.set(comparator);
                } else {
                    comparatorChainRef.updateAndGet(p -> p.thenComparing(comparator));
                }
            });

        }
        return comparatorChainRef.get();
    }

    /**
     * Use the "sort" query parameter to sort a list of entities.
     * - Chains sorting directives in order of appearance
     * - Supports sorting on sub-properties by providing the path as "Property.SubProperty".
     *
     * @param entities    The list of entities to sort
     * @param queryParams A map with the value of the "sort" query parameter
     * @return The sorted list of entities
     * @throws IOException When Jackson is unnable to unmarshal the sorting directives
     */

    @Override
    public List<T> process(List<T> entities, Map<String, Object> queryParams) throws Exception {
        if (entities.size() > 1 && queryParams.containsKey(PARAM_NAME_SORT)) {
            try {
                String sorters = (String) queryParams.get(PARAM_NAME_SORT);
                Collection<RestSortDirective> sortDirectives = null;
                // noinspection unchecked This error is inevitable depending on user input
                Class<T> rootClass = (Class<T>) entities.get(0).getClass();

                if (StringUtils.isNotEmpty(sorters)) {
                    sortDirectives = JacksonMarshaller.unmarshalCollection(sorters, RestSortDirective.class);
                }

                if (sortDirectives != null && sortDirectives.size() > 0) {
                    Comparator<T> comparatorChain = buildComparatorChain(sortDirectives, rootClass);

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
