package nl.juraji.biliomi.rest.config.directives;

import java.util.List;
import java.util.Map;

/**
 * Created by Juraji on 23-12-2017.
 * Biliomi
 */
public class LimitQueryProcessor<T> implements QueryProcessor<List<T>> {
    public static final String PARAM_NAME_LIMIT = "limit";
    public static final String PARAM_NAME_OFFSET = "offset";

    @Override
    public List<T> process(List<T> entities, Map<String, Object> queryParams) {
        if (entities.size() > 0 && queryParams.containsKey(PARAM_NAME_LIMIT)) {
            Integer limit = (Integer) queryParams.get(PARAM_NAME_LIMIT);
            Integer offset = (Integer) queryParams.getOrDefault(PARAM_NAME_OFFSET, 0);

            offset = Math.max(0, offset);
            limit = Math.min(offset + limit, entities.size());
            entities = entities.subList(offset, limit);
        }

        return entities;
    }
}
