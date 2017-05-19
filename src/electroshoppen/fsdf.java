package electroshoppen;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class fsdf extends Application {
    @Override public void start(Stage stage) {
        WebView web = new WebView();
        System.out.println(
                "Java Version:   " + System.getProperty("java.runtime.version")
        );
        System.out.println(
                "JavaFX Version: " + System.getProperty("javafx.runtime.version"
                ));
        System.out.println(
                "OS:             " + System.getProperty("os.name") + ", "
                        + System.getProperty("os.arch")
        );
        System.out.println(
                "User Agent:     " + web.getEngine().getUserAgent()
        );
        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }
}