package nl.juraji.biliomi.utility.factories.reflections;

import nl.juraji.biliomi.utility.estreams.EStream;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class ReflectionUtils {
  private final String[] packages;

  private ReflectionUtils(String[] packages) {
    this.packages = packages;
  }

  public static ReflectionUtils forClassPackage(Class<?> clazz) {
    return forPackages(clazz.getPackage().getName());
  }

  public static ReflectionUtils forPackages(String... packages) {
    return new ReflectionUtils(packages);
  }

  public <T> EStream<Class<? extends T>, Exception> subTypes(Class<T> type) {
    return EStream
        .from(new Reflections(new ConfigurationBuilder()
            .forPackages(packages))
            .getSubTypesOf(type))
        .filter(c -> !Modifier.isAbstract(c.getModifiers()));
  }

  public EStream<Method, Exception> annotatedMethods(Class<? extends Annotation> annotation) {
    return EStream.from(new Reflections(new ConfigurationBuilder()
        .forPackages(packages)
        .addScanners(new MethodAnnotationsScanner()))
        .getMethodsAnnotatedWith(annotation));
  }

  public EStream<Class<?>, Exception> annotatedTypes(Class<? extends Annotation> annotation) {
    return EStream.from(new Reflections(new ConfigurationBuilder()
        .forPackages(packages)
        .addScanners(new TypeAnnotationsScanner()))
        .getTypesAnnotatedWith(annotation));
  }
}
