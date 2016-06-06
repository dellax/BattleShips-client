package game;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
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
    private TextField textInput;

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
        assert textInput != null : "fx:id=\"textInput\" was not injected: check your FXML file 'sample.fxml'.";
        assert listView != null : "fx:id=\"listView\" was not injected: check your FXML file 'sample.fxml'.";

        // game list


        versionButton.setOnAction(event -> {
            game.getVersion();
        });
        createGameButton.setOnAction(event -> {
            game.createGame();
            // TODO open window with game
        });
        joinGameButton.setOnAction(event -> {
            ObservableList<String> gameList = listView.getItems();
            // check if game exists
            if (selectedGameId == null || !gameList.contains(selectedGameId)) {
                System.out.println("No game selected");
                return;
            }
            // TODO open window with game
            //
            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            Parent root = null;/* Exception */
            try {
                root = FXMLLoader.load(getClass().getResource("game.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        });

        game.setController(this);
        game.getGameList();
    }

    public void updateGamesList(ObservableList gameList) {
        listView.setItems(gameList);
    }
}
