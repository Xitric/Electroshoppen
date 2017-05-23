package shared.presentation.cms;

import cms.business.CMS;
import cms.business.DocumentMarker;
import dam.business.DAM;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import org.w3c.dom.html.HTMLElement;
import pim.business.Product;
import shared.Image;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Controller for the CMS view.
 *
 * @author Kasper
 * @author Niels
 */
public class CMSViewController implements Initializable {

	@FXML
	public RadioButton insertTextToggle;

	@FXML
	public RadioButton insertImageToggle;

	@FXML
	public ToggleGroup insertOptionGroup;

	@FXML
	public TextField insertTextField;

	@FXML
	public TextField insertImageUrlField;

	@FXML
	public TextField productIdField;

	@FXML
	public RadioButton nameLinkToggle;

	@FXML
	public RadioButton priceLinkToggle;

	@FXML
	public RadioButton imageLinkToggle;

	@FXML
	public RadioButton descriptionLinkToggle;

	@FXML
	public RadioButton tagsLinkToggle;

	@FXML
	public RadioButton insertPageLinkToggle;

	@FXML
	public TextField pageIdField;

	@FXML
	private TextArea htmlPreview;

	@FXML
	private StackPane editorPane;

	@FXML
	private ListView<Page> pageListView;

	private SelectableWebView editor;

	/** The mediator for the business layer. */
	private CMS cms;

	/** The mediator for the dam. */
	private DAM dam;

