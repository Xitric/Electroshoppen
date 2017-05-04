/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pim.presentation;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import pim.business.PIM;

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

	@FXML
	private CategoryController categoryTabPageController;

	@FXML
	private AttributeController attributeTabPageController;

	@FXML
	private ProductController productTabPageController;

	/**
	 * The mediator for the business layer.
	 */
	private PIM pim;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	/**
	 * Set the business mediator for this controller to use.
	 *
	 * @param pim the mediator for the pim
	 */
	public void setPIM(PIM pim) {
		this.pim = pim;
		categoryTabPageController.setPIM(pim);
		attributeTabPageController.setPIM(pim);
		productTabPageController.setPIM(pim);
	}

	@FXML
	private void onAttributeEnter(Event event) {
		attributeTabPageController.onEnter();
	}

	@FXML
	private void onProductEnter(Event event) {
		productTabPageController.onEnter();
	}
}
