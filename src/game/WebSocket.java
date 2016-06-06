package game;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

@ClientEndpoint
public class WebSocket {

    private Session session;
    private static final Logger LOGGER = Logger.getLogger(WebSocket.class.getName());
    private Game game;

    public WebSocket(Game game) {

        this.game = game;
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }

    @OnMessage
    public void onMessage(String message) {

        game.processMessage(message);
    }

    @OnClose
    public void onClose() {
        connectToWebSocket();
    }

    public void connectToWebSocket() {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            URI uri = URI.create("ws://localhost:8000/");
            container.connectToServer(this, uri);
        } catch (DeploymentException | IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void sendMessage(String message) {
        RemoteEndpoint.Basic other = session.getBasicRemote();
        try {
            other.sendText(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
