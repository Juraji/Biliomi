package nl.juraji.biliomi.io.web.oauthflow.grants.code;

import nl.juraji.biliomi.io.web.Url;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Juraji on 28-7-2017.
 * Biliomi v3
 */
public class CallbackRequestHandler implements Runnable {

  private final Socket socket;
  private final String accessTokenParamName;
  private final CallbackEventListener eventListener;
  private final CallbackResources resources;

  public CallbackRequestHandler(Socket socket, String accessTokenParamName, CallbackEventListener eventListener, CallbackResources resources) {
    this.socket = socket;
    this.accessTokenParamName = accessTokenParamName;
    this.eventListener = eventListener;
    this.resources = resources;
  }

  @Override
  public void run() {
    try (BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())) {

      // The first line is the request line, that is all we need so close the input reader
      // [METHOD] [REQUEST URI] [HTTP PROTOCOL]
      String requestLine = inputReader.readLine();
      StringTokenizer requestTokens = new StringTokenizer(requestLine);

      // The first token is the method, which most probably is GET
      String requestMethod = requestTokens.nextToken();
      if (!"GET".equals(requestMethod)) {
        // If the method is not GET we're just going to ignore the request
        return;
      }

      // The second token is the actual request uri
      String requestUri = requestTokens.nextToken();

      // Parse Query string
      Map<String, String> queryParams = Url.unpackQueryString(requestUri);

      try {
        if (queryParams.size() == 0) {
          // There were no query params, the token most probably came via hash data
          // The redirect page rewrites this to query parameters then comes back here
          sendFile(resources.getAuthHashRedirectPageFilePath(), outputStream);
        } else {
          updateCallbackEventListener(queryParams);

          if (queryParams.containsKey(accessTokenParamName)) {
            // Authorization was succesful
            sendFile(resources.getAuthSuccessPageFilePath(), outputStream);
          } else {
            // Authorization failed
            sendFile(resources.getAuthFailedPageFilePath(), outputStream);
          }
        }
      } catch (IllegalStateException e) {
        sendFile(resources.getAuthFailedPageFilePath(), outputStream);
        throw e;
      }
    } catch (IOException | URISyntaxException e) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, "An error occurred during communication", e);
    }
  }

  private void updateCallbackEventListener(Map<String, String> queryParams) {
    if (eventListener != null) {
      if (queryParams.containsKey(accessTokenParamName)) {
        String accessToken = queryParams.get(accessTokenParamName);
        String stateToken = Url.decode(queryParams.get("state"));
        eventListener.onAccessTokenReceived(accessToken, stateToken);
      } else if (queryParams.containsKey("error")) {
        String error = queryParams.getOrDefault("error", "Unknown Error");
        eventListener.onAuthenticationError(error);
      }
    }
  }

  private void sendFile(String fileResourcePath, DataOutputStream os) throws IOException, URISyntaxException {
    try (InputStream resourceStream = CallbackRequestHandler.class.getResourceAsStream(fileResourcePath)) {
      os.writeBytes("HTTP/1.1 200 OK\r\n");
      os.writeBytes("Content-Type: text/html\r\n");
      os.writeBytes("\r\n");


      byte[] buffer = new byte[resourceStream.available()];
      resourceStream.read(buffer);
      os.write(buffer);
    } catch (Exception e) {
      Logger.getLogger(getClass().getName()).severe("Resource was missing: " + fileResourcePath);

      os.writeBytes("HTTP/1.1 404 Not Found\r\n");
      os.writeBytes("\r\n");
      os.writeBytes("404 Not Found");
    }
  }
}
