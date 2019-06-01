import javafx.fxml.Initializable;
import javafx.scene.Scene;

public class SceneHolder {

    private Scene scene;
    private Initializable controller;
    private String styleCss;
    private String fxml;

    public SceneHolder(Initializable controller, String styleCss, String fxml) {
        this.scene = scene;
        this.controller = controller;
        this.styleCss = styleCss;
        this.fxml = fxml;
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public Initializable getController() {
        return controller;
    }

    public void setController(Initializable controller) {
        this.controller = controller;
    }

    public String getStyleCss() {
        return styleCss;
    }

    public void setStyleCss(String styleCss) {
        this.styleCss = styleCss;
    }

    public String getFxml() {
        return fxml;
    }

    public void setFxml(String fxml) {
        this.fxml = fxml;
    }

}
