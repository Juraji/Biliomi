package nl.juraji.biliomi.io.web.oauthflow.grants.code;

import nl.juraji.biliomi.utility.factories.concurrent.ThreadPools;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;

/**
 * Created by Juraji on 28-7-2017.
 * Biliomi v3
 */
public class CallbackServer implements CallbackEventListener {

  private final CallbackResources resources;
  private final String stateToken;

  private ServerSocket serverSocket;
  private ExecutorService handlerExecutor;
  private String accessToken;
  private String authorizationError;

  public CallbackServer(CallbackResources resources, String stateToken) {
    this.stateToken = stateToken;
    this.resources = resources;
  }

  @Override
  public void onAccessTokenReceived(String accessToken, String stateToken) {
    if (!this.stateToken.equals(stateToken)) {
      // This is bad, throw a Runtime error so Biliomi shuts down and no more data is sent
      throw new IllegalStateException("Invalid OAuth state token, this might indicate a CSRF attack. Please contact Twitch about this.");
    }

    this.accessToken = accessToken;
    stopCallbackServer();
  }

  @Override
  public void onAuthenticationError(String error) {
    this.authorizationError = error;
    stopCallbackServer();
  }

  /**
   * Start a server listening for request on redirectHost:redirectPort
   *
   * @param accessTokenParamName The Query parameter name of the access token returned by the oauth callback, e.g. "access_token" or "code"
   * @throws IOException When an exception occurs reading/writing to/from a socket
   */
  public void awaitAuthorization(String accessTokenParamName) throws IOException {
    serverSocket = new ServerSocket(resources.getCallbackPort(), 0, InetAddress.getByName(resources.getCallbackHost()));
    runCallbackServer(accessTokenParamName);
  }

  /**
   * The access token received during callback
   *
   * @return The received access token or NULL when no token is received
   */
  public String getAccessToken() {
    return accessToken;
  }

  /**
   * A description of any error occured during authorization
   *
   * @return The message, or NULL of no error occurred
   */
  public String getAuthorizationError() {
    return authorizationError;
  }

  /**
   * Run the callback server handling incoming requests
   *
   * @param accessTokenParamName The name of the code returned by the callback (code, access_token...)
   */
  private void runCallbackServer(String accessTokenParamName) throws IOException {
    handlerExecutor = ThreadPools.newSingleThreadExecutor("OAuthFlowCallbackServer");

    while (true) {
      try {
        Socket socket = serverSocket.accept();
        CallbackRequestHandler handler = new CallbackRequestHandler(socket, accessTokenParamName, this, resources);

        assert handlerExecutor != null;
        handlerExecutor.submit(handler);
      } catch (SocketException e) {
        // Server socket was closed
        break;
      }
    }
  }

  /**
   * Stop the callback server.
   */
  public void stopCallbackServer() {
    if (handlerExecutor != null && !handlerExecutor.isShutdown()) {
      handlerExecutor.shutdownNow();
      handlerExecutor = null;
    }

    if (serverSocket != null && !serverSocket.isClosed()) {
      try {
        serverSocket.close();
      } catch (IOException ignored) {
      } finally {
        serverSocket = null;
      }
    }
  }
}
