package nl.juraji.biliomi.config.panelserver;

import nl.juraji.biliomi.config.ConfigService;

/**
 * Created by Juraji on 30-1-2018.
 * Biliomi
 */
public class PanelServerConfigService extends ConfigService<YamlPanelServerConfig> {

    public PanelServerConfigService() {
        super("integrations/panel-server.yml", YamlPanelServerConfig.class);
    }

    public boolean isEnablePanelServer() {
        return config.isEnablePanelServer();
    }

    public String getServerHost() {
        return config.getServerHost();
    }

    public int getServerPort() {
        return config.getServerPort();
    }

    public String getServerRoot() {
        return config.getServerRoot();
    }

    public String getCorsAllowedOrigin() {
        return config.getCorsAllowedOrigin();
    }
}
