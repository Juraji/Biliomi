package nl.juraji.biliomi.io.api.github.v3;

import nl.juraji.biliomi.io.api.github.v3.model.GithubRelease;
import nl.juraji.biliomi.io.web.Response;
import nl.juraji.biliomi.io.web.Url;
import nl.juraji.biliomi.io.web.WebClient;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.AppDataValue;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

/**
 * Created by Juraji on 6-10-2017.
 * Biliomi
 */
@Default
public class GithubApiImpl implements GithubApi {

  @Inject
  @AppDataValue("github.api.uris.v3")
  private String apiBaseUri;


  @Inject
  private WebClient webClient;

  private final HttpFields headers = new HttpFields();

  @PostConstruct
  private void initGithubApi() {
    headers.put(HttpHeader.ACCEPT, "application/vnd.github.v3+json");
    headers.put(WebClient.NO_CACHE_HEADER, "true");
  }

  @Override
  public Response<GithubRelease> getLatestRelease(String owner, String repository) throws Exception {
    return webClient.get(Url.url(apiBaseUri, "repos", owner, repository, "releases", "latest"), headers, GithubRelease.class);
  }
}
