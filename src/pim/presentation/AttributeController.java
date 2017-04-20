package pim.presentation;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import pim.business.Attribute;
import pim.business.PIMFacade;

import java.net.URL;
import java.util.Collections;
import java.util.Optional;
import java.util.ResourceBundle;

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

	private ObservableList<Attribute> attributeList;

	private Alert confirmationDialog;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//TODO: Execute this when tab is selected, bind controller in main PIM controller
		attributeList = FXCollections.observableArrayList(PIMFacade.getPIM().getAttributes());
		Collections.sort(attributeList);
		attributeListView.setItems(attributeList);

		attributeListView.getSelectionModel().selectedItemProperty().addListener(this::listViewSelectionChanged);

		confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
		confirmationDialog.setTitle("Delete attribute");
		confirmationDialog.setHeaderText("Confirm deletion");
		confirmationDialog.setContentText("Are you sure that you wish to delete this attribute?");
	}

	@FXML
	private void addButtonOnAction(ActionEvent event) {

	}

	@FXML
	private void removeButtonOnAction(ActionEvent event) {
		Attribute selection = attributeListView.getSelectionModel().getSelectedItem();
		if (selection == null) return;

		Optional<ButtonType> choice = confirmationDialog.showAndWait();
		if (choice.isPresent() && choice.get() == ButtonType.OK) {
			PIMFacade.getPIM().removeAttribute(selection.getID());
			attributeList.remove(selection);
		}
	}

	private void listViewSelectionChanged(Observable observable) {
		Attribute selection = attributeListView.getSelectionModel().getSelectedItem();
		//TODO: Trim in getters
		attributeIDLabel.setText(selection.getID().trim());
		attributeNameField.setText(selection.getName().trim());
	}
}
