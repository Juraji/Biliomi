package nl.juraji.biliomi.io.api.twitch.helix.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import nl.juraji.biliomi.utility.calculate.EnumUtils;

/**
 * Created by Juraji on 3-1-2018.
 * Biliomi
 */
public enum TwitchStreamType {
    LIVE, VODCAST, OFFLINE;

    @JsonCreator
    public static TwitchStreamType fromJson(String streamType) {
        return EnumUtils.toEnum(streamType, TwitchStreamType.class, OFFLINE);
    }
}
