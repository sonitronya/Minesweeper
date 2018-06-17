package UI;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import logic.Polygon;
import logic.PolygonType;
import logic.Time;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

public class Main extends Application {

    private static Stage mainStage = null;
    private static Graphics graphics;
    private static int level = 0;
    private static Label time = new Label();
    public static int countBomb = 25;

    public void start(Stage primaryStage) {
        menu();
    }

    private static void startNewGame() {

        int sizeOfLevel = 500;

        switch (level) {
            case 1:
                sizeOfLevel = 500;
                countBomb = 25;
                break;
            case 2:
                sizeOfLevel = 600;
                countBomb = 45;
                break;
            case 3:
                sizeOfLevel = 700;
                countBomb = 65;
                break;
            case 4:
                sizeOfLevel = 800;
                countBomb = 85;
                break;
            case 5:
                sizeOfLevel = 900;
                countBomb = 150;
                break;
        }

        Group root = new Group();
        Scene scene = new Scene(root, sizeOfLevel, sizeOfLevel + 25);

        Canvas canvas = new Canvas(sizeOfLevel, sizeOfLevel);

        canvas.setLayoutY(35);

        Label timeLabel = new Label("00:00:00");

        timeLabel.setLayoutX(scene.getWidth() / 2 - timeLabel.getWidth());
        Time time = new Time(timeLabel);

        time.restart();

        (new Thread(time)).start();

        root.getChildren().addAll(timeLabel, canvas);


        if (mainStage != null) mainStage.close();
        mainStage = new Stage();
        mainStage.setScene(scene);
        mainStage.setOnCloseRequest(event -> System.exit(0));
        mainStage.setResizable(false);
        mainStage.show();

        graphics = new Graphics(canvas.getGraphicsContext2D(), canvas.getHeight(), canvas.getWidth());

        List<Polygon> polygonList = Polygon.getPolygons();
        Random random = new Random();

        IntStream.range(0, countBomb).map(i ->
                random.nextInt(polygonList.size())).forEachOrdered(randomIndex ->
                polygonList.get(randomIndex).setPolygonType(PolygonType.HIDDEN_BOMB));

        canvas.setOnMouseClicked(event -> Polygon.click(event, graphics));
    }

    private static void menu() {

        Stage menu = new Stage();
        GridPane root = new GridPane();
        root.setAlignment(Pos.CENTER);
        root.setVgap(10);

        Scene scene = new Scene(root, 150, 250);

        Label levelLabel = new Label("Choose level:");

        int[] levels = new int[]{1, 2, 3, 4, 5};

        ChoiceBox<String> choiceLevels = new ChoiceBox<>(FXCollections.observableArrayList(
                "Small", "Normal", "Middle", "Hard", "Very hard"));
        choiceLevels.getSelectionModel().selectFirst();
        choiceLevels.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) ->
                level = levels[newValue.intValue()]);

        Button startGame = new Button("Start!");
        startGame.setOnMouseClicked(event -> {
            menu.close();
            startNewGame();
        });

        root.add(levelLabel, 0, 0);
        root.add(choiceLevels, 0, 1);
        root.add(startGame, 0, 2);

        menu.setScene(scene);
        menu.setResizable(false);
        menu.setOnCloseRequest(event -> System.exit(0));
        menu.show();
    }

    public static void gameOver(boolean userLose) {
        Time.stop();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("U " + (userLose ? "lose." : "win!") + " Again?");

        Optional<ButtonType> button = alert.showAndWait();

        if (button.get() == ButtonType.OK) {
            mainStage.close();
            menu();
        } else {
            System.exit(0);
        }
    }
}
