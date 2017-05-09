package pim.presentation;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
	private Button editDefaultButton;

	@FXML
	private Label attributeDefaultTypeLabel;

	@FXML
	private ListView<Object> attributeLegalValuesListView;

	private ObservableList<Attribute> attributeList;

	private ObservableList<Object> attributeLegalValuesList;

	private Alert confirmationDialog;

	private Object defaultValue;

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

		editDefaultButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("../../gear.png"))));
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
		//TODO
		try {
			List<Attribute> attributes = pim.getAttributes();
			Collections.sort(attributes);
			attributeList.setAll(attributes);
		} catch (IOException e) {

		}
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
			Attribute attribute = result.get();
			//TODO:
			try {
				pim.saveAttribute(attribute);
			} catch (IOException e) {
				e.printStackTrace();
			}

			//Get the new attribute and add it to the list
			attributeList.add(attribute);
		}
	}

	@FXML
	private void editDefaultButtonOnAction(ActionEvent event) {
		//Show attribute dialog and get result
		Optional result = ValueSelectorFactory.getDialogValueSelectorForType(
				ValueSelectorFactory.AttributeType.fromClass(defaultValue.getClass())).showAndWait();

		//If a value was selected, update it
		if (result.isPresent()) {
			defaultValue = attributeListView.getSelectionModel().getSelectedItem().createValue(result.get()).getValue();
			attributeDefaultValueField.setText(defaultValue.toString());
		}
	}

	@FXML
	private void removeButtonOnAction(ActionEvent event) {
		Attribute selection = attributeListView.getSelectionModel().getSelectedItem();
		if (selection == null) return;

		Optional<ButtonType> choice = confirmationDialog.showAndWait();
		if (choice.isPresent() && choice.get() == ButtonType.OK) {
			//TODO:
			try {
				pim.removeAttribute(selection.getID());
			} catch (IOException e) {
				e.printStackTrace();
			}
			attributeList.remove(selection);
		}
	}

	@FXML
	private void saveButtonOnAction(ActionEvent event) {
		Attribute selection = attributeListView.getSelectionModel().getSelectedItem();
		if (selection == null) return;

		selection.setName(attributeNameField.getText());
		selection.setDefaultValue(defaultValue);
		//TODO:
		try {
			pim.saveAttribute(selection);
			attributeListView.refresh();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void listViewSelectionChanged(Observable observable) {
		Attribute selection = attributeListView.getSelectionModel().getSelectedItem();

		if (selection == null) {
			//Clear
			attributeIDLabel.setText("");
			attributeNameField.setText("");
			attributeDefaultValueField.setText("");
			attributeDefaultTypeLabel.setText("");
			defaultValue = null;
			editDefaultButton.setDisable(true);
			attributeLegalValuesList.clear();
		} else {
			//Set values
			attributeIDLabel.setText(String.valueOf(selection.getID()));
			attributeNameField.setText(selection.getName());
			attributeDefaultValueField.setText(selection.getDefaultValue().toString());
			attributeDefaultTypeLabel.setText("[" + selection.getDefaultValue().getClass().getSimpleName() + "]");
			defaultValue = selection.getDefaultValue();
			editDefaultButton.setDisable(false);

			Set<Object> legals = selection.getLegalValues();
			if (legals == null) {
				attributeLegalValuesList.clear();
			} else {
				attributeLegalValuesList.setAll(legals);
			}
		}
	}
}
