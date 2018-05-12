package nl.juraji.biliomi.components.integrations.spotify;

import nl.juraji.biliomi.components.integrations.spotify.api.v1.SpotifyApi;
import nl.juraji.biliomi.components.integrations.spotify.api.v1.model.SpotifySnapshot;
import nl.juraji.biliomi.components.integrations.spotify.api.v1.model.player.SpotifyCurrentTrack;
import nl.juraji.biliomi.components.integrations.spotify.api.v1.model.playlist.SpotifyPlaylist;
import nl.juraji.biliomi.components.integrations.spotify.api.v1.model.tracks.SpotifyTrack;
import nl.juraji.biliomi.components.integrations.spotify.api.v1.model.tracks.SpotifyTracksSearchResult;
import nl.juraji.biliomi.components.shared.TimeFormatter;
import nl.juraji.biliomi.components.system.channel.ChannelService;
import nl.juraji.biliomi.components.system.settings.SettingsService;
import nl.juraji.biliomi.io.web.Response;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.integrations.SpotifySettings;
import nl.juraji.biliomi.utility.calculate.EnumUtils;
import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.calculate.NumberConverter;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.NormalComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.annotations.SubCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.types.components.Component;
import nl.juraji.biliomi.utility.types.enums.OnOff;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Juraji on 30-9-2017.
 * Biliomi
 */
@Default
@Singleton
@NormalComponent
public class SpotifyComponent extends Component {

    @Inject
    private ChannelService channelService;

    @Inject
    private SpotifyApi spotifyApi;

    @Inject
    private TimeFormatter timeFormatter;

    @Inject
    private SettingsService settingsService;
    private SpotifySettings settings;

    @Override
    public void init() {
        settings = settingsService.getSettings(SpotifySettings.class, s -> settings = s);
    }

    /**
     * Post the current song, playing on Spotify in the chat
     * Usage: !currentsong
     */
    @CommandRoute(command = "currentSong")
    public boolean currentSongCommand(User user, Arguments arguments) {
        if (apiAvailabilityFailed(user)) {
            return false;
        }

        if (!channelService.isStreamOnline()) {
            chat.whisper(user, i18n.get("ChatCommand.currentSong.playerInactive"));
            return false;
        }

        try {
            Response<SpotifyCurrentTrack> response = spotifyApi.getMeCurrentlyPlaying();
            if (!response.isOK()) {
                throw new Exception(response.getRawData());
            }

            SpotifyCurrentTrack currentTrack = response.getData();
            if (currentTrack.isPlaying()) {
                SpotifyTrack item = currentTrack.getItem();
                chat.say(i18n.get("ChatCommand.currentSong.nowPlaying")
                        .add("artist", item::getCombinedArtists)
                        .add("title", item::getName)
                        .add("link", item::getUri)
                        .add("progress", () -> timeFormatter.digitalClockQuantity(currentTrack.getProgressMs()))
                        .add("duration", () -> timeFormatter.digitalClockQuantity(currentTrack.getItem().getDurationMs())));
            } else {
                chat.whisper(user, i18n.get("ChatCommand.currentSong.playerInactive"));
            }

            return true;
        } catch (Exception e) {
            logger.error("Failed retrieving information from Spotify", e);
            chat.whisper(user, i18n.get("Common.failedCommunication"));
            return false;
        }
    }

