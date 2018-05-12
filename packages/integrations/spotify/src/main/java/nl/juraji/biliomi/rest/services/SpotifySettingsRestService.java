package nl.juraji.biliomi.rest.services;

import nl.juraji.biliomi.components.integrations.spotify.api.v1.SpotifyApi;
import nl.juraji.biliomi.components.integrations.spotify.api.v1.model.playlist.SpotifyPlaylist;
import nl.juraji.biliomi.io.web.Response;
import nl.juraji.biliomi.model.core.security.tokens.AuthTokenDao;
import nl.juraji.biliomi.model.core.security.tokens.TokenGroup;
import nl.juraji.biliomi.model.integrations.SpotifySettings;
import nl.juraji.biliomi.rest.config.SettingsModelRestService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.Path;

/**
 * Created by Juraji on 1-10-2017.
 * Biliomi
 */
@Path("/social/spotify/settings")
public class SpotifySettingsRestService extends SettingsModelRestService<SpotifySettings> {

    @Inject
    private AuthTokenDao authTokenDao;

    @Inject
    private Logger logger;

    @Inject
    private SpotifyApi spotifyApi;

    @Override
    public SpotifySettings getEntity() {
        SpotifySettings settings = settingsService.getSettings(SpotifySettings.class);

        // Set integration enabled process variable
        settings.set_integrationEnabled(authTokenDao.isTokenPresent(TokenGroup.INTEGRATIONS, "spotify"));

        return settings;
    }

    @Override
    public SpotifySettings updateEntity(SpotifySettings e) {
        SpotifySettings settings = settingsService.getSettings(SpotifySettings.class);

        settings.setSongrequestsEnabled(e.isSongrequestsEnabled());

        if (StringUtils.isEmpty(e.getSongRequestPlaylistId())) {
            settings.setSongRequestPlaylistId(null);
        } else {
            if (!settings.getSongRequestPlaylistId().equals(e.getSongRequestPlaylistId())) {
                try {
                    Response<SpotifyPlaylist> response = spotifyApi.getPlaylist(e.getSongRequestPlaylistId());
                    if (response.isOK()) {
                        settings.setSongRequestPlaylistId(e.getSongRequestPlaylistId());
                    } else {
                        throw new Exception(response.getRawData());
                    }
                } catch (Exception e1) {
                    logger.error("Failed communication with Spotify", e);
                    return null;
                }
            }
        }

        settingsService.save(settings);
        return settings;
    }
}
