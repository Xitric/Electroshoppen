/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package electroshoppen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pim.business.PIMFacade;
import pim.presentation.PIMController;

/**
 * Main class for application.
 *
 * @author Kasper
 */
public class Electroshoppen extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/pim/presentation/PIMView.fxml"));
		Parent root = loader.load();
		((PIMController) loader.getController()).setPIM(PIMFacade.createPIM());

		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
