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
import shared.Utility;

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

		editDefaultButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("res/gear.png"))));
	}

	/**
	 * Set the business mediator for this controller to use.
	 *
	 * @param pim the mediator for the pim
	 */
	public void setPIM(PIM pim) {
		this.pim = pim;
		onEnter();
	}

	/**
	 * Call this when the view for this controller is entered in the GUI.
	 */
	public void onEnter() {
		if (pim != null) {
			//TODO
			try {
				List<Attribute> attributes = pim.getAttributes();
				Collections.sort(attributes);
				attributeList.setAll(attributes);
			} catch (IOException e) {

			}
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
			((CreateAttributeDialog) loader.getController()).setPIM(pim);
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
		boolean resultWasBad = true;
		while (resultWasBad) {
			Optional<Attribute> result = dialog.showAndWait();
			if (result.isPresent()) {
				try {
					Attribute attribute = result.get();

					try {
						pim.saveAttribute(attribute);
						resultWasBad = false;

						//Get the new attribute and add it to the list
						attributeList.add(attribute);
					} catch (IOException e) {
						Utility.newErrorAlert("Changes are not accepted!",
								"Could not save attribute",
								"Something went wrong when saving the attribute")
								.showAndWait();
					}
				} catch (NoSuchElementException e) {
					Utility.newErrorAlert("Changes are not accepted!",
							"Critical error",
							"No element could be found")
							.showAndWait();
				}
			} else {
				resultWasBad = false;
			}
		}
	}

	@FXML
	private void editDefaultButtonOnAction(ActionEvent event) {
		//Show attribute dialog and get result
		Optional result = ValueSelectorFactory.getDialogValueSelectorForType(
				ValueSelectorFactory.AttributeType.fromClass(defaultValue.getClass())).showAndWait();

		//If a value was selected, update it
		if (result.isPresent()) {
			defaultValue = result.get();
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
				attributeList.remove(selection);
			} catch (IOException e) {
				Utility.newErrorAlert("Changes are not accepted!",
						"Could not remove attribute",
						"Something went wrong when removing the attribute")
						.showAndWait();
			}
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
			Utility.newErrorAlert("Changes are not accepted!",
					"Could not save attribute",
					"Something went wrong when saving the attribute")
					.showAndWait();
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
