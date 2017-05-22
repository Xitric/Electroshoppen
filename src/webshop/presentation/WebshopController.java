package webshop.presentation;

import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import webshop.business.Webshop;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * @author Emil
 */
public class WebshopController implements Initializable {

	@FXML
	private TitledPane titledPaneLeft;
	@FXML
	private TitledPane titledPaneCenter;
	@FXML
	private Button homeBtn;
	@FXML
	private Button productsBtn;
	@FXML
	private Button articlesBtn;
	@FXML
	private Button guidesBtn;


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

		titledPaneLeft.setText("Home");
		titledPaneCenter.setText("Home");
		//homeBtn.setDisable(true);
	}
	public void onEnter() {
		System.out.println("entered webshop...");
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
	private void buttonOnAction(ActionEvent event) {
		Object btn = event.getSource();
		String leftPane = "";
		String centerPane = "";

		if (btn == homeBtn){
			leftPane = "Home";
			centerPane = "Home";
		} else if (btn == productsBtn){
			leftPane = "Products";
			centerPane = "Products";
		} else if (btn == articlesBtn){
			leftPane = "Articles";
			centerPane = "Articles";
		} else if (btn == guidesBtn){
			leftPane = "Guides";
			centerPane = "Guides";
		}
		titledPaneLeft.setText(leftPane);
		titledPaneCenter.setText(centerPane);
	}

}
