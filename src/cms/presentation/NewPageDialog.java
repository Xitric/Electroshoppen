package cms.presentation;

import cms.business.CMS;
import cms.business.Template;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.web.WebView;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Dialog used for creating new pages in the cms.
 *
 * @author Kasper
 */
public class NewPageDialog extends Dialog<NewPageDialog.NewPageInfo> implements Initializable {

	@FXML
	public TextField pageNameField;

	@FXML
	public ComboBox<CMS.PageType> pageTypeSelector;

	@FXML
	public ComboBox<Template> templateSelector;

	@FXML
	public WebView preview;

	private ObservableList<Template> templates;

	/**
	 * The cms facade.
	 */
	private CMS cms;

	/**
	 * Constructs a new dialog for creating new pages in the cms.
	 *
	 * @param cms the cms facade
	 */
	public NewPageDialog(CMS cms) {
		this.cms = cms;
		setTitle("Create a new page");
		setHeaderText("Specify page options");
		getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		//Style the dialog
		getDialogPane().getStylesheets().add(
				getClass().getResource("../../pim/presentation/pimview.css").toExternalForm());

		//Load the content from the fxml file
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("NewPageView.fxml"));
			fxmlLoader.setController(this);
			Parent root = fxmlLoader.load();
			getDialogPane().setContent(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Listen for changes in template selection. When a change occurs, we want to update the web view preview
		templates = FXCollections.observableArrayList();
		templateSelector.setItems(templates);
		templateSelector.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) preview.getEngine().loadContent(newValue.getMarkup());
		});

		//Listen for changes in the page type selection. When a change occurs, we want to refresh the template options
		pageTypeSelector.setItems(FXCollections.observableArrayList(CMS.PageType.values()));
		pageTypeSelector.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			try {
				templates.setAll(cms.getTemplatesForPageType(newValue));

				//Auto select the first template
				templateSelector.getSelectionModel().selectFirst();
			} catch (IOException e) {
				templates.clear();
			}
		});
		//Auto select the first page type
		pageTypeSelector.getSelectionModel().selectFirst();

		//Specify how a result is gathered. Partly taken from the javafx.scene.control.TextInputDialog class
		setResultConverter((dialogButton) -> {
			ButtonBar.ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
			if (data == ButtonBar.ButtonData.OK_DONE) {
				//If something is not specified, return null
				if (pageNameField.getText().isEmpty() ||
						pageTypeSelector.getSelectionModel().getSelectedItem() == null ||
						templateSelector.getSelectionModel().getSelectedItem() == null)
					return null;

				//Otherwise return selections
				return new NewPageInfo(pageNameField.getText(),
						pageTypeSelector.getSelectionModel().getSelectedItem(),
						templateSelector.getSelectionModel().getSelectedItem().getID());
			}

			return null;
		});
	}

	/**
	 * Inner class used to wrap the choices made by the user when creating a new page in the cms.
	 */
	public static class NewPageInfo {

		private String name;
		private CMS.PageType pageType;
		private int templateID;

		private NewPageInfo(String name, CMS.PageType pageType, int templateID) {
			this.name = name;
			this.pageType = pageType;
			this.templateID = templateID;
		}

		/**
		 * Get the name of the page to create.
		 *
		 * @return the name of the page to create
		 */
		public String getName() {
			return name;
		}

		/**
		 * Get the type of page to create.
		 *
		 * @return the type of page to create
		 */
		public CMS.PageType getPageType() {
			return pageType;
		}

		/**
		 * Get the id of the template for the page to create.
		 *
		 * @return the id of the template for the page to create
		 */
		public int getTemplateID() {
			return templateID;
		}
	}
}
