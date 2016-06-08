package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
            // TODO now goes enemy
            onTurn = false;
            game.playerShot(cell.x, cell.y);

            if (enemyBoard.ships == 0) {
                System.out.println("YOU WIN");
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
            }
            shipsToPlace--;
        });

        HBox hbox = new HBox(30, playerBoard, enemyBoard);
        hbox.setAlignment(Pos.CENTER);

        root.setCenter(hbox);

        return root;
    }

    public void setEnemyMove(int x, int y) {

        Cell cell = playerBoard.getCell(x, y);

        enemyTurn = cell.shoot();

        if (playerBoard.ships == 0) {
            System.out.println("YOU LOSE");
        }

    }

    public void setEnemyShip(int type, boolean vertical, int x, int y) {
        // place enemy ships
        if (enemyBoard.placeShip(new Ship(type, vertical), x, y)) {

        }
    }

    public void setGameStatus(String status) {
        this.gameStatus.setText(status);
    }

    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;
        Scene scene = new Scene(createContent());
        this.stage.setTitle("Battleship");
        this.stage.setScene(scene);
        this.stage.setResizable(false);
        this.stage.show();
    }

    void setStageTitle(String title) {
        this.stage.setTitle(title);
    }

    void setGame(Game game) {
        this.game = game;
    }

    void setGameStarted(boolean onTurn) {
        setGameStatus("Game started. Your turn");
        // 1. set enemy ships
        // 2. set game status
        this.onTurn = onTurn;
    }

    void setOnTurn(boolean onTurn) {
        this.onTurn = onTurn;
    }
}
