package nl.juraji.biliomi.config.core.biliomi;

import nl.juraji.biliomi.config.core.biliomi.twitch.USTwitchLogin;

/**
 * Created by Juraji on 9-10-2017.
 * Biliomi
 */
public class USTwitch {
    private String clientId;
    private String clientSecret;
    private String webhookCallbackUrl;
    private USTwitchLogin login;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getWebhookCallbackUrl() {
        return webhookCallbackUrl;
    }

    public void setWebhookCallbackUrl(String webhookCallbackUrl) {
        this.webhookCallbackUrl = webhookCallbackUrl;
    }

    public USTwitchLogin getLogin() {
        return login;
    }

    public void setLogin(USTwitchLogin login) {
        this.login = login;
    }
}
