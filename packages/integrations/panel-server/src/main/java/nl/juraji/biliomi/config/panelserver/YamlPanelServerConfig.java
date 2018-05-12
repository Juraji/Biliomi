package nl.juraji.biliomi.config.panelserver;

/**
 * Created by Juraji on 30-1-2018.
 * Biliomi
 */
public class YamlPanelServerConfig {
    private boolean enablePanelServer;
    private String serverHost;
    private int serverPort;
    private String serverRoot;
    private String corsAllowedOrigin;

    public boolean isEnablePanelServer() {
        return enablePanelServer;
    }

    public void setEnablePanelServer(boolean enablePanelServer) {
        this.enablePanelServer = enablePanelServer;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getServerRoot() {
        return serverRoot;
    }

    public void setServerRoot(String serverRoot) {
        this.serverRoot = serverRoot;
    }

    public String getCorsAllowedOrigin() {
        return corsAllowedOrigin;
    }

    public void setCorsAllowedOrigin(String corsAllowedOrigin) {
        this.corsAllowedOrigin = corsAllowedOrigin;
    }
}
