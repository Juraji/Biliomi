package nl.juraji.biliomi.io.web;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.net.MediaType;
import nl.juraji.biliomi.model.core.VersionInfo;
import nl.juraji.biliomi.utility.calculate.ObjectGraphs;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.AppData;
import nl.juraji.biliomi.utility.factories.marshalling.JacksonMarshaller;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Juraji on 19-4-2017.
 * Biliomi v3
 */
@Default
@Singleton
public class WebClientImpl implements WebClient {

    private Cache<String, Response> cache;
    private HttpClient httpClient;

    @Inject
    private Logger logger;

    @Inject
    private VersionInfo versionInfo;

    @Inject
    @AppData("webclient.cache.duration")
    private Long cacheDuration;

    @PostConstruct
    private void initWebClient() {
        String userAgent = versionInfo.getUserAgent();

        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(cacheDuration, TimeUnit.MILLISECONDS)
                .build();

        SslOverTlsContextFactory contextFactory = new SslOverTlsContextFactory();
        httpClient = new HttpClient(contextFactory);
        httpClient.setMaxConnectionsPerDestination(8);
        httpClient.setUserAgentField(new HttpField(HttpHeader.USER_AGENT, userAgent));
    }

    @PreDestroy
    private void destructWebClient() {
        try {
            if (httpClient != null && !httpClient.isStopped()) {
                httpClient.stop();
            }
        } catch (Exception e) {
            logger.error("Could not stop the web client", e);
        }
    }

    @Override
    public <T> Response<T> get(String uri, HttpFields headers, Class<T> expectedModel) throws Exception {
        Request request = newRequest(uri);
        request.method(HttpMethod.GET);
        return execute(request, headers, expectedModel);
    }

    @Override
    public <T> Response<T> post(String uri, HttpFields headers, String body, MediaType bodyMediaType, Class<T> expectedModel) throws Exception {
        Request request = newRequest(uri);
        request.method(HttpMethod.POST);
        request.content(new StringContentProvider(body, mediaTypeToCharset(bodyMediaType)));

        headers = appendDefaultPostHeaders(headers, body, bodyMediaType);
        return execute(request, headers, expectedModel);
    }

    @Override
    public <T> Response<T> put(String uri, HttpFields headers, String body, MediaType bodyMediaType, Class<T> expectedModel) throws Exception {
        Request request = newRequest(uri);
        request.method(HttpMethod.PUT);
        request.content(new StringContentProvider(body, mediaTypeToCharset(bodyMediaType)));

        headers = appendDefaultPostHeaders(headers, body, bodyMediaType);
        return execute(request, headers, expectedModel);
    }

    @Override
    public <T> Response<T> delete(String uri, HttpFields headers, Class<T> expectedModel) throws Exception {
        Request request = newRequest(uri);
        request.method(HttpMethod.DELETE);
        return execute(request, headers, expectedModel);
    }

    private Charset mediaTypeToCharset(MediaType mediaType) {
        return mediaType.charset().or(Charset.forName("UTF-8"));
    }

    private HttpFields appendDefaultPostHeaders(HttpFields headers, String body, MediaType bodyMediaType) {
        if (headers == null) {
            headers = new HttpFields();
        }

        if (!headers.containsKey(HttpHeader.CONTENT_TYPE.name())) {
            headers.put(HttpHeader.CONTENT_TYPE, bodyMediaType.toString());
        }

        if (!headers.containsKey(HttpHeader.CONTENT_LENGTH.name())) {
            headers.put(HttpHeader.CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        }

        return headers;
    }

    private Request newRequest(String uri) throws Exception {
        if (httpClient.isStopped()) {
            httpClient.start();
        }
        return httpClient.newRequest(uri);
    }

    private <T> Response<T> execute(Request request, HttpFields headers, Class<T> model) {
        String cacheKey = request.getURI().toString();
        //noinspection unchecked
        Response<T> response = null;
        boolean bypassCache = (headers != null && headers.containsKey(NO_CACHE_HEADER));

        if (!bypassCache) {
            //noinspection unchecked
            response = cache.getIfPresent(cacheKey);
        }

        if (response == null) {
            if (headers != null) {
                request.getHeaders().addAll(headers);
            }

            logger.debug("{} {} as {}", request.getMethod(), request.getURI(), model.getSimpleName());

            response = new Response<>();
            response.setRequest(request);

            try {
                ContentResponse cr = request.send();
                response.setStatus(cr.getStatus());
                response.setRawData(cr.getContentAsString().trim());

                if (cr.getStatus() == 200) {
                    if (!ObjectGraphs.isJavaType(model)) {
                        T unmarshal = JacksonMarshaller.unmarshal(cr.getContentAsString(), model);
                        response.setData(unmarshal);
                    }
                }

                if (!bypassCache) {
                    cache.put(cacheKey, response);
                }
            } catch (IOException | InterruptedException | TimeoutException | ExecutionException e) {
                response.setStatus(1);
                response.setRawData(e.getMessage());
            }
        }

        return response;
    }
}
