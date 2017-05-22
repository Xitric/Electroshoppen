package shared;

import javafx.scene.control.Alert;

/**
 *
 */

public class Utility {

    public static Alert newErrorAlert(String title, String header, String content) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert;
    }

}
