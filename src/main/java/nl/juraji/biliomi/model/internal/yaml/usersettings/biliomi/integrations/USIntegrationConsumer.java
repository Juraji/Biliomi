package nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi.integrations;

/**
 * Created by Juraji on 9-10-2017.
 * Biliomi
 */
public class USIntegrationConsumer {
  private String consumerKey;
  private String consumerSecret;

  public String getConsumerKey() {
    return consumerKey;
  }

  public void setConsumerKey(String consumerKey) {
    this.consumerKey = consumerKey;
  }

  public String getConsumerSecret() {
    return consumerSecret;
  }

  public void setConsumerSecret(String consumerSecret) {
    this.consumerSecret = consumerSecret;
  }
}
