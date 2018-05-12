package nl.juraji.biliomi.io.web.sockets;

import com.neovisionaries.ws.client.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Juraji on 3-10-2017.
 * Biliomi
 */
public abstract class SocketClient extends WebSocketAdapter {
    public static final int CONNECTION_TIMEOUT = 5000; // 5 seconds
    public static final int RECONNECT_TIMEOUT = 60000; // 60 seconds

    protected final Logger logger;

    public SocketClient() {
        this.logger = LogManager.getLogger(getClass());
    }

    @Override
    public void onSendingFrame(WebSocket websocket, WebSocketFrame frame) {
        if (frame.isTextFrame()) {
            logger.debug("OUT: " + frame.getPayloadText().trim());
        } else {
            logger.debug("OUT: Opcode " + frame.getOpcode());
        }
    }

    @Override
    public void onFrame(WebSocket websocket, WebSocketFrame frame) {
        if (frame.isTextFrame()) {
            logger.debug("IN: " + frame.getPayloadText().trim());
        } else {
            logger.debug("IN: Opcode " + frame.getOpcode());
        }
    }

    @Override
    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
        // If the connection state is CLOSED this is a reconnect attempt
        //noinspection Duplicates
        if (websocket.getState().equals(WebSocketState.CLOSED) || closedByServer) {
            logger.info("Websocket got disconnected, retrying in " + (RECONNECT_TIMEOUT / 1000) + " seconds");
            // If the connection was closed by Twitch or a network error try reconnecting
            Thread.sleep(RECONNECT_TIMEOUT);
            try {
                websocket.recreate().connect();
            } catch (WebSocketException e) {
                // Connect errors should be handled by onError
                onError(websocket, e);
            }
        }
    }

    @Override
    public void onError(WebSocket websocket, WebSocketException e) throws Exception {
        // Ignore FLUSH_ERROR
        if (!e.getError().equals(WebSocketError.FLUSH_ERROR)) {
            logger.error("An exception occurred", e);
            onDisconnected(websocket, null, null, true);
        }
    }
}
