package nl.juraji.biliomi.utility.commandrouters.types;

import nl.juraji.biliomi.components.interfaces.Component;

import java.lang.reflect.Method;

/**
 * Created by robin on 1-6-17.
 * biliomi
 */
public final class RegistryEntry {
  private final Component componentInstance;
  private final Method method;

  public RegistryEntry(Component componentInstance, Method method) {
    this.componentInstance = componentInstance;
    this.method = method;
  }

  public Component getComponentInstance() {
    return componentInstance;
  }

  public Method getMethod() {
    return method;
  }
}
