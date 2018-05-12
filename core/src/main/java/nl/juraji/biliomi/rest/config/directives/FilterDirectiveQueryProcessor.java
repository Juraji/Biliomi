package nl.juraji.biliomi.rest.config.directives;

import nl.juraji.biliomi.model.internal.rest.query.RestFilterDirective;
import nl.juraji.biliomi.utility.factories.marshalling.JacksonMarshaller;
import nl.juraji.biliomi.utility.types.xml.XmlElementPathBeanPredicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Juraji on 22-12-2017.
 * Biliomi
 */
public class FilterDirectiveQueryProcessor<T> implements QueryProcessor<List<T>> {
    public static final String PARAM_NAME_FILTER = "filter";

    public static <T> Predicate<T> buildPredicateChain(Collection<RestFilterDirective> filterDirectives, Class<T> rootClass) {
        AtomicReference<Predicate<T>> predicateChainRef = new AtomicReference<>();

        if (filterDirectives != null && filterDirectives.size() > 0) {

            filterDirectives.forEach(filterDirective -> {
                Predicate<T> predicate = XmlElementPathBeanPredicate.forFilterDirective(filterDirective, rootClass);

                if (predicateChainRef.get() == null) {
                    predicateChainRef.set(predicate);
                } else {
                    if (filterDirective.isOrPrevious()) {
                        predicateChainRef.updateAndGet(p -> p.or(predicate));
                    } else {
                        predicateChainRef.updateAndGet(p -> p.and(predicate));
                    }
                }
            });

        }
        return predicateChainRef.get();
    }

    /**
     * Use the "filter" query parameter to filter a list of entities.
     * - Chains filter directives in order of appearance
     * - Supports filtering on sub-properties by providing the path as "Property.SubProperty".
     * - Supports basic and/or per directive against previous directives
     *
     * @param entities    The list of entities to filter
     * @param queryParams A map with the value of the "filter" query parameter
     * @return The filtered list of entities
     * @throws IOException When Jackson is unnable to unmarshal the sorting directives
     */
    @Override
    public List<T> process(List<T> entities, Map<String, Object> queryParams) throws Exception {
        if (entities.size() != 0 && queryParams.containsKey(PARAM_NAME_FILTER)) {
            try {
                String filter = (String) queryParams.get(PARAM_NAME_FILTER);
                Collection<RestFilterDirective> filterDirectives = null;
                // noinspection unchecked This error is inevitable depending on user input
                Class<T> rootClass = (Class<T>) entities.get(0).getClass();

                if (StringUtils.isNotEmpty(filter)) {
                    if (filter.startsWith("[") && filter.endsWith("]")) {
                        // This looks like a JSON-array, use Jackson to parse it
                        filterDirectives = JacksonMarshaller.unmarshalCollection(filter, RestFilterDirective.class);
                    } else {
                        // Maybe it's a query, parse it to find out
                        filterDirectives = RestFilterQueryParser.parseQuery(filter);
                    }
                }

                Predicate<T> predicateChain = buildPredicateChain(filterDirectives, rootClass);
                if (predicateChain != null) {
                    entities = entities.stream()
                            .filter(predicateChain)
                            .collect(Collectors.toList());
                }

            } catch (Exception e) {
                LogManager.getLogger(this.getClass()).error(e);
                throw e;
            }
        }

        return entities;
    }
}
