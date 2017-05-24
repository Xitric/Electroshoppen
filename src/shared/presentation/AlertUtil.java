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

	/**
	 * Constructs a new alert of any type, depending on the arguments given.
	 * The alert is populated by the corresponding String arguments
	 *
	 * @param type		The type of alert
	 * @param title		The title text of the alert dialog
	 * @param header	The header text of the alert dialog
	 * @param content	The content text of the alert dialog
	 * @return	Returns an alert dialog using the arguments given
	 */
	public static Alert newAlertDialog(Alert.AlertType type, String title, String header,String content) {
		Alert alert;
		switch (type) {
			case CONFIRMATION:
				alert = new Alert(Alert.AlertType.CONFIRMATION);
				alert.setTitle(title);
				alert.setHeaderText(header);
				alert.setContentText(content);
				break;
			case ERROR:
				alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle(title);
				alert.setHeaderText(header);
				alert.setContentText(content);
				break;
			case WARNING:
				alert = new Alert(Alert.AlertType.WARNING);
				alert.setTitle(title);
				alert.setHeaderText(header);
				alert.setContentText(content);
				break;
			case INFORMATION:
				alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle(title);
				alert.setHeaderText(header);
				alert.setContentText(content);
				break;
			default:
				alert = new Alert(Alert.AlertType.NONE);
				alert.setTitle(title);
				alert.setHeaderText(header);
				alert.setContentText(content);
				break;
		}
		return alert;
	}
}
