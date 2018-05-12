package nl.juraji.biliomi.utility.types;

/**
 * Created by Juraji on 25-4-2017.
 * Biliomi v3
 */
public interface Restartable {

    /**
     * Design usage: Starts a service or process
     */
    void start();

    /**
     * Design usage: Stops a service or process
     */
    void stop();

    /**
     * Design usage: Kill a service, making it unable to start in the future
     * By default this method will simply call stop()
     */
    default void kill() {
        stop();
    }

    /**
     * Uses stop then start in order to restart a service
     */
    default void restart() {
        this.stop();
        this.start();
    }
}
