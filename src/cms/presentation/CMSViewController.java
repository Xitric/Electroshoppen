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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import org.w3c.dom.html.HTMLElement;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * @author Kasper
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
		try {
			populateTreeView();
		} catch (IOException e) {
			e.printStackTrace();
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
			if (! text.isEmpty()) {
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

		String currentText = cms.getElementText(selection.getId());
		if (currentText != null) {
			present(cms.editElementText(selection.getId(), showTextEditDialog(currentText)));
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
	private void openPageOnAction(ActionEvent event) {
		//TODO: More user friendly approach
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Open Page");
		dialog.setHeaderText("Choose a page to open");
		dialog.setContentText("Enter page id:");

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
			try {
				present(cms.editPage(Integer.parseInt(result.get())));
			} catch (NumberFormatException e) {
				//TODO
			} catch (IOException e) {
				//TODO
				e.printStackTrace();
			}
		}
	}

	@FXML
	private void openPage(){
		if(pageListView.getSelectionModel().getSelectedItem() == null){
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
	private void browsePageOnAction(ActionEvent event) {
		//TODO: Select from page list
		pageIdField.setText("1");
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


	private class Page{
		private int pageId;
		private String pageName;

		public Page(int pageId, String pageName){
			this.pageId = pageId;
			this.pageName = pageName;
		}

		public int getPageId(){
			return pageId;
		}

		public String toString(){
			return pageName;
		}
	}
}
