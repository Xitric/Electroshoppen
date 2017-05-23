/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package electroshoppen;

import cms.business.CMSFacade;
import dam.business.DAM;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pim.business.PIMFacade;
import shared.presentation.ElectroshopController;

/**
 * Main class for the application.
 *
 * @author Kasper
 */
public class Electroshoppen extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		//Load the main fxml file
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/shared/presentation/ElectroshopView.fxml"));
		Parent root = loader.load();

		//Load up the mediators for the controllers
		ElectroshopController controller = loader.getController();
		controller.setPIM(PIMFacade.createPIM());
		controller.setCMS(CMSFacade.createCMS());
		controller.setDAM(new DAM((stage.getOwner()))); //Don't mind this, the DAM is a dummy class

		//Set up the scene and show the gui
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Next Gen PIM");
		stage.show();
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
