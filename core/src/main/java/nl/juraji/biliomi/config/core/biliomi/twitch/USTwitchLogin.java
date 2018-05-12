package nl.juraji.biliomi.config.core.biliomi.twitch;

/**
 * Created by Juraji on 9-10-2017.
 * Biliomi
 */
public class USTwitchLogin {
    private String botUsername;
    private String channelUsername;

    public String getBotUsername() {
        return botUsername;
    }

    public void setBotUsername(String botUsername) {
        this.botUsername = botUsername;
    }

    public String getChannelUsername() {
        return channelUsername;
    }

    public void setChannelUsername(String channelUsername) {
        this.channelUsername = channelUsername;
    }
}
