/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pim.presentation;

import cms.business.CMS;
import cms.presentation.CMSViewController;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import pim.business.PIM;
import webshop.presentation.WebshopController;

import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author Kasper
 * @author Emil
 */
public class PIMController implements Initializable {

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

	}


	/**
	 * Set the business mediator for this controller to use.
	 *
	 * @param pim the mediator for the pim
	 */
	public void setPIM(PIM pim) {
		categoryTabPageController.setPIM(pim);
		attributeTabPageController.setPIM(pim);
		productTabPageController.setPIM(pim);
	}

	/**
	 * Set the business mediator for this controller to use.
	 *
	 * @param cms the mediator for the cms
	 */
	public void setCMS(CMS cms) {
		cmsTabPageController.setCMS(cms);
	}

	@FXML
	private void onAttributeEnter(Event event) {
			attributeTabPageController.onEnter();
		}
	}

	@FXML
	private void onProductEnter(Event event) {
			productTabPageController.onEnter();
		}
	}

	@FXML
	private void onCMSEnter(Event event) {
			cmsTabPageController.onEnter();
		}
	}

	@FXML
	private void onWebshopEnter(Event event) {
			webshopTabPageController.onEnter();
		}
	}
}

