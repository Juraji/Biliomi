package nl.juraji.biliomi.components.chat.raffle;

import nl.juraji.biliomi.BiliomiContainer;
import nl.juraji.biliomi.model.core.User;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;

/**
 * Created by Juraji on 28-5-2017.
 * Biliomi v3
 */
public class RaffleLogFile {
    private static final File WORKING_DIR = BiliomiContainer.getParameters().getWorkingDir("Raffles");
    private static final String FILE_NAME_DT_PATTERN = "y-M-d_H-m";
    private static final String IN_FILE_DT_PATTERN = "E d MMMM y HH:mm z";

    private final File logFile;

    /**
     * Generate a log file for a raffle instance
     *
     * @param keyword       The raffle keyword
     * @param joinCost      The points cost for joining, formatted by the PointsService
     * @param followersOnly Raffle is followers only or not
     */
    public RaffleLogFile(String keyword, long joinCost, boolean followersOnly) {
        DateTime now = DateTime.now();
        String fDate = now.toString(FILE_NAME_DT_PATTERN);
        logFile = new File(WORKING_DIR, "Raffle_" + fDate + ".txt");

        write("New raffle on " + now.toString(IN_FILE_DT_PATTERN));
        write("Keyword: " + keyword);
        write("Join cost: " + joinCost + " Points");
        write("Followers only: " + (followersOnly ? "yes" : "no"));
        write("");
    }

    /**
     * Log when a user joined the raffle
     *
     * @param user The User that joined
     */
    public void userJoin(User user) {
        write("User joined " + user.getDisplayName());
    }

    /**
     * Log the end of the raffle
     */
    public void raffleEnd() {
        write("");
        write("Raffle ended on " + DateTime.now().toString(IN_FILE_DT_PATTERN));
    }

    /**
     * Log when a user gets (re)picked
     *
     * @param pickedUser The picked User
     */
    public void userPicked(User pickedUser) {
        write("");
        write("Picked user on " + DateTime.now().toString(IN_FILE_DT_PATTERN));
        write("Picked user: " + pickedUser.getDisplayName());
    }

    private void write(String data) {
        try {
            FileUtils.writeStringToFile(logFile, data + Character.LINE_SEPARATOR, "UTF-8", true);
        } catch (IOException e) {
            LogManager.getLogger(getClass()).error("Failed writing to raffle log", e);
        }
    }
}