	/**
	 * Set the business mediator for this controller to use.
	 *
	 * @param cms the mediator for the cms
	 */
	public void setCMS(CMS cms) {
		this.cms = cms;
		onEnter();

		//TODO: Temp
		try {
			present(cms.editPage(1), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set the dam mediator for this controller to use.
	 *
	 * @param dam the mediator for the dam
	 */
	public void setDAM(DAM dam) {
		this.dam = dam;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		editor = new SelectableWebView(this::editorInsert, this::editorDelete);

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
		if (cms != null) {
			try {
				populatePageListView();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Called when the user presses one of the insert buttons in the editor.
	 *
	 * @param marker the document marker that describes the user's selection
	 */
	private void editorInsert(DocumentMarker marker) {
		Toggle option = insertOptionGroup.getSelectedToggle();

		if (option == insertTextToggle) {
			String text = insertTextField.getText();
			if (!text.isEmpty()) {
				present(cms.insertText(marker, text), false);
			}
		} else if (option == insertImageToggle) {
			try {
				present(cms.insertImage(marker, new Image(insertImageUrlField.getText())), false);
			} catch (IllegalArgumentException e) {
				//invalid image
			}
		} else if (option == insertPageLinkToggle) {
			int id = Integer.parseInt(pageIdField.getText()); //Should be safe as we control the contents of this field
			present(cms.createLink(marker, id), false);
		} else if (option == nameLinkToggle){
			present(cms.createReference(marker, Integer.parseInt(productIdField.getText()), CMS.ReferenceType.NAME), false);
		} else if (option == priceLinkToggle){
			present(cms.createReference(marker, Integer.parseInt(productIdField.getText()), CMS.ReferenceType.PRICE), false);
		} else if (option == imageLinkToggle){
			present(cms.createReference(marker, Integer.parseInt(productIdField.getText()), CMS.ReferenceType.IMAGE), false);
		} else if (option == descriptionLinkToggle){
			present(cms.createReference(marker, Integer.parseInt(productIdField.getText()), CMS.ReferenceType.DESCRIPTION), false);
		} else if (option == tagsLinkToggle){
			present(cms.createReference(marker, Integer.parseInt(productIdField.getText()), CMS.ReferenceType.TAGS), false);
		}
	}

	/**
	 * Called when the user presses the delete button in the editor.
	 *
	 * @param marker the document marker that describes the user's selection
	 */
	private void editorDelete(DocumentMarker marker) {
		present(cms.removeElement(marker), false);
	}

	/**
	 * Called when a double click is performed on the editor
	 */
	private void editorDoubleClick() {
		//Get current selection. This should be the one that the user double clicked
		HTMLElement selection = editor.selectedElementProperty().getValue();
		if (selection != null) {
			String currentText = cms.getElementText(selection.getId());
			if (currentText != null) {
				String newText = showTextEditDialog(currentText);
				if (!newText.equals(currentText)) {
					present(cms.editElementText(selection.getId(), newText), false);
				}
			}
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
				getClass().getResource("../electroshop.css").toExternalForm());

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
		//Show dialog to user
		NewPageDialog dialog = new NewPageDialog(cms);
		Optional<NewPageDialog.NewPageInfo> result = dialog.showAndWait();

		//If the user made proper selections, create a new page and show it
		if (result.isPresent()) {
			NewPageDialog.NewPageInfo info = result.get();
			present(cms.createNewPage(info.getName(), info.getPageType(), info.getTemplateID()), true);
		}
	}

	@FXML
	private void openPage() {
		if (pageListView.getSelectionModel().getSelectedItem() == null) {
			return;
		}
		Page selectedPage = pageListView.getSelectionModel().getSelectedItem();
		try {
			present(cms.editPage(selectedPage.pageId), true);
		} catch (NumberFormatException e) {
			//TODO
		} catch (IOException e) {
			//TODO
			e.printStackTrace();
		}

	}

	@FXML
	private void textFieldOnAction(ActionEvent event) {
		insertTextToggle.setSelected(true);
	}

	@FXML
	private void imageFieldOnAction(ActionEvent event) {
		insertImageToggle.setSelected(true);
	}

	@FXML
	private void browseOnAction(ActionEvent event) {
		Image img = dam.getImage();
		if (img == null || img.getURL() == null) {
			insertImageUrlField.clear();
		} else {
			insertImageUrlField.setText(img.getURL());
			insertImageToggle.setSelected(true);
		}
	}

	@FXML
	private void browseProductOnAction(ActionEvent event) throws IOException {
		//TODO: Select from product list
		ListViewDialog<Product> productDialog = new ListViewDialog<>(cms.getAllProducts());
		productDialog.setTitle("Select product");
		productDialog.setHeaderText("Select product to insert");
		Optional<Product> result = productDialog.showAndWait();
		result.ifPresent(product -> {
			productIdField.setText(Integer.toString(product.getID()));
		});
	}

	@FXML
	private void browsePageDialog() throws IOException {
		//Construct list of page objects to choose between
		Map<Integer, String> pagesInfo = cms.getPageInfo();
		List<Page> pages = new ArrayList<>();
		for (Map.Entry<Integer, String> entry : pagesInfo.entrySet()) {
			pages.add(new Page(entry.getKey(), entry.getValue()));
		}

		//Show dialog
		Dialog<Page> dialog = new ListViewDialog<>(pages);
		dialog.setTitle("Insert link");
		dialog.setHeaderText("Insert the selected link");

		Optional<Page> result = dialog.showAndWait();
		result.ifPresent(page -> {
			pageIdField.setText(Integer.toString(page.getPageId()));
			insertPageLinkToggle.setSelected(true);
		});
	}

	@FXML
	private void saveButtonOnAction(ActionEvent event) {
		try {
			cms.savePage();
		} catch (IOException e) {
			//TODO
			e.printStackTrace();
		}
	}

	/**
	 * Present the specified html in this editor.
	 *
	 * @param html    the html to present
	 * @param newPage true if presenting a new web page, false otherwise
	 */
	private void present(String html, boolean newPage) {
		editor.setContent(html, !newPage);

		//Remove the image encoding from the html preview
		//Made with the help of https://www.freeformatter.com/java-regex-tester.html
		String strippedHTML = html.replaceAll("(<img src=\"data:image/png;base64,)[\\w+/\n\r=]+[^\"]", "<img src=\"data:image/png;base64,...");
		htmlPreview.setText(strippedHTML);
	}

	/**
	 * Fill in the tree view on the left with the available pages in the CMS for easy access.
	 */
	private void populatePageListView() throws IOException {
		ObservableList<Page> pages = FXCollections.observableArrayList();
		Map<Integer, String> pageInformation = cms.getPageInfo();
		for (Map.Entry<Integer, String> entry : pageInformation.entrySet()) {
			pages.add(new Page(entry.getKey(), entry.getValue()));
		}
		pageListView.setItems(pages);
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
			return "[" + pageId + "] " + pageName;
		}
	}
}
