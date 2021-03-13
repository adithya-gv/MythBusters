package controller;
import gamefiles.*;
import model.GameModel;
import views.ConfigurationScreen;
import views.GameScreen;
import views.WelcomeScreen;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import views.WinScreen;


public class Controller extends Application {
    private static Stage mainWindow;
    private GameModel gameModel;
    private static final int W = 1200;
    private static final int H = 800;

    private Player p1;
    private static GameScreen gameScreen;

    public void start(Stage primaryStage) throws Exception {
        mainWindow = primaryStage;
        mainWindow.setTitle("MythBusters!");
        gameModel = new GameModel();
        p1 = new Player(0);
        WeaponDatabase.initialize();
        initWelcomeScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void initWelcomeScreen() {
        WelcomeScreen welcomeScreen = new WelcomeScreen(W, H);
        Scene scene = welcomeScreen.getScene();
        Button startGameButton = welcomeScreen.getStartButton();

        startGameButton.setOnAction(e -> {
            goToConfigurationScreen();
        });
        welcomeScreen.setBinds(mainWindow);

        mainWindow.setScene(scene);
        mainWindow.show();
    }

    public void goToConfigurationScreen() {
        ConfigurationScreen configScreen = new ConfigurationScreen(W, H);
        Button beginButton = configScreen.getBeginButton();
        TextField heroNameField = configScreen.getHeroNameField();
        ComboBox<StartingWeapon> startingWeaponSelector = configScreen.getStartingWeaponSelector();
        ComboBox<Difficulty> difficultySelector = configScreen.getDifficultySelector();

        beginButton.addEventHandler(ActionEvent.ACTION, (e) -> {
            if (heroNameField.getText().length() < 1
                    || heroNameField.getText().trim().isEmpty()) {
                showAlert("Your name cannot be empty or whitespace only!");
                return;
            }
            initializeStats(heroNameField.getText(),
                    startingWeaponSelector.getSelectionModel().getSelectedIndex(),
                    difficultySelector.getValue());
            goToRoomOne();
        });
        Scene scene = configScreen.getScene();
        mainWindow.setScene(scene);
        mainWindow.show();
        configScreen.setBinds(mainWindow);

    }

    public void goToRoomOne() {
        RoomLayout roomLayout = new RoomLayout();
        gameScreen = new GameScreen(W, H, p1, roomLayout);

        Scene scene = gameScreen.getScene();
        mainWindow.setScene(scene);
        mainWindow.show();
    }

    public static void updateRoom() {
        Scene scene = gameScreen.getScene();
        mainWindow.setScene(scene);
        mainWindow.show();
    }

    public void goToWinScreen() {
        WinScreen winScreen = new WinScreen(W, H);
        Scene scene = winScreen.getScene();
        mainWindow.setScene(scene);
        mainWindow.show();
    }


    /**
     * Set initial parameters
     *
     * @param nameEntry           the name of the hero
     * @param startingWeaponIndex the index of the starting weapon
     * @param difficultyEntry     the difficulty
     */
    private void initializeStats(String nameEntry,
                                 int startingWeaponIndex, Difficulty difficultyEntry) {
        p1.setName(nameEntry);
        p1.setWeapon(WeaponDatabase.getWeapon(startingWeaponIndex));
        Difficulty difficulty = difficultyEntry;
        switch (difficulty) {
        case EASY:
            p1.setCoins(30);
            break;
        case MEDIUM:
            p1.setCoins(20);
            break;
        case HARD:
            p1.setCoins(10);
            break;
        default: // unnecessary because of type safety
        }
    }

    /**
     * Alert Method
     * @param message Message to display in alert.
     */
    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        alert.showAndWait();
        return;
    }

    /**
     * @return the player object
     */
    public Player getPlayer() {
        return p1;
    }

    /**
     * @return the first room
     */
    public GameScreen getRoomOne() {
        return gameScreen;
    }

    /**
     * Private testing method to return a String representation of the Label of the window.
     * @return the string representing the label of the window.
     */
    public String getWindowTitle() {
        return mainWindow.getTitle();
    }

    public static int getW() {
        return W;
    }
    public static int getH() {
        return H;
    }
}
