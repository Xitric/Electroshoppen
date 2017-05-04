package pim.presentation;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.FlowPane;
import pim.business.Image;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Kasper
 */
public class ProductController implements Initializable {

	@FXML
	private FlowPane productImagePane;

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		File dir = new File("res");
		for (File file: dir.listFiles()) {
			if (file.getName().equals("cross.png")) {
				continue;
			}
			Image img = new Image(file.getAbsolutePath());
			productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		}
	}

	private void removeImage(RemoveableImage img) {
		//TODO
		productImagePane.getChildren().remove(img);
	}
}
