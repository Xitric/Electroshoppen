package cms.presentation;

import cms.business.CMS;
import cms.business.DocumentMarker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import org.w3c.dom.html.HTMLElement;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

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
	private TreeView<Page> pageTreeView;

	@FXML
	private StackPane editorPane;

	@FXML
	private ListView<Page> pageListView;

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
		onEnter();

		//TODO: Temp
		try {
			present(cms.editPage(1));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		editor = new SelectableWebView(this::editorInsert);

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
				populateTreeView();
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
				present(cms.insertText(marker, text));
			}
		} else if (option == insertImageToggle) {
			//TODO: Move image loading somewhere else
			try {
				BufferedImage img = ImageIO.read(new File(insertImageUrlField.getText()));
				present(cms.insertImage(marker, img));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (option == insertPageLinkToggle) {
			int id = Integer.parseInt(pageIdField.getText()); //Should be safe as we control the contents of this field
			present(cms.createLink(marker, id));
		}
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
				present(cms.editElementText(selection.getId(), showTextEditDialog(currentText)));
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
		//Show dialog to user
		NewPageDialog dialog = new NewPageDialog(cms);
		Optional<NewPageDialog.NewPageInfo> result = dialog.showAndWait();

		//If the user made proper selections, create a new page and show it
		if (result.isPresent()) {
			NewPageDialog.NewPageInfo info = result.get();
			present(cms.createNewPage(info.getName(), info.getPageType(), info.getTemplateID()));
		}
	}

	@FXML
	private void openPage() {
		if (pageListView.getSelectionModel().getSelectedItem() == null) {
			return;
		}
		Page selectedPage = pageListView.getSelectionModel().getSelectedItem();
		try {
			present(cms.editPage(selectedPage.pageId));
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
		//TODO: Duplication, we should combine the gui packages and reuse code
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.bmp", "*.gif", "*.png", "*.jpeg", "*.wbmp"));
		File selectedFile = fileChooser.showOpenDialog(insertImageUrlField.getScene().getWindow());
		if (selectedFile != null) {
			insertImageUrlField.setText(selectedFile.getPath());
		}
	}

	@FXML
	private void browseProductOnAction(ActionEvent event) {
		//TODO: Select from product list
		productIdField.setText("30");
	}

	@FXML
	private void browsePageDialog() throws IOException {
		Dialog<Integer> dialog = new Dialog<>();
		dialog.setTitle("Insert link");
		dialog.setHeaderText("Insert the selected link");

		DialogPane dialogPane = dialog.getDialogPane();
		dialogPane.getStylesheets().add(getClass().getResource("../../pim/presentation/pimview.css").toExternalForm());

		ListView<Page> pageView = new ListView<>();
		Map<Integer, String> pagesInfo = cms.getPageInfo();
		ObservableList<Page> pages = FXCollections.observableArrayList();
		for (Map.Entry<Integer, String> entry : pagesInfo.entrySet()) {
			pages.add(new Page(entry.getKey(), entry.getValue()));
		}
		pageView.setItems(pages);
		dialog.getDialogPane().setContent(pageView);

		ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

		//Specify how a result is gathered from the dialog
		dialog.setResultConverter(button -> {
			if (button == confirmButtonType) {
				return pageView.getSelectionModel().getSelectedItem().getPageId();
			}
			return null;
		});
		Optional<Integer> result = dialog.showAndWait();
		result.ifPresent(integer -> pageIdField.setText(Integer.toString(integer)));
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
	 * @param html the html to present
	 */
	private void present(String html) {
		editor.setContent(html, true);

		//Remove the image encoding from the html preview
		//Made with the help of https://www.freeformatter.com/java-regex-tester.html
		String strippedHTML = html.replaceAll("(<img src=\"data:image/png;base64,)[\\w+/\n\r=]+[^\"]", "<img src=\"data:image/png;base64,...");
		htmlPreview.setText(strippedHTML);
	}

	/**
	 * Fill in the tree view on the left with the available pages in the CMS for easy access.
	 */
	private void populateTreeView() throws IOException {
		ObservableList<Page> pages = FXCollections.observableArrayList();
		Map<Integer, String> pageInformation = cms.getPageInfo();
		for (Map.Entry<Integer, String> entry : pageInformation.entrySet()) {
			pages.add(new Page(entry.getKey(), entry.getValue()));
		}
		pageListView.setItems(pages);
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
			return "[" + pageId + "] " + pageName;
		}
	}
}
