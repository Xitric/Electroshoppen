package cms.presentation;

import cms.business.XMLElement;
import cms.business.XMLParser;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.w3c.dom.html.HTMLElement;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Kasper
 */
public class TestCMS extends Application implements Initializable {

	@FXML
	public Label selectLabel;

	@FXML
	public Label rememberedLabel;

	@FXML
	public TextField textInput;

	@FXML
	private VBox content;

	private SelectableWebView swv;

	private HTMLElement remembered;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		swv = new SelectableWebView();
		content.getChildren().setAll(swv);
		//		swv.setContent("<html><head></head><body>" +
		//				"<div>" +
		//				"<p>Hello, world!</p>" +
		//				"<p>Foo</p>" +
		//				"</div>" +
		//				"<div>" +
		//				"<p>Bar</p>" +
		//				"</div>" +
		//				"</html>");
//		page = new Article("<html id=\"h1\"><head></head>" +
//				"<body id=\"b1\">" +
//				"<form action=\"/action_page.php\" id=\"f1\">" +
//				"First name: <input id=\"i1\" type=\"text\" name=\"fname\"/><br/>" +
//				"Last name: <input id=\"i2\" type=\"text\" name=\"lname\"/><br/>" +
//				"<input id=\"i3\" type=\"submit\" value=\"Submit as normal\"/>" +
//				"<input id=\"i4\" type=\"submit\" formtarget=\"_blank\" value=\"Submit to a new window/tab\"/>" +
//				"</form>" +
//				"<p id=\"p1\"><strong id=\"s1\">Note:</strong> The formtarget attribute of the input tag is not supported in Internet Explorer 9 and earlier versions.</p>" +
//				"</body>" +
//				"</html>");

		XMLElement root = new XMLParser().parse("<html>" +
				"<head>" +
				"<title>Skrammelshop varer</title>" +
				"</head>" +
				"<body>" +
				"<label>Favorite" +
				"Town:</label><br />" +
				"<select title=\"Please choose your favorite town\" name=\"ddlTown\" " +
				"id=\"ddlTown\">" +
				"<option value=\"\">Please Select ... </option>" +
				"<option value=\"Swindon\">Swindon</option>" +
				"<option value=\"London\">London</option>" +
				"<option value=\"Burkino Faso\">Burkino Faso</option>" +
				"</select>" +
				"</body>" +
				"</html>");
		swv.setContent(root.toString());
		System.out.println(root);

		//		swv.setContent("<html><head></head>\n" +
		//				"<body id=\"b1\">\n" +
		//				"\n" +
		//				"<form action=\"/action_page.php\" id=\"f1\">\n" +
		//				"First name: <input id=\"i1\" type=\"text\" name=\"fname\"><br>\n" +
		//				"Last name: <input id=\"i2\" type=\"text\" name=\"lname\"><br>\n" +
		//				"<input id=\"i3\" type=\"submit\" value=\"Submit as normal\">\n" +
		//				"<input id=\"i4\" type=\"submit\" formtarget=\"_blank\" value=\"Submit to a new window/tab\">\n" +
		//				"</form>\n" +
		//				"\n" +
		//				"<p id=\"p1\"><strong id=\"s1\">Note:</strong> The formtarget attribute of the input tag is not supported in Internet Explorer 9 and earlier versions.</p>\n" +
		//				"\n" +
		//				"</body>\n" +
		//				"</html>\n");
		swv.selectedElementProperty().addListener((observable, oldValue, newValue) -> selectLabel.setText(newValue.toString()));
	}

	//	public void handleJSEvent(Object a) {
	//		if (current != null) {
	//			current.removeAttribute("style");
	//		}
	//
	//		System.out.println(a.getClass().getName());
	//		System.out.println(a);
	//
	//		HTMLElementImpl element = (HTMLElementImpl) a;
	//		element.setAttribute("style", "background-color:blue;");
	//
	//		current = element;
	//		outLabel.setText(a.getClass().getName() + ": " + a);
	//
	//		Text text = (Text) a;
	//		System.out.println(text.getParentNode().getParentNode());
	//		//		text.setTextContent("Hello, world!");
	//
	//		//		HTMLParagraphElementImpl newText = (HTMLParagraphElementImpl) webview.getEngine().getDocument().createElement("p");
	//		//		System.out.println(o.getClass().getName());
	//		//		newText.setTextContent("Hello, world!");
	//		//		text.getParentNode().insertBefore(newText, text);
	//
	//		HTMLImageElementImpl img = (HTMLImageElementImpl) webview.getEngine().getDocument().createElement("img");
	//		img.setSrc(String.valueOf(new File("C:\\Users\\Kasper\\Documents\\NetBeansProjects\\SDU\\Semesterprojekter\\Projekt 2 - Electroshoppen\\Electroshoppen\\src\\cms\\presentation\\GitHubSmall.png").toURI()));
	//		img.setAlt("Some Image");
	//		text.getParentNode().getParentNode().appendChild(img);
	//
	//		printDocument(webview.getEngine().getDocument());
	//	}


	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("TestCMS.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Next Gen CMS");
		stage.show();
	}

	@FXML
	public void insertTextOnAction(ActionEvent event) {
		HTMLElement e = swv.selectedElementProperty().getValue();

//		if (!textInput.getText().isEmpty() && e != null) {
//			page.insertText(new DocumentMarker(e, ""), textInput.getText());
//			swv.setContent(page.toString());
//		}

		//		HTMLElement e = swv.selectedElementProperty().getValue();
		//
		//		if (! textInput.getText().isEmpty() && e != null) {
		//			HTMLParagraphElementImpl newText = (HTMLParagraphElementImpl) e.getOwnerDocument().createElement("p");
		//			newText.setTextContent(textInput.getText());
		//			e.getParentNode().insertBefore(newText, e);
		//		}
	}

	@FXML
	public void rememberOnAction(ActionEvent event) {
		remembered = swv.selectedElementProperty().getValue();
		rememberedLabel.setText(remembered.toString());
	}

	@FXML
	public void forceSelectionOnAction(ActionEvent event) {
		if (remembered != null) {
			swv.select(remembered);
		}
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
