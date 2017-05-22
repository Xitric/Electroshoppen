package webshop.presentation;

import cms.business.CMS;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TitledPane;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
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
	private WebView webView;
	@FXML
	private Button homeBtn;
	@FXML
	private Button productsBtn;
	@FXML
	private Button articlesBtn;
	@FXML
	private Button guidesBtn;

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
		onEnter();

		//Attempt to load and show the landing page
		try {
			present(cms.getLandingPage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Called to initialize a controller after its root element has been completely processed.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

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
	private void buttonOnAction(ActionEvent event) {
		Object btn = event.getSource();
		String centerPane = "";

		try {
			if (btn == homeBtn) {
				centerPane = "Home";
				present(cms.getLandingPage());
			} else if (btn == productsBtn) {
				centerPane = "Products";
			} else if (btn == articlesBtn) {
				centerPane = "Articles";
			} else if (btn == guidesBtn) {
				centerPane = "Guides";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		titledPaneCenter.setText(centerPane);
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
}
