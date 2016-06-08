package game;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

class Game {

    private WebSocket ws = new WebSocket(this);
    private BattleShip battleShip = new BattleShip();
    private String gameKey, playerKey;
    private Controller controller;

    Game() {

        ws.connectToWebSocket();
    }

    private void sendMessage(String type, JSONObject jsonObject) {

        JSONObject obj = new JSONObject();

        obj.put("messageType", type);
        obj.put("message", jsonObject);

        ws.sendMessage(obj.toString());
    }

    void processMessage(String message) {
        System.out.println(message);
        JSONObject jsonObject = new JSONObject(message);
        JSONObject jsonData = jsonObject.getJSONObject("data");
        String messageType = jsonData.getString("messageType");
        Object messageObject = jsonData.get("message");
        switch (messageType) {
            case "version":
                getVersionHandler(messageObject);
                break;
            case "allGames":
                allGamesChangedHandler(messageObject);
                break;
            case "preparationStarted":
                preparationStartedHandler(messageObject);
                break;
            case "gameCreated":
                gameCreatedHandler(messageObject);
                break;
            case "disconnect":
                disconnectHandler(messageObject);
                break;
        }
    }

    private void disconnectHandler(Object messageObject) {

        System.out.println(messageObject);
    }

    void getVersion() {

        sendMessage("version", null);
    }

    private void getVersionHandler(Object response) {

        System.out.println(response);
    }

    void createGame() {

        sendMessage("createGame", null);
    }

    /**
     * Get initial list of games from server
     */
    public void getGameList() {
        sendMessage("getGameList", null);
    }

    private void allGamesChangedHandler(Object response) {
        // TODO tu dostaneme zoznam hier v tvare
        // [{"isPlaying":false,"gameKey":"z4vpgbr9qpfu5wmi"},{"isPlaying":false,"gameKey":"zalg3kxqo5x3l3di"}]
        // treba vypisat cez updateGameList funkciu
        JSONArray gamesListJSON = (JSONArray) response;
        ObservableList<String> gameList = FXCollections.observableArrayList();
        for (int i = 0; i < gamesListJSON.length(); i++) {
            JSONObject jsonObject = gamesListJSON.getJSONObject(i);
            boolean isPlaying = jsonObject.getBoolean("isPlaying");
            String gameKey = jsonObject.getString("gameKey");
            if (!isPlaying) {
                gameList.add(gameKey);
            }
        }
        System.out.println(response);
        updateGameList(gameList);
    }

    private void gameCreatedHandler(Object messageObject) {
        JSONObject messageJSON = (JSONObject) messageObject;
        String gameKey = messageJSON.getString("gameKey");
        Platform.runLater(() -> {
            this.battleShip.setStageTitle("BattleShips gameID " + gameKey);
        });
    }

    /**
     * Function to join selected game with gameKey.
     * @param gameKey Must be string
     */
    public void joinGame(String gameKey) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("gameKey", gameKey);

        sendMessage("joinGame", jsonObject);
        Platform.runLater(() -> {
            this.battleShip.setStageTitle("BattleShips gameID " + gameKey);
        });
    }

    private void preparationStartedHandler(Object messageObject) {
        JSONObject messageJSON = (JSONObject) messageObject;
        System.out.println(messageJSON.getString("playerKey"));
        this.playerKey = messageJSON.getString("playerKey");
        this.battleShip.setGameStatus("All players connected. Set your ships now!");
    }

    private void addShip(int type, String playerKey, String gameKey, int x, int y) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("gameKey", gameKey);
        jsonObject.put("playerKey", playerKey);
        jsonObject.put("type", type);
        jsonObject.put("x", x);
        jsonObject.put("y", y);

        sendMessage("addShip", jsonObject);
    }

    private void leaveGame(String gameKey, String playerKey) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("gameKey", gameKey);
        jsonObject.put("playerKey", playerKey);

        sendMessage("leaveGame", jsonObject);
    }

    private void setGameKey(String gameKey) {
        this.gameKey = gameKey;
    }

    private void setPlayerKey(String playerKey) {
        this.playerKey = playerKey;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Function to update list of games
     * @param gameList ObservableList
     */
    public void updateGameList(ObservableList gameList) {
        // to prevent thread error
        Platform.runLater(() -> this.controller.updateGamesList(gameList));
    }

    /**
     * Function to create stage with game.
     * @param stage current stage
     * @throws Exception
     */
    public void createStage(Stage stage) throws Exception {
        battleShip.start(stage);
    }
}
