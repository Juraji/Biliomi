package nl.juraji.biliomi.utility.commandrouters.routers;

import nl.juraji.biliomi.utility.calculate.WeldUtils;
import nl.juraji.biliomi.utility.commandrouters.annotations.CliCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.RegistryEntry;
import nl.juraji.biliomi.utility.estreams.EStream;
import nl.juraji.biliomi.utility.types.collections.CIMap;
import nl.juraji.biliomi.utility.types.collections.MultivaluedHashMap;
import nl.juraji.biliomi.utility.types.components.Component;

import javax.enterprise.inject.Default;
import javax.inject.Singleton;
import java.lang.reflect.Method;

/**
 * Created by robin on 1-6-17.
 * biliomi
 */
@Default
@Singleton
public class CliCommandRouterRegistry {
  private final CIMap<RegistryEntry> registry = new CIMap<>();

  public boolean containsKey(String key) {
    return registry.containsKey(key);
  }

  public RegistryEntry get(String key) {
    return registry.getOrDefault(key, null);
  }

  public RegistryEntry put(String key, Component component, Method method) {
    return registry.put(key, new RegistryEntry(component, method));
  }

  public RegistryEntry remove(String key) {
    return registry.remove(key);
  }

  public MultivaluedHashMap<String, CliCommandRoute> getCommandDescriptionsMap() {
    MultivaluedHashMap<String, CliCommandRoute> map = new MultivaluedHashMap<>();

    EStream.from(registry.values())
        .map(RegistryEntry::getMethod)
        .filter(method -> method.isAnnotationPresent(CliCommandRoute.class))
        .mapToBiEStream(Method::getDeclaringClass, m -> m.getAnnotation(CliCommandRoute.class))
        .mapKey(WeldUtils::getAbsoluteClassForClass)
        .mapKey(Class::getSimpleName)
        .forEach(map::putSingle);

    return map;
  }
}
