package nl.juraji.biliomi.utility.estreams.einterface;

/**
 * Created by Juraji on 5-5-2017.
 * Biliomi v3
 */
@FunctionalInterface
public interface EBiConsumer<T, U, E extends Exception> {
    void accept(T t, U u) throws E;
}
