package shared.presentation;

import javafx.scene.control.Alert;

/**
 * Class containing utility methods for creating JavaFX alert dialogs.
 *
 * @author Mads
 * @author Kasper
 */
public class AlertUtil {

	/**
	 * Constructs a new alert dialog with the specified header and content text.
	 *
	 * @param header  the header text of the alert dialog
	 * @param content the content text of the alert dialog
	 * @return the resulting alert dialog
	 */
	public static Alert newErrorAlert(String header, String content) {
		return newErrorAlert("Error", header, content);
	}

	/**
	 * Constructs a new alert dialog with the specified title, header, and content text.
	 *
	 * @param title   the title of the alert dialog
	 * @param header  the header text of the alert dialog
	 * @param content the content text of the alert dialog
	 * @return the resulting alert dialog
	 */
	public static Alert newErrorAlert(String title, String header, String content) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		return alert;
	}
}
