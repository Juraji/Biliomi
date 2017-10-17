package nl.juraji.biliomi.components.integrations.streamlabs;

import nl.juraji.biliomi.components.integrations.streamlabs.api.streamlabs.socket.StreamLabsSocketSession;
import nl.juraji.biliomi.components.integrations.streamlabs.api.streamlabs.v1.StreamLabsApi;
import nl.juraji.biliomi.components.integrations.streamlabs.api.streamlabs.v1.model.StreamLabsSocketToken;
import nl.juraji.biliomi.components.integrations.streamlabs.api.streamlabs.v1.model.StreamLabsTwitchUser;
import nl.juraji.biliomi.components.interfaces.Component;
import nl.juraji.biliomi.io.web.Response;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.NormalComponent;
import nl.juraji.biliomi.utility.exceptions.UnavailableException;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Juraji on 2-10-2017.
 * Biliomi
 */
@Default
@Singleton
@NormalComponent
public class StreamLabsComponent extends Component {

  @Inject
  private StreamLabsApi streamLabsApi;

  @Inject
  private Instance<StreamLabsSocketSession> streamLabsSocketSessionInstance;

  @Override
  public void init() {
    initStreamLabsSocketSession();
  }

  private void initStreamLabsSocketSession() {
    String accountName = null;
    String socketToken = null;

    try {
      accountName = getAccountName();
      socketToken = getSocketToken();
    } catch (UnavailableException e) {
      logger.debug("Stream Labs integration is unavailable", e);
    } catch (Exception e) {
      logger.error(e);
    }

    if (StringUtils.isNotEmpty(accountName) && StringUtils.isNotEmpty(socketToken)) {
      StreamLabsSocketSession streamLabsSocketSession = streamLabsSocketSessionInstance.get();
      streamLabsSocketSession.setSocketToken(socketToken);
      streamLabsSocketSession.start();
      logger.info("Started Stream Labs socket session for " + accountName);
    }
  }

  private String getAccountName() throws Exception {
    Response<StreamLabsTwitchUser> meResponse = streamLabsApi.getMe();

    if (!meResponse.isOK()) {
      return null;
    }
    return meResponse.getData().getUser().getDisplayName();
  }

  private String getSocketToken() throws Exception {
    Response<StreamLabsSocketToken> response = streamLabsApi.getSocketToken();

    if (!response.isOK()) {
      return null;
    }

    return response.getData().getToken();
  }
}
