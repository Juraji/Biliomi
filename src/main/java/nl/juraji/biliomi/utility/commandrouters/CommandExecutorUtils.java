package nl.juraji.biliomi.utility.commandrouters;

import nl.juraji.biliomi.model.internal.events.bot.ConsoleInputEvent;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.components.interfaces.Component;
import nl.juraji.biliomi.utility.commandrouters.cmd.CliCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.estreams.EBiStream;
import nl.juraji.biliomi.utility.estreams.EStream;
import nl.juraji.biliomi.utility.estreams.types.EStreamAssertionFailedException;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by Juraji on 1-5-2017.
 * Biliomi v3
 */
public final class CommandExecutorUtils {

  private CommandExecutorUtils() {
  }

  /**
   * Get a BiEStream of CommandRoute annotations and the annotated metods for a class
   * componentClass can be any class for compatibility with interceptors
   *
   * @param componentClass The class to scan
   * @return A BiEStream
   */
  public static EBiStream<CommandRoute, Method, Exception> findCommandExecutors(Class<?> componentClass) throws EStreamAssertionFailedException {
    return EStream.from(componentClass.getDeclaredMethods())
        .filter(method -> method.isAnnotationPresent(CommandRoute.class))
        .assertion(CommandExecutorUtils::assertMethodMeetsRequirements, CommandExecutorUtils::getRequirementsNotMetMsg)
        .mapToBiEStream(method -> method.getAnnotation(CommandRoute.class), method -> method);
  }

  public static EBiStream<SubCommandRoute, Method, Exception> findSubCommandExecutors(Class<? extends Component> componentClass) throws EStreamAssertionFailedException {
    return EStream.from(componentClass.getDeclaredMethods())
        .filter(method -> method.isAnnotationPresent(SubCommandRoute.class))
        .assertion(CommandExecutorUtils::assertMethodMeetsRequirements, CommandExecutorUtils::getRequirementsNotMetMsg)
        .mapToBiEStream(method -> method.getAnnotation(SubCommandRoute.class), method -> method);
  }

  public static EBiStream<CliCommandRoute, Method, Exception> findCliCommandExecutors(Class<? extends Component> componentClass) throws EStreamAssertionFailedException {
    return EStream.from(componentClass.getDeclaredMethods())
        .filter(method -> method.isAnnotationPresent(CliCommandRoute.class))
        .assertion(CommandExecutorUtils::assertCliMethodMeetsRequirements, CommandExecutorUtils::getRequirementsNotMetMsg)
        .mapToBiEStream(method -> method.getAnnotation(CliCommandRoute.class), method -> method);
  }

  private static boolean assertMethodMeetsRequirements(Method method) {
    // Method needs to be public in order to make the CommandRouters able to call it
    if (!Modifier.isPublic(method.getModifiers())) {
      return true;
    }

    // The return type must be a boolean
    if (!method.getReturnType().equals(boolean.class)) {
      return true;
    }

    // The method parameters must exactly match (User, Arguments)
    Class<?>[] parameterTypes = method.getParameterTypes();
    return (parameterTypes.length == 2
        && parameterTypes[0].equals(User.class)
        && parameterTypes[1].equals(Arguments.class));
  }

  private static boolean assertCliMethodMeetsRequirements(Method method) {
    // Method needs to be public in order to make the CommandRouters able to call it
    if (!Modifier.isPublic(method.getModifiers())) {
      return true;
    }

    // The return type must be a boolean
    if (!method.getReturnType().equals(boolean.class)) {
      return true;
    }

    // The method parameters must exactly match (Arguments, ConsoleInputEvent)
    Class<?>[] parameterTypes = method.getParameterTypes();
    return (parameterTypes.length == 1
        && parameterTypes[0].equals(ConsoleInputEvent.class));
  }

  private static String getRequirementsNotMetMsg(Method method) {
    return method.getDeclaringClass().getSimpleName() + "." + method.getName() + "() does not have the required interface";
  }
}
