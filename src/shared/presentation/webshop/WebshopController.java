package shared.presentation.webshop;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import pim.business.Product;
import shared.presentation.AlertUtil;
import webshop.business.Webshop;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for the web shop view.
 *
 * @author Emil
 * @author Kasper
 */
public class WebshopController implements Initializable {

	@FXML
	private TitledPane titledPaneCenter;
	@FXML
	private ListView<Object> listViewAside;
	@FXML
	private WebView webView;

	private ObservableList<Object> asideList;

	/**
	 * The mediator for the business layer.
	 */
	private Webshop webshop;

	/**
	 * Set the business mediator for this controller to use.
	 *
	 * @param web the mediator for the web shop
	 */
	public void setWebshop(Webshop web) {
		this.webshop = web;
	}

	/**
	 * Called to initialize a controller after its root element has been completely processed.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		asideList = FXCollections.observableArrayList();
		listViewAside.setItems(asideList);
		listViewAside.getSelectionModel().selectedItemProperty().addListener(this::listViewAsideChanged);
	}

	public void onEnter() {
		//Attempt to load and show the landing page
		try {
			present(webshop.getLandingPage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void cartOnAction(ActionEvent event) {
		AlertUtil.newAlertDialog(Alert.AlertType.INFORMATION, "Check Out", "Never implemented",
				"This functionality was never implemented, as it was not a focus of the project. The button has been left for illustration purposes only.")
				.showAndWait();
	}

	@FXML
	private void landingOnAction(ActionEvent event) {
		asideList.clear();
		titledPaneCenter.setText("Home");
		try {
			present(webshop.getLandingPage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void productsOnAction(ActionEvent event) {
		try {
			titledPaneCenter.setText("Products");
			asideList.setAll(webshop.getAllProducts());
			listViewAside.getSelectionModel().selectFirst();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void articlesOnAction(ActionEvent event) {
		try {
			titledPaneCenter.setText("Articles");
			setListViewFromMap(webshop.getArticlePages());
			listViewAside.getSelectionModel().selectFirst();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void guidesOnAction(ActionEvent event) {
		try {
			titledPaneCenter.setText("Guides");
			setListViewFromMap(webshop.getGuidePages());
			listViewAside.getSelectionModel().selectFirst();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Makes a list from the map with Page objects and show Page names in the list view.
	 *
	 * @param map The pages from the cms
	 */
	private void setListViewFromMap(Map<Integer, String> map) {
		List<Page> listPage = new ArrayList<>();
		for (Map.Entry<Integer, String> entry : map.entrySet()) {
			Page p = new Page(entry.getKey(), entry.getValue());
			listPage.add(p);
		}

		asideList.setAll(listPage);
	}

	/**
	 * Called when the user's selection in the list view to the left changes.
	 */
	private void listViewAsideChanged(javafx.beans.Observable observable) {
		Object selected = listViewAside.getSelectionModel().getSelectedItem();
		if (selected != null) {
			try {
				if (selected instanceof Page) {
					present(webshop.getPage(((Page) selected).getPageId()));
				} else if (selected instanceof Product) {
					present(webshop.getProductPage(((Product) selected).getID()));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unused")
	public void linkPressed(Object anchor) {
		try {
			//Calling toString() on the anchor element returns the content of the href attribute, which is the id of the
			//page we want to access
			present(webshop.getPage(Integer.parseInt(anchor.toString())));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Present the page described by the specified html in the web shop.
	 *
	 * @param html the html to present
	 */
	private void present(String html) {
		webView.getEngine().loadContent(html);

		//Inject script for dealing with links
		webView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.equals(Worker.State.SUCCEEDED)) {
				injectLinkScript();
			}
		});
	}

	/**
	 * Inject a script into the web view for dealing with links.
	 */
	private void injectLinkScript() {
		JSObject window = (JSObject) webView.getEngine().executeScript("window");
		window.setMember("controller", this);

		webView.getEngine().executeScript("var links = document.getElementsByTagName('a');" +
				"for (i = links.length - 1; i >= 0; i--) {" +
				"links[i].onclick = function(event) {" +
				"   anchorClick(event);" +
				"}" +
				"}" +
				"function anchorClick(event){" +
				"   controller.linkPressed(event.target);" +
				"}");
	}

	/**
	 * Inner class for representing an entry in the page list view with a proper toString() method.
	 */
	private class Page {
		private int pageId;
		private String pageName;

		public Page(int pageId, String pageName) {
			this.pageId = pageId;
			this.pageName = pageName;
		}

		public int getPageId() {
			return pageId;
		}

		public String toString() {
			return pageName;
		}
	}
}
