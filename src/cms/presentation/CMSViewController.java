package cms.presentation;

import cms.business.CMS;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Pair;
import org.w3c.dom.html.HTMLElement;

import java.net.URL;
import java.util.Optional;
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
	}

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
	}

	/**
	 * Executes every time the tab is opened to populate the TreeView showing existing pages.
	 */
	public void onEnter() {

	}

	/**
	 * Called when a double click is performed on the editor
	 */
	private void editorDoubleClick() {
		//Get current selection. This should be the one that the user double clicked
		HTMLElement selection = editor.selectedElementProperty().getValue();

		//We only want to select meaningful text elements, so we ensure that the only child (other than inserters) of
		//the selected element is a text node. An element containing text should not have more than this, single child
		//(and possibly the inserters)
		//TODO: Move to business layer, presentation is too cluttered to work with!
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
	private void newPageOnAction(ActionEvent event) {
		Dialog<Pair<CMS.PageType, Integer>> newPageDialog = new Dialog<>();
		newPageDialog.setTitle("Create a new page");
		newPageDialog.setHeaderText("Specify page options");

		//Style the dialog
		DialogPane dialogPane = newPageDialog.getDialogPane();
		dialogPane.getStylesheets().add(
				getClass().getResource("../../pim/presentation/pimview.css").toExternalForm());

		//TODO: Create new fxml and controller for this
		HBox content = new HBox(16);
		ComboBox<CMS.PageType> pageTypes = new ComboBox<>(FXCollections.observableArrayList(CMS.PageType.values()));
		TextField templateIDField = new TextField();
		templateIDField.setPromptText("Template id");
		content.getChildren().addAll(pageTypes, templateIDField);
		newPageDialog.getDialogPane().setContent(content);

		ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
		newPageDialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

		//Specify how a result is gathered from the dialog
		newPageDialog.setResultConverter(button -> {
			if (button == confirmButtonType) {
				try {
					return new Pair<>(pageTypes.getValue(), Integer.parseInt(templateIDField.getText()));
				} catch (NumberFormatException e) {
					return null;
				}
			}

			return null;
		});

		Optional<Pair<CMS.PageType, Integer>> result = newPageDialog.showAndWait();
		if (result.isPresent()) {
			Pair<CMS.PageType, Integer> pair = result.get();
			String html = cms.createNewPage(pair.getKey(), pair.getValue());
			present(html);
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

	/**
	 * Present the specified html in this editor.
	 *
	 * @param html the html to present
	 */
	private void present(String html) {
		editor.setContent(html);

		//Remove the image encoding from the html preview
		//Made with the help of https://www.freeformatter.com/java-regex-tester.html
		String strippedHTML = html.replaceAll("(<img src=\"data:base64,)[\\w+/\n\r=]+[^\"]", "<img src=\"data:base64,...");
		htmlPreview.setText(strippedHTML);
	}

	/**
	 * Fill in the tree view on the left with the available pages in the CMS for easy access.
	 */
	private void populateTreeView() {

	}
}
