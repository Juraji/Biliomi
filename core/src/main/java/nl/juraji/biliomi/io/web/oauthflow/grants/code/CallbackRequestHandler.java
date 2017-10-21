package nl.juraji.biliomi.io.web.oauthflow.grants.code;

import nl.juraji.biliomi.io.web.Url;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Juraji on 28-7-2017.
 * Biliomi v3
 */
public class CallbackRequestHandler implements Runnable {

  private static final String HTTP_EOL = "\r\n";
  private static final String CONTENT_TYPE_HTML = "text/html";

  private final Socket socket;
  private final String accessTokenParamName;

  private CallbackEventListener callbackEventListener;

  public CallbackRequestHandler(Socket socket, String accessTokenParamName) {
    this.socket = socket;
    this.accessTokenParamName = accessTokenParamName;
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
          sendFile(CallbackResources.getAuthHashRedirectPageFile(), CONTENT_TYPE_HTML, outputStream);
        } else {
          updateCallbackEventListener(queryParams);

          if (queryParams.containsKey(accessTokenParamName)) {
            // Authorization was succesful
            sendFile(CallbackResources.getAuthSuccessPageFile(), CONTENT_TYPE_HTML, outputStream);
          } else {
            // Authorization failed
            sendFile(CallbackResources.getAuthFailedPageFile(), CONTENT_TYPE_HTML, outputStream);
          }
        }
      } catch (IllegalStateException e) {
        sendFile(CallbackResources.getAuthFailedPageFile(), CONTENT_TYPE_HTML, outputStream);
        throw e;
      }
    } catch (IOException | URISyntaxException e) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, "An error occurred during communication", e);
    }
  }

  public void setCallbackEventListener(CallbackEventListener callbackEventListener) {
    this.callbackEventListener = callbackEventListener;
  }

  private void updateCallbackEventListener(Map<String, String> queryParams) {
    if (callbackEventListener != null) {
      if (queryParams.containsKey(accessTokenParamName)) {
        String accessToken = queryParams.get(accessTokenParamName);
        String stateToken = queryParams.get("state");
        callbackEventListener.onAccessTokenReceived(accessToken, stateToken);
      } else if (queryParams.containsKey("error")) {
        String error = queryParams.getOrDefault("error", "Unknown Error");
        callbackEventListener.onAuthenticationError(error);
      }
    }
  }

  private void sendFile(URL fileUrl, String contentType, DataOutputStream os) throws IOException, URISyntaxException {
    File file = new File(fileUrl.toURI());

    if (file.exists()) {
      os.writeBytes("HTTP/1.1 200 OK" + HTTP_EOL);
      os.writeBytes("Content-Type: " + contentType + HTTP_EOL);
      os.writeBytes(HTTP_EOL);

      byte[] bytes = FileUtils.readFileToByteArray(file);
      IOUtils.write(bytes, os);
    } else {
      os.writeBytes("HTTP/1.1 404 Not Found" + HTTP_EOL);
      os.writeBytes(HTTP_EOL);
      os.writeBytes("404 Not Found");
    }
  }
}
