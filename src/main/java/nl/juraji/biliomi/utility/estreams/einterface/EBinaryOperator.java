package nl.juraji.biliomi.utility.estreams.einterface;

/**
 * Created by Juraji on 5-5-2017.
 * Biliomi v3
 */
@FunctionalInterface
public interface EBinaryOperator<T, E extends Exception> {
  T apply(T t1, T t2) throws E;
}