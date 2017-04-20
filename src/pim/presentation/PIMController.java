/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pim.presentation;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author Kasper
 */
public class PIMController implements Initializable {

	@FXML
	private BorderPane categoryTabPage;

	@FXML
	private BorderPane attributeTabPage;

	@FXML
	private BorderPane productTabPage;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
}