    /**
     * Request a song (when enabled and available)
     * Usage: !requestsong [Spotify track url or search query]
     * Eg: spotify:track:XXXXXXXXXXXXXXXXXXXXXX
     * Note: If a track is not available in the caster's country it will be marked as not found
     */
    @CommandRoute(command = "requestsong", defaultCooldown = 300000, defaultPrice = 20)
    public boolean requestSongCommand(User user, Arguments arguments) {
        if (!settings.isSongrequestsEnabled()) {
            chat.whisper(user, i18n.get("ChatCommand.requestSong.requestsDisabled"));
            return false;
        }

        if (apiAvailabilityFailed(user)) {
            return false;
        }

        String input = arguments.toString();
        if (StringUtils.isEmpty(input)) {
            chat.whisper(user, i18n.get("ChatCommand.requestSong.usage"));
            return false;
        }

        try {
            SpotifyTrack track;
            if (isSpotifyTrackUrl(input)) {
                track = findSpotifyTrackByTrackUrl(input);
            } else {
                track = findSpotifyTrackByQuery(input);
            }

            if (track == null || !track.isPlayable()) {
                chat.whisper(user, i18n.get("ChatCommand.requestSong.notFound")
                        .add("input", input));
                return false;
            }

            if (settings.getMaxDuration() > 0 && track.getDurationMs() > settings.getMaxDuration()) {
                chat.whisper(user, i18n.get("ChatCommand.requestSong.exceedsMaxDuration")
                        .add("title", track::getName)
                        .add("artist", track::getCombinedArtists)
                        .add("maxduration", () -> timeFormatter.timeQuantity(settings.getMaxDuration())));
                return false;
            }

            Response<SpotifySnapshot> playlistRes = spotifyApi.addToTracksPlaylist(settings.getSongRequestPlaylistId(), track.getUri());
            if (!playlistRes.isOK()) {
                throw new Exception(playlistRes.getRawData());
            }

            SpotifyTrack finalTrack = track;
            chat.say(i18n.get("ChatCommand.requestSong.added")
                    .add("username", user::getDisplayName)
                    .add("artist", finalTrack::getCombinedArtists)
                    .add("title", finalTrack::getName)
                    .add("duration", () -> timeFormatter.timeQuantity(finalTrack.getDurationMs())));
            return true;
        } catch (Exception e) {
            logger.error("Failed retrieving information from Spotify", e);
            chat.whisper(user, i18n.get("Common.failedCommunication"));
            return false;
        }
    }

    /**
     * Main command for manipulating Spotify settings
     * Usage: !spotify [requests|setrequestsplaylist] [more...]
     */
    @CommandRoute(command = "spotify", systemCommand = true)
    public boolean spotifyCommand(User user, Arguments arguments) {
        return captureSubCommands("spotify", () -> i18n.getString("ChatCommand.spotify.usage"), user, arguments);
    }

    /**
     * Enable/disable songrequests
     * Usage: !spotify requests [on or off]
     */
    @SubCommandRoute(parentCommand = "spotify", command = "requests")
    public boolean spotifyRequestsCommand(User user, Arguments arguments) {
        if (apiAvailabilityFailed(user)) {
            return false;
        }

        if (StringUtils.isEmpty(settings.getSongRequestPlaylistId())) {
            chat.whisper(user, i18n.get("ChatCommand.spotify.requests.noPlaylistSet"));
            return false;
        }

        OnOff onOff = EnumUtils.toEnum(arguments.getSafe(0), OnOff.class);

        if (onOff == null) {
            chat.whisper(user, i18n.get("ChatCommand.spotify.requests.usage"));
            return false;
        }

        settings.setSongrequestsEnabled(OnOff.ON.equals(onOff));
        settingsService.save(settings);

        chat.whisper(user, i18n.get("ChatCommand.spotify.requests.set")
                .add("state", () -> i18n.getEnabledDisabled(onOff)));
        return true;
    }

