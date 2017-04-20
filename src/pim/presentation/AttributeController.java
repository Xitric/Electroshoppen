package pim.presentation;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import pim.business.Attribute;
import pim.business.PIMFacade;

import java.net.URL;
import java.util.Collections;
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
	private TextField atributeNameField;

	private ObservableList<Attribute> attributeList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//TODO: Execute this when tab is selected, bind controller in main PIM controller
		attributeList = FXCollections.observableArrayList(PIMFacade.getPIM().getAttributes());
		Collections.sort(attributeList);
		attributeListView.setItems(attributeList);

		attributeListView.getSelectionModel().selectedItemProperty().addListener(this::listViewSelectionChanged);
	}

	@FXML
	private void addButtonOnAction(ActionEvent event) {

	}

	@FXML
	private void removeButtonOnAction(ActionEvent event) {

	}

	private void listViewSelectionChanged(Observable observable) {

	}
}
