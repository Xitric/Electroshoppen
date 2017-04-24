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
import java.util.Collections;
import java.util.List;
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

	/**
	 * The mediator for the business layer.
	 */
	private PIM pim;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		attributeList = FXCollections.observableArrayList();
		attributeListView.setItems(attributeList);
		attributeListView.getSelectionModel().selectedItemProperty().addListener(this::listViewSelectionChanged);

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

		dialog.showAndWait();
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
	}
}
