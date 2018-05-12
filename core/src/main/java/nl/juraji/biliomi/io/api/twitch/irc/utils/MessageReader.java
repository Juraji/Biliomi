package nl.juraji.biliomi.io.api.twitch.irc.utils;

import nl.juraji.biliomi.utility.calculate.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Juraji on 6-9-2017.
 * Biliomi v3
 */
public final class MessageReader {
    private static final Pattern COMMAND_PATTERN = Pattern.compile("([a-z]+.)?tmi\\.twitch\\.tv[ ]([A-Z0-9]+)");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("([a-z0-9_]+)\\.tmi\\.twitch\\.tv");
    private static final Pattern TAGS_PATTERN = Pattern.compile("^(@[^ ]+)\\s:");
    private static final Pattern MESSAGE_PATTERN = Pattern.compile("[A-Z]+\\s#?[a-z]+\\s:(.*)");

    private final String message;

    public MessageReader(String message) {
        if (StringUtils.isEmpty(message)) {
            throw new IllegalArgumentException("Message can not be empty");
        }

        this.message = message;
    }

    public IrcCommand getIrcCommand() {
        Matcher matcher = COMMAND_PATTERN.matcher(message);

        if (matcher.find()) {
            String group = matcher.group(2);
            if (Character.isDigit(group.charAt(0))) {
                group = 'C' + group;
            }
            return EnumUtils.toEnum(group, IrcCommand.class);
        }

        return null;
    }

    public String getUsername() {
        Matcher matcher = USERNAME_PATTERN.matcher(message);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    public Tags getTags() {
        Matcher matcher = TAGS_PATTERN.matcher(message);

        if (matcher.find()) {
            return new Tags(matcher.group(1));
        }

        return null;
    }

    public String getMessage() {
        Matcher matcher = MESSAGE_PATTERN.matcher(message);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }
}
