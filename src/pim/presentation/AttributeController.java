package pim.presentation;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import pim.business.Attribute;
import pim.business.PIM;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * @author Kasper
 */
public class AttributeController implements Initializable {

	@FXML
	private ListView<Attribute> attributeListView;

	@FXML
	private Label attributeIDLabel;

	@FXML
	private TextField attributeNameField;

	@FXML
	private TextField attributeDefaultValueField;

	@FXML
	private Label attributeDefaultTypeLabel;

	@FXML
	private ListView<Object> attributeLegalValuesListView;

	private ObservableList<Attribute> attributeList;

	private ObservableList<Object> attributeLegalValuesList;

	private Alert confirmationDialog;

	/**
	 * The mediator for the business layer.
	 */
	private PIM pim;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		attributeList = FXCollections.observableArrayList();
		attributeListView.setItems(attributeList);
		attributeListView.getSelectionModel().selectedItemProperty().addListener(this::listViewSelectionChanged);

		attributeLegalValuesList = FXCollections.observableArrayList();
		attributeLegalValuesListView.setItems(attributeLegalValuesList);

		confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
		confirmationDialog.setTitle("Delete attribute");
		confirmationDialog.setHeaderText("Confirm deletion");
		confirmationDialog.setContentText("Are you sure that you wish to delete this attribute?");
	}

	/**
	 * Set the business mediator for this controller to use.
	 *
	 * @param pim the mediator for the pim
	 */
	public void setPIM(PIM pim) {
		this.pim = pim;
	}

	/**
	 * Call this when the view for this controller is entered in the GUI.
	 */
	public void onEnter() {
		List<Attribute> attributes = pim.getAttributes();
		Collections.sort(attributes);
		attributeList.setAll(pim.getAttributes());
	}

	@FXML
	private void addButtonOnAction(ActionEvent event) {
		//Create dialog for specifying attribute information
		Dialog<Attribute> dialog = new Dialog<>();
		dialog.setTitle("Create attribute");
		dialog.setHeaderText("Specify attribute information");

		FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateAttributeDialog.fxml"));

		try {
			dialog.getDialogPane().setContent(loader.load());
		} catch (IOException e) {
			e.printStackTrace();
		}

		ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

		//Specify how a result is gathered from the dialog
		dialog.setResultConverter(button -> {
			if (button == saveButtonType) {
				//TODO: Reopen dialog if user specified insufficient information
				return ((CreateAttributeDialog) loader.getController()).getAttribute();
			}

			return null;
		});

		//If a result could be gathered, register it
		Optional<Attribute> result = dialog.showAndWait();
		if (result.isPresent()) {
			Attribute a = result.get();
			String id = pim.addAttribute(a.getName(), a.getDefaultValue(), a.getLegalValues());

			//Get the new attribute and add it to the list
			attributeList.add(pim.getAttribute(id));
		}
	}

	@FXML
	private void removeButtonOnAction(ActionEvent event) {
		Attribute selection = attributeListView.getSelectionModel().getSelectedItem();
		if (selection == null) return;

		Optional<ButtonType> choice = confirmationDialog.showAndWait();
		if (choice.isPresent() && choice.get() == ButtonType.OK) {
			pim.removeAttribute(selection.getID());
			attributeList.remove(selection);
		}
	}

	private void listViewSelectionChanged(Observable observable) {
		Attribute selection = attributeListView.getSelectionModel().getSelectedItem();
		attributeIDLabel.setText(selection.getID());
		attributeNameField.setText(selection.getName());
		attributeDefaultValueField.setText(selection.getDefaultValue().toString());
		attributeDefaultTypeLabel.setText("[" + selection.getDefaultValue().getClass().getSimpleName() + "]");

		Set<Object> legals = selection.getLegalValues();
		if (legals == null) {
			attributeLegalValuesList.clear();
		} else {
			attributeLegalValuesList.setAll(legals);
		}
	}
}
