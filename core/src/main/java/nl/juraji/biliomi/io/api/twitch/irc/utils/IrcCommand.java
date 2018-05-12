package nl.juraji.biliomi.io.api.twitch.irc.utils;

public enum IrcCommand {
    // No command
    NONE,

    // Login flow
    C001, C002, C003, C004, C353, C366, C372, C375, C376,

    // Ping pong
    PING,
    PONG,

    // Client capability
    CAP,

    // Generic capabilities
    JOIN, // Join a channel.
    PART, // Depart from a channel.
    PRIVMSG, //	Send a message to a channel.
    WHISPER, //	Received a whisper.

    // Membership capabilities
    MODE,  // Gain/lose moderator (operator) status in a channel.
    NAMES,  // List current chatters in a channel.

    // Commands capabilities
    CLEARCHAT,  // Temporary or permanent ban on a channel.
    HOSTTARGET,  // Host starts or stops a message.
    NOTICE,  // General notices from the server.
    RECONNECT,  // Rejoin channels after a restart.
    ROOMSTATE,  // When a user joins a channel or a room setting is changed.
    USERNOTICE,  // On resubscription to a channel.
    USERSTATE,  // When a user joins a channel or sends a PRIVMSG to a channel.

    // Tags capabilities
    GLOBALUSERSTATE,  // On successful login.
}
