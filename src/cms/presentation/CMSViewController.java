package cms.presentation;

import cms.business.XMLParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Kasper
 */
public class CMSViewController implements Initializable {

	@FXML
	private TextArea htmlPreview;

	@FXML
	private TreeView</*TODO*/?> existingPageTreeView;

	@FXML
	private StackPane editorPane;

	private SelectableWebView editor;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		editor = new SelectableWebView();
		editorPane.getChildren().add(editor);

//		String html = "<html><head/><body>" +
//				"<div style=\"background-color:green;\">" +
//				"<h1>Hello, world!</h1>" +
//				"</div>" +
//				"<div style=\"background-color:cyan;\">" +
//				"<p>Another paragraph</p>" +
//				"<p>And another line</p>" +
//				"</div>" +
//				"</body></html>";
		String html = "<html><head><title>Example</title>\n" +
				"<style>\n" +
				".wrapper {\n" +
				"  display: flex;\n" +
				"}\n" +
				".wrapper > div {\n" +
				"  font-size: 4vh;\n" +
				"  color: black;\n" +
				"  background: white;\n" +
				"  margin: .3em;\n" +
				"  padding: .3em;\n" +
				"  outline:2px #125688 solid;\n" +
				"  flex: 1;\n" +
				"}\n" +
				"</style>\n" +
				"</head><body class=\"nonselectable\">" +
				"<div class=\"wrapper nonselectable\">\n" +
				"  <div>Title</div>\n" +
				"</div>\n" +
				"<div class=\"wrapper nonselectable\">\n" +
				"  <div>1 Column</div>\n" +
				"</div>\n" +
				"<div class=\"wrapper nonselectable\">\n" +
				"  <div>2 Columns</div>\n" +
				"  <div>2 Columns</div>\n" +
				"</div>\n" +
				"<div class=\"wrapper nonselectable\">\n" +
				"  <div>3 Columns</div>\n" +
				"  <div>3 Columns</div>\n" +
				"  <div>3 Columns</div>\n" +
				"</div>\n" +
				"<div class=\"wrapper nonselectable\">\n" +
				"  <div>Footer</div>\n" +
				"</div></body></html>";
		editor.setContent(html);
		htmlPreview.setText(new XMLParser().parse(html).getChildrenByTag("body").get(0).toString());
	}

	@FXML
	private void insertTextAfterOnAction(ActionEvent event) {

	}

	@FXML
	private void insertTextBeforeOnAction(ActionEvent event) {

	}

	@FXML
	private void insertImageAfterOnAction(ActionEvent event) {

	}

	@FXML
	private void insertImageBeforeOnAction(ActionEvent event) {

	}

	@FXML
	private void insertHTMLAfterOnAction(ActionEvent event) {

	}

	@FXML
	private void insertHTMLBeforeOnAction(ActionEvent event) {

	}

	@FXML
	private void removeSelectionOnAction(ActionEvent event) {

	}

	@FXML
	private void browseOnAction(ActionEvent event) {

	}
}
