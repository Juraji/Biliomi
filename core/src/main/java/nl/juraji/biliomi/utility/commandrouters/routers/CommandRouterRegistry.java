package nl.juraji.biliomi.utility.commandrouters.routers;

import nl.juraji.biliomi.utility.commandrouters.types.RegistryEntry;
import nl.juraji.biliomi.utility.types.collections.CIMap;
import nl.juraji.biliomi.utility.types.components.Component;

import javax.enterprise.inject.Default;
import javax.inject.Singleton;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

/**
 * Created by Juraji on 2-5-2017.
 * Biliomi v3
 */
@Default
@Singleton
public class CommandRouterRegistry {
    private final CIMap<RegistryEntry> registry = new CIMap<>();
    private final CIMap<String> aliassesRegistry = new CIMap<>();

    public boolean containsKey(String key) {
        return registry.containsKey(key) || aliassesRegistry.containsKey(key);
    }

    public RegistryEntry get(String key) {
        // Translate alias, else use key itself
        String tKey = translateAlias(key);
        return registry.getOrDefault(tKey, null);
    }

    public void put(String key, Component component, Method method) {
        registry.put(key, new RegistryEntry(component, method));
    }

    public void remove(String key) {
        registry.remove(key);
    }

    public String putAlias(String alias, String command) {
        return aliassesRegistry.put(alias, command);
    }

    public String removeAlias(String alias) {
        return aliassesRegistry.remove(alias);
    }

    public void clearAliassesFor(String command) {
        Collections.unmodifiableSet(aliassesRegistry.entrySet())
                .stream()
                .filter(e -> e.getValue().equalsIgnoreCase(command))
                .map(Map.Entry::getKey)
                .forEach(aliassesRegistry::remove);
    }

    public String translateAlias(String key) {
        return aliassesRegistry.getOrDefault(key, key);
    }

}
