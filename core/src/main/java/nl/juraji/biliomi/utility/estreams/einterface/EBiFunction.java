package nl.juraji.biliomi.utility.estreams.einterface;

/**
 * Created by Juraji on 5-5-2017.
 * Biliomi v3
 */
@FunctionalInterface
public interface EBiFunction<T, U, R, E extends Exception> {
    R apply(T t, U u) throws E;
}
