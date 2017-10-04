package nl.juraji.biliomi.io.api.steam.v1;

import nl.juraji.biliomi.io.api.steam.v1.model.wrappers.SteamLibraryResponse;
import nl.juraji.biliomi.io.web.Response;

/**
 * Created by Juraji on 25-5-2017.
 * Biliomi v3
 */
public interface SteamApi {
  Response<SteamLibraryResponse> getLibrary() throws Exception;
}