    /**
     * Link a Spotify playlist for song requests
     * Usage: !spotify setrequestsplaylist [Spotify playlist url]
     * Eg: spotify:user:00000000000:playlist:XXXXXXXXXXXXXXXXXXXXXX
     */
    @SubCommandRoute(parentCommand = "spotify", command = "setrequestsplaylist")
    public boolean spotifySetRequestsPlaylistCommand(User user, Arguments arguments) {
        if (apiAvailabilityFailed(user)) {
            return false;
        }

        String playlistUrl = arguments.getSafe(0);
        if (!isSpotifyPlaylistUrl(playlistUrl)) {
            chat.whisper(user, i18n.get("ChatCommand.spotify.setRequestsPlaylist.usage"));
            return false;
        }

        playlistUrl = playlistUrl.substring(34);
        SpotifyPlaylist spotifyPlaylist = null;
        try {
            Response<SpotifyPlaylist> response = spotifyApi.getPlaylist(playlistUrl);
            if (response.isOK()) {
                spotifyPlaylist = response.getData();
            }
        } catch (Exception e) {
            logger.error("Failed retrieving information from Spotify", e);
            chat.whisper(user, i18n.get("Common.failedCommunication"));
            return false;
        }

        if (spotifyPlaylist == null) {
            chat.whisper(user, i18n.get("ChatCommand.spotify.setRequestsPlaylist.playlistNotFound")
                    .add("id", playlistUrl));
            return false;
        }

        settings.setSongRequestPlaylistId(spotifyPlaylist.getId());
        settingsService.save(settings);

        chat.whisper(user, i18n.get("ChatCommand.spotify.setRequestsPlaylist.linked")
                .add("name", spotifyPlaylist::getName));
        return true;
    }

    /**
     * Set the max duration for song requests
     * Usage: !spotify setmaxduration [amount of minutes or 0 to disable]
     */
    @SubCommandRoute(parentCommand = "spotify", command = "setmaxduration")
    public boolean spotifyMaxDurationCommand(User user, Arguments arguments) {
        Long maxDuration = NumberConverter.asNumber(arguments.get(0)).toLong();

        if (maxDuration == null || MathUtils.isNotInRange(maxDuration, 0, Long.MAX_VALUE)) {
            chat.whisper(user, i18n.get("ChatCommand.spotify.setMaxDuration.usage"));
            return false;
        }

        maxDuration = TimeUnit.MINUTES.convert(maxDuration, TimeUnit.MILLISECONDS);
        settings.setMaxDuration(maxDuration);
        settingsService.save(settings);

        if (settings.getMaxDuration() > 0) {
            chat.whisper(user, i18n.get("ChatCommand.spotify.setMaxDuration.set")
                    .add("maxduration", () -> timeFormatter.timeQuantity(settings.getMaxDuration())));
        } else {
            chat.whisper(user, i18n.get("ChatCommand.spotify.setMaxDuration.disabled"));
        }
        return true;
    }

    private SpotifyTrack findSpotifyTrackByTrackUrl(String input) throws Exception {
        input = input.substring(14);
        Response<SpotifyTrack> trackRes = spotifyApi.getTrack(input);
        return (trackRes.isOK() ? trackRes.getData() : null);
    }

    private SpotifyTrack findSpotifyTrackByQuery(String input) throws Exception {
        Response<SpotifyTracksSearchResult> pagingObjectResponse = spotifyApi.searchTrack(input, 1);
        if (pagingObjectResponse.isOK() && pagingObjectResponse.getData().getTracks().getTotal() > 0) {
            return pagingObjectResponse.getData().getTracks().getItems().iterator().next();
        } else {
            return null;
        }
    }

    private boolean isSpotifyTrackUrl(String trackUrl) {
        Matcher matcher = Pattern.compile("^spotify:track:[a-zA-Z0-9]{22}$").matcher(trackUrl);
        return matcher.matches();
    }

    private boolean isSpotifyPlaylistUrl(String playlistUrl) {
        Matcher matcher = Pattern.compile("^spotify:user:[0-9]{11}:playlist:[a-zA-Z0-9]{22}$").matcher(playlistUrl);
        return matcher.matches();
    }

    private boolean apiAvailabilityFailed(User user) {
        try {
            if (!spotifyApi.isAvailable()) {
                chat.whisper(user, i18n.get("Common.notAvailable"));
                return true;
            }
        } catch (Exception e) {
            logger.error("Failed retrieving information from Spotify", e);
            chat.whisper(user, i18n.get("Common.failedCommunication"));
            return true;
        }

        return false;
    }
}
