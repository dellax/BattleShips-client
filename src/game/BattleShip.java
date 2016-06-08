package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import game.Board.Cell;

public class BattleShip {

    private Game game;
    private boolean onTurn = false;
    private Board enemyBoard, playerBoard;

    private int shipsToPlace = 5;

    private boolean enemyTurn = false;

    private Random random = new Random();

    private Text gameStatus;

    private Stage stage;

    private Parent createContent() {
        BorderPane root = new BorderPane();
        root.setPrefSize(800, 400);

        HBox hboxButtons = new HBox();
        hboxButtons.setPadding(new Insets(15, 12, 15, 12));
        hboxButtons.setSpacing(10);
        hboxButtons.setStyle("-fx-background-color: #336699;");

        Button buttonReady = new Button("Ready");
        buttonReady.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                // TODO send message that player is ready
                if (shipsToPlace == 0) {
                    buttonReady.setDisable(true);
                    gameStatus.setText("You are ready. Waiting for enemy...");
                    game.setPlayerReady();
                } else {
                    gameStatus.setText("Not all ships placed !!!");
                }
            }
        });

        gameStatus = new Text("Waiting for other players to connect");
        gameStatus.setFont(Font.font ("Verdana", 20));
        gameStatus.setFill(Color.WHITE);

        hboxButtons.getChildren().addAll(buttonReady, gameStatus);
        root.setTop(hboxButtons);

        enemyBoard = new Board(true, event -> {
            if (!onTurn)
                return;

            Cell cell = (Cell) event.getSource();
            if (cell.wasShot) {
                // cell was already shot, go again
                return;
            }

            cell.shoot();

            // now goes enemy
            onTurn = false;
            setGameStatus("Enemy turn");
            game.playerShot(cell.x, cell.y);

            if (enemyBoard.ships == 0) {
                System.out.println("YOU WIN");
                setGameStatus("You WIN !!!");
                this.onTurn = false;
            }


        });

        playerBoard = new Board(false, event -> {
            if (onTurn)
                return;

            Cell cell = (Cell) event.getSource();
            if (shipsToPlace == 0) {
                return;
            }
            if (playerBoard.placeShip(new Ship(shipsToPlace, event.getButton() == MouseButton.PRIMARY), cell.x, cell.y)) {
                // add ship to server
                game.addShip(shipsToPlace, event.getButton() == MouseButton.PRIMARY, cell.x, cell.y);
            } else return;
            shipsToPlace--;
        });

        HBox hbox = new HBox(30, playerBoard, enemyBoard);
        hbox.setAlignment(Pos.CENTER);

        root.setCenter(hbox);

        return root;
    }

    /**
     * Set enemy shot based on x, y coordinates
     * @param x Integer
     * @param y Integer
     */
    public void setEnemyMove(int x, int y) {

        Cell cell = playerBoard.getCell(x, y);

        enemyTurn = cell.shoot();

        if (playerBoard.ships == 0) {
            setGameStatus("You LOSE !!!");
            this.onTurn = false;
        }

    }

    /**
     * Add enemy ship to enemyBoard
     * @param type Integer
     * @param vertical Boolean
     * @param x Integer
     * @param y Integer
     */
    public void setEnemyShip(int type, boolean vertical, int x, int y) {
        // place enemy ships
        if (enemyBoard.placeShip(new Ship(type, vertical), x, y)) {

        }
    }

    /**
     * Set status of the game displayed in blue box
     * @param status String
     */
    public void setGameStatus(String status) {
        this.gameStatus.setText(status);
    }

    /**
     * Show primary stage with game
     * @param primaryStage Stage
     * @throws Exception Stage
     */
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;
        Scene scene = new Scene(createContent());
        this.stage.setTitle("Battleship");
        this.stage.setScene(scene);
        this.stage.setResizable(false);
        this.stage.show();
    }

    /**
     * Set title of the game window
     * @param title String
     */
    public void setStageTitle(String title) {
        this.stage.setTitle(title);
    }

    /**
     * Set game object so we can access websockets
     * @param game Game object
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Set game status and which player is on turn
     * @param onTurn Boolean
     */
    public void setGameStarted(boolean onTurn) {
        setGameStatus("Game started. Your turn");
        if (onTurn) {
            setGameStatus("Game started. Your turn");
        } else {
            setGameStatus("Game started. Enemy turn");
        }
        this.onTurn = onTurn;
    }

    /**
     * Set who is on turn and inform user trough game status
     * @param onTurn Boolean
     */
    void setOnTurn(boolean onTurn) {
        if (onTurn) {
            setGameStatus("Your turn");
        } else {
            setGameStatus("Enemy turn");
        }
        this.onTurn = onTurn;
    }
}
