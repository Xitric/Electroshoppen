/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared.presentation;

import cms.business.CMS;
import shared.presentation.cms.CMSViewController;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import pim.business.PIM;
import shared.presentation.pim.AttributeController;
import shared.presentation.pim.CategoryController;
import shared.presentation.pim.ProductController;
import shared.presentation.webshop.WebshopController;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML base controller class for the entire application.
 *
 * @author Kasper
 * @author Emil
 */
public class ElectroshopController implements Initializable {

	@FXML
	private TabPane tabPane;
	@FXML
	private BorderPane categoryTabPage;
	@FXML
	private BorderPane attributeTabPage;
	@FXML
	private BorderPane productTabPage;
	@FXML
	private BorderPane cmsTabPage;
	@FXML
	private BorderPane webshopTabPage;
	@FXML
	private Tab categoryTab;
	@FXML
	private Tab attributeTab;
	@FXML
	private Tab productTab;
	@FXML
	private Tab cmsTab;
	@FXML
	private Tab webshopTab;
	@FXML
	private CategoryController categoryTabPageController;
	@FXML
	private AttributeController attributeTabPageController;
	@FXML
	private ProductController productTabPageController;
	@FXML
	private CMSViewController cmsTabPageController;
	@FXML
	private WebshopController webshopTabPageController;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		tabPane.getSelectionModel().select(3);
	}

	/**
	 * Set the PIM facade for this controller to use.
	 *
	 * @param pim the mediator for the pim
	 */
	public void setPIM(PIM pim) {
		categoryTabPageController.setPIM(pim);
		attributeTabPageController.setPIM(pim);
		productTabPageController.setPIM(pim);
	}

	/**
	 * Set the CMS facade for this controller to use.
	 *
	 * @param cms the mediator for the cms
	 */
	public void setCMS(CMS cms) {
		cmsTabPageController.setCMS(cms);
		webshopTabPageController.setCMS(cms);
	}

	@FXML
	private void onCategoryEnter(Event event) {
		if (categoryTab.isSelected()) {
			categoryTabPageController.onEnter();
		}
	}

	@FXML
	private void onAttributeEnter(Event event) {
		if (attributeTab.isSelected()) {
			attributeTabPageController.onEnter();
		}
	}

	@FXML
	private void onProductEnter(Event event) {
		if (productTab.isSelected()) {
			productTabPageController.onEnter();
		}
	}

	@FXML
	private void onCMSEnter(Event event) {
		if (cmsTab.isSelected()) {
			cmsTabPageController.onEnter();
		}
	}

	@FXML
	private void onWebshopEnter(Event event) {
		if (webshopTab.isSelected()) {
			webshopTabPageController.onEnter();
		}
	}
}

