package game;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private Game game = new Game();
    private String selectedGameId;

    @FXML
    private Button versionButton;
    @FXML
    private Button createGameButton;
    @FXML
    private Button joinGameButton;

    @FXML
    private ListView listView;

    @FXML public void handleMouseClick(MouseEvent arg0) {
        String clickedGameId = (String) listView.getSelectionModel().getSelectedItem();
        System.out.println("Clicked on gameId " + clickedGameId);
        this.selectedGameId = clickedGameId;
    }

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        assert versionButton != null : "fx:id=\"versionButton\" was not injected: check your FXML file 'sample.fxml'.";
        assert createGameButton != null : "fx:id=\"createGameButton\" was not injected: check your FXML file 'sample.fxml'.";
        assert joinGameButton != null : "fx:id=\"joinGameButton\" was not injected: check your FXML file 'sample.fxml'.";
        assert listView != null : "fx:id=\"listView\" was not injected: check your FXML file 'sample.fxml'.";


        versionButton.setOnAction(event -> {
            game.getVersion();
        });

        createGameButton.setOnAction(event -> {
            // open windows with game
            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            try {
                game.createStage(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
            game.createGame();
        });

        joinGameButton.setOnAction(event -> {
            ObservableList<String> gameList = listView.getItems();
            // check if game exists
            if (selectedGameId == null || !gameList.contains(selectedGameId)) {
                System.out.println("No game selected");
                return;
            }
            // open windows with game
            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            try {
                game.createStage(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
            game.joinGame(selectedGameId);
        });

        game.setController(this);
        game.getGameList();
    }

    public void updateGamesList(ObservableList gameList) {
        listView.setItems(gameList);
    }
}
