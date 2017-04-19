/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pim.presentation;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.FlowPane;
import pim.business.Image;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author Kasper
 */
public class PIMController implements Initializable {

	@FXML
	private FlowPane productImagePane;

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		Image img = new Image("res/omen-ax005no.jpg");
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
	}

	private void removeImage(RemoveableImage img) {
		//TODO
		productImagePane.getChildren().remove(img);
	}
}
