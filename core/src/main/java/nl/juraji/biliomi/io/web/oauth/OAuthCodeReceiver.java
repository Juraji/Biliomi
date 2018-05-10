package nl.juraji.biliomi.io.web.oauth;

import fi.iki.elonen.NanoHTTPD;
import nl.juraji.biliomi.io.web.Url;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created by Juraji on 10-5-2018.
 * Biliomi
 */
public final class OAuthCodeReceiver {
    private final URI redirectUri;
    private final String stateToken;

    public OAuthCodeReceiver(String redirectUri, String stateToken) {
        this.redirectUri = URI.create(redirectUri);
        this.stateToken = stateToken;
    }

    public String awaitAccessCode() throws ExecutionException, InterruptedException, IOException {
        final CompletableFuture<String> future = new CompletableFuture<>();

        final NanoHTTPD callBackServer = new NanoHTTPD(redirectUri.getHost(), redirectUri.getPort()) {

            @Override
            public Response serve(IHTTPSession session) {
                if (!Method.GET.equals(session.getMethod())) {
                    return newFixedLengthResponse(Response.Status.FORBIDDEN, MIME_HTML, "FORBIDDEN");
                }

                final String uri = session.getUri();
                if (!uri.endsWith(redirectUri.getPath())) {
                    return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_HTML, "");
                }

                final String query = session.getQueryParameterString();
                final Map<String, String> queryMap = Url.unpackQueryString(query, true);

                try {
                    if (queryMap.size() == 0) {
                        return createResourceResponse("/oauth/auth-hash-redirect.html");
                    } else {
                        try {
                            String token = extractAccessToken(queryMap);
                            future.complete(token);
                            return createResourceResponse("/oauth/auth-success.html");
                        } catch (Exception e) {
                            future.completeExceptionally(e);
                            return createResourceResponse("/oauth/auth-failed.html");
                        }
                    }
                } catch (IOException e) {
                    future.completeExceptionally(e);
                }

                return newFixedLengthResponse("OK");
            }
        };

        callBackServer.start();
        final String result = future.get();
        callBackServer.stop();
        return result;
    }

    private String extractAccessToken(Map<String, String> queryMap) throws Exception {
        if (!queryMap.containsKey("state") || !stateToken.equals(Url.decode(queryMap.get("state")))) {
            throw new Exception("State token invalid!");
        }

        if (queryMap.containsKey("code")) {
            return queryMap.get("code");
        } else {
            final String error = queryMap.getOrDefault("error", "Unknown error");
            throw new Exception(error);
        }
    }

    private NanoHTTPD.Response createResourceResponse(String path) throws IOException {
        final InputStream stream = OAuthCodeReceiver.class.getResourceAsStream(path);
        return NanoHTTPD.newFixedLengthResponse(
                NanoHTTPD.Response.Status.OK,
                NanoHTTPD.MIME_HTML,
                stream,
                stream.available());
    }
}
