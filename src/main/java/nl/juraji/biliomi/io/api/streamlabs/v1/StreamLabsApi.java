package nl.juraji.biliomi.io.api.streamlabs.v1;

import nl.juraji.biliomi.io.api.streamlabs.v1.model.StreamLabsSocketToken;
import nl.juraji.biliomi.io.api.streamlabs.v1.model.StreamLabsTwitchUser;
import nl.juraji.biliomi.io.web.Response;

/**
 * Created by Juraji on 2-10-2017.
 * Biliomi
 */
public interface StreamLabsApi {

  /**
   * Get information about the linked Stream Labs account's owner
   *
   * @see <a href="https://dev.streamlabs.com/v1.0/reference#user">/user</a>
   */
  Response<StreamLabsTwitchUser> getMe() throws Exception;

  /**
   * Retrieve a socket token
   *
   * @see <a href="https://dev.streamlabs.com/v1.0/reference#sockettoken">/socket/token</a>
   */
  Response<StreamLabsSocketToken> getSocketToken() throws Exception;
}
