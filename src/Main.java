import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main extends Application {

    private Stage primaryStage;
    private Map<Integer,SceneHolder> sceneHolders = new HashMap<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle(AppConfigConstants.APP_NAME);
        this.primaryStage = primaryStage;

        createScenes();
        showScene(1);
    }

    private void showScene(int sceneNumber) throws IOException {
        SceneHolder sceneHolder = sceneHolders.get(sceneNumber);
        FXMLLoader loader = new FXMLLoader(getClass().getResource(sceneHolder.getFxml()));
        loader.setController(sceneHolder.getController());
        Pane root = loader.load();
        Scene scene = new Scene(root, AppConfigConstants.APP_WIDTH, AppConfigConstants.APP_HEIGHT);
        scene.getStylesheets().add(getClass().getResource(sceneHolder.getStyleCss()).toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
        sceneHolder.setScene(scene);
        sceneHolders.put(sceneNumber, sceneHolder);
    }

    private void createScenes() {
        SceneHolder sceneHolder = new SceneHolder(new Controller(), "style.css", "SceneOne.fxml");
        sceneHolders.put(1, sceneHolder);

        //create other scenes TODO
    }

    public static void main(String[] args) {
        launch(args);
    }
}
