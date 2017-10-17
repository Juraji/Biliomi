package nl.juraji.biliomi.utility.estreams.types;

import nl.juraji.biliomi.utility.estreams.einterface.*;

import java.util.Comparator;
import java.util.function.*;

/**
 * Created by robin.
 * augustus 2016
 */
@SuppressWarnings({"Duplicates", "ConstantConditions", "unchecked"})
public final class RethrowEInterface {

  private RethrowEInterface() {
    // Empty private constructor
  }

  public static <T, E extends Exception> Consumer<T> consumer(EConsumer<T, E> consumer) {
    return t -> {
      try {
        consumer.accept(t);
      } catch (Exception exception) {
        throwAsUnchecked(exception);
      }
    };
  }

  public static <T, U, E extends Exception> BiConsumer<T, U> biConsumber(EBiConsumer<T, U, E> biConsumer) {
    return (t, u) -> {
      try {
        biConsumer.accept(t, u);
      } catch (Exception exception) {
        throwAsUnchecked(exception);
      }
    };
  }

  /**
   * .map(rethrow(value -> Class.forName(value))) or .map(rethrow(Class::forName))
   */
  public static <T, R, E extends Exception> Function<T, R> function(EFunction<T, R, E> function) {
    return t -> {
      try {
        return function.apply(t);
      } catch (Exception exception) {
        throwAsUnchecked(exception);
        return null;
      }
    };
  }

  /**
   * .map(rethrow(value -> Class.forName(value))) or .map(rethrow(Class::forName))
   */
  public static <T, U, R, E extends Exception> BiFunction<T, U, R> biFunction(EBiFunction<T, U, R, E> function) {
    return (t, u) -> {
      try {
        return function.apply(t, u);
      } catch (Exception exception) {
        throwAsUnchecked(exception);
        return null;
      }
    };
  }

  /**
   * .filter(rethrow(value -> Failing evaluation))
   */
  public static <T, E extends Exception> Predicate<T> predicate(EPredicate<T, E> predicate) {
    return t -> {
      try {
        return predicate.test(t);
      } catch (Exception exception) {
        throwAsUnchecked(exception);
        return false;
      }
    };
  }

  /**
   * .filter(rethrow(value -> Failing evaluation, value -> Failing evaluation))
   */
  public static <T, U, E extends Exception> BiPredicate<T, U> biPredicate(EBiPredicate<T, U, E> biPredicate) {
    return (t, u) -> {
      try {
        return biPredicate.test(t, u);
      } catch (Exception exception) {
        throwAsUnchecked(exception);
        return false;
      }
    };
  }

  /**
   * .sort(rethrow(value -> Failing comperator))
   */
  public static <T, E extends Exception> Comparator<T> comparator(EComparator<T, E> comperator) {
    return (t1, t2) -> {
      try {
        return comperator.compare(t1, t2);
      } catch (Exception exception) {
        throwAsUnchecked(exception);
        return 0;
      }
    };
  }

  /**
   * rethrow(() -> new StringJoiner(new String(new byte[]{77, 97, 114, 107}, "UTF-8"))),
   */
  public static <T, E extends Exception> Supplier<T> supplier(ESupplier<T, E> function) {
    return () -> {
      try {
        return function.get();
      } catch (Exception exception) {
        throwAsUnchecked(exception);
        return null;
      }
    };
  }

  /**
   * rethrow(value1, value2 -> failing binary operator)
   */
  public static <T, E extends Exception> BinaryOperator<T> binaryOperator(EBinaryOperator<T, E> binaryOperator) {
    return (t1, t2) -> {
      try {
        return binaryOperator.apply(t1, t2);
      } catch (Exception exception) {
        throwAsUnchecked(exception);
        return null;
      }
    };
  }

  public static <E extends Throwable> void throwAsUnchecked(Exception exception) throws E {
    if (exception != null) {
      throw (E) exception;
    }
  }
}