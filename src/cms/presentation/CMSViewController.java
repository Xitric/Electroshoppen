package cms.presentation;

import cms.business.DynamicPage;
import cms.business.Template;
import cms.business.XMLElement;
import cms.persistence.CMSPersistenceFacade;
import cms.persistence.CMSPersistenceFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;

import java.io.IOException;
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

		CMSPersistenceFacade persistence = CMSPersistenceFactory.createDatabaseMediator();
		try {
			DynamicPage page = persistence.getPage(1);
			Template template = persistence.getTemplateForPage(1);
			XMLElement layout = template.enrichPage(page);

			editor.setContent(layout.toString());
			htmlPreview.setText(layout.getChildrenByTag("body").get(0).toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
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
