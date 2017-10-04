package nl.juraji.biliomi.rest.config;

/**
 * Created by Juraji on 18-6-2017.
 * Biliomi v3
 */
public final class RestRequestInfoHolder {

  private static final ThreadLocal<RequestInfo> requestInfo = new ThreadLocal<>();

  public static RequestInfo getRequestInfo() {
    if (requestInfo.get() == null) {
      requestInfo.set(new RequestInfo());
    }
    return requestInfo.get();
  }

  public static class RequestInfo {
    private String username;

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }
  }
}
