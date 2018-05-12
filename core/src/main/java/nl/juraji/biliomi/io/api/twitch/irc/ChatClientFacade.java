package nl.juraji.biliomi.io.api.twitch.irc;

/**
 * Created by robin.
 * april 2017
 * <p>
 * Create a facade, hiding "forbidden" methods
 */
public interface ChatClientFacade {

    /**
     * Design usage: Post a message in the connected chat
     *
     * @param message The message to post
     */
    void say(String message);

    /**
     * Design usage: Post a message as whisper to a user
     *
     * @param username The target username
     * @param message  The message to post
     */
    void whisper(String username, String message);
}
