package nl.juraji.biliomi.utility.estreams.einterface;

/**
 * Created by Juraji on 5-5-2017.
 * Biliomi v3
 */
@FunctionalInterface
public interface EPredicate<T, E extends Exception> {
    boolean test(T t) throws E;
}
