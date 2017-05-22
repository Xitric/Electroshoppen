package webshop.presentation;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextInputDialog;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * @author Emil
 */
public class WebshopController implements Initializable {

	/**
	 * Called to initialize a controller after its root element has been
	 * completely processed.
	 *
	 * @param location  The location used to resolve relative paths for the root object, or
	 *                  <tt>null</tt> if the location is not known.
	 * @param resources The resources used to localize the root object, or <tt>null</tt> if
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	@FXML
	private void cartOnAction(ActionEvent event) {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Check out");
		dialog.setHeaderText("Enter your ...");
		dialog.setContentText("...:");
		Optional<String> result = dialog.showAndWait();
		result.ifPresent(name -> {
		});
	}

	@FXML
	private void homeButtonOnAction(ActionEvent event) {
	}

	@FXML
	private void productsButtonOnAction(ActionEvent event) {
	}

	@FXML
	private void articlesButtonOnAction(ActionEvent event) {
	}

	@FXML
	private void guidesButtonOnAction(ActionEvent event) {
	}
}
