package cms.presentation;

import cms.business.DynamicPage;
import cms.business.Template;
import cms.business.XMLElement;
import cms.persistence.CMSPersistenceFacade;
import cms.persistence.CMSPersistenceFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLElement;

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

	@FXML
	private MenuItem newPage;

	private SelectableWebView editor;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		editor = new SelectableWebView();

		//Add double click listener to editor
		editor.setOnMouseClicked(mouseEvent -> {
			if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() >= 2) {
				editorDoubleClick();
			}
		});

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

	/**
	 * Executes every time the tab is opened to populate the TreeView
	 * showing existing pages.
	 */
	public void onEnter() {

    }

	/**
	 * Called when a double click is performed on the editor
	 */
	private void editorDoubleClick() {
		//Get current selection. This should be the one that the user double clicked
		HTMLElement selection = editor.selectedElementProperty().getValue();

		//We oly want to select text elements, so we ensure that the only child of the selected element is a text node.
		//An element containing text should not have more than this, single child
		NodeList children = selection.getChildNodes();
		if (children.getLength() == 1 && children.item(0).getNodeType() == Node.TEXT_NODE) {
			//TODO: Call to CMS
			children.item(0).setNodeValue(showTextEditDialog(children.item(0).getNodeValue()));
		}
	}

	/**
	 * Show a dialog for editing a piece of text and get the user result. If the user cancelled, the returned text is
	 * the same as the text parameter.
	 *
	 * @param text the text to edit
	 * @return the result of the user's edit
	 */
	private String showTextEditDialog(final String text) {
		//Create dialog for changing text
		Dialog<String> dialog = new Dialog<>();
		dialog.setTitle("Change text");
		dialog.setHeaderText("Change the selected text");

		//Style the dialog
		DialogPane dialogPane = dialog.getDialogPane();
		dialogPane.getStylesheets().add(
				getClass().getResource("../../pim/presentation/pimview.css").toExternalForm());

		//Add text area to dialog
		TextArea textEdit = new TextArea(text);
		textEdit.setWrapText(true);
		dialog.getDialogPane().setContent(textEdit);

		ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

		//Specify how a result is gathered from the dialog
		dialog.setResultConverter(button -> {
			if (button == confirmButtonType) {
				return textEdit.getText();
			}

			return null;
		});

		//Get and return result
		return dialog.showAndWait().orElse(text);
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

	// needs to build the pages to show them in the TreeView?
    private void populateTreeView() {

    }

}
