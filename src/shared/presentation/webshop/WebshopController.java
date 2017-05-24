package shared.presentation.webshop;

import cms.business.CMS;
import javafx.beans.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TitledPane;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.io.IOException;
import java.net.URL;
import java.util.*;

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
	private CMS cms;


	/**
	 * Set the business mediator for this controller to use.
	 *
	 * @param cms the mediator for the cms
	 */
	public void setCMS(CMS cms) {
		this.cms = cms;

		//Attempt to load and show the landing page
		try {
			setListViewFromMap(cms.getLandingPages());
			//	present(cms.getLandingPage());
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	private void landingOnAction(ActionEvent event) {
		try {
			setListViewFromMap(cms.getLandingPages());
			titledPaneCenter.setText("Home");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void productsOnAction(ActionEvent event) {
		try {
			setListViewFromMap(cms.getProductPages());
			titledPaneCenter.setText("Products");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void articlesOnAction(ActionEvent event) {
		try {
			setListViewFromMap(cms.getArticlePages());
			titledPaneCenter.setText("Articles");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void guidesOnAction(ActionEvent event) {
		try {
			setListViewFromMap(cms.getGuidePages());
			titledPaneCenter.setText("Guides");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setListViewFromMap(Map<Integer, String> map) throws IOException {
		List<Page> listPage = new ArrayList<>();
		for (Map.Entry<Integer, String> entry : map.entrySet()) {
			Page p = new Page(entry.getKey(), entry.getValue());
			listPage.add(p);
		}
		asideList.setAll(listPage);
		present(cms.getPage(map.keySet().iterator().next()));
	}

	private void listViewAsideChanged(javafx.beans.Observable observable) {
		Page selected = (Page) listViewAside.getSelectionModel().getSelectedItem();
		if (selected != null) {
			try {
				present(cms.getPage(selected.getPageId()));
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
			present(cms.getPage(Integer.parseInt(anchor.toString())));
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
