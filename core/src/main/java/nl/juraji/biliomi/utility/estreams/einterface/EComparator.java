package nl.juraji.biliomi.utility.estreams.einterface;

/**
 * Created by Juraji on 5-5-2017.
 * Biliomi v3
 */
@FunctionalInterface
public interface EComparator<T, E extends Exception> {
  int compare(T t1, T t2) throws E;
}
