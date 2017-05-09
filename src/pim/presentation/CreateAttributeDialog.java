package pim.presentation;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import pim.business.Attribute;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * Controller for the dialog responsible for gathering data for creating new attributes.
 *
 * @author Kasper
 */
public class CreateAttributeDialog implements Initializable {

	@FXML
	private TextField nameField;

	@FXML
	private ComboBox<ValueSelectorFactory.AttributeType> dataTypeComboBox;

	@FXML
	private CheckBox restrictedCheckBox;

	@FXML
	private TextField defaultValueField;

	@FXML
	private VBox legalValuesPane;

	@FXML
	private ListView<Object> legalValuesListView;

	@FXML
	private Button addLegalButton;

	@FXML
	private BorderPane valueSelectionPane;

	/**
	 * The set of legal values currently selected.
	 */
	private ObservableSet<Object> legalValues;

	/**
	 * The currently selected default value.
	 */
	private Object defaultValue;

	/**
	 * The current control for selecting attribute values. Varies with the selected data type.
	 */
	private ValueSelector currentSelector;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Listen to changes in the selected data type
		dataTypeComboBox.getSelectionModel().selectedItemProperty().addListener(this::dataTypeChanged);

		//See: http://stackoverflow.com/questions/33901891/binding-a-listview-to-an-observableset
		//Make the list view of legal values display a 'set' of objects
		legalValues = FXCollections.observableSet();
		legalValues.addListener((SetChangeListener.Change<?> c) -> {
			if (c.wasAdded()) {
				legalValuesListView.getItems().add(c.getElementAdded());
			} else if (c.wasRemoved()) {
				legalValuesListView.getItems().remove(c.getElementRemoved());
			}
		});

		//Populate combo box for data types
		dataTypeComboBox.setItems(FXCollections.observableArrayList(ValueSelectorFactory.AttributeType.values()));
		dataTypeComboBox.getSelectionModel().select(0);
	}

	/**
	 * Get the attribute created by this dialog, or null if no proper attribute has been specified.
	 *
	 * @return the resulting attribute
	 */
	public Attribute getAttribute() {
		if (nameField.getText().isEmpty() || defaultValue == null) return null;
		String name = nameField.getText();

		if (restrictedCheckBox.isSelected()) {
			return new Attribute(-1, name, defaultValue, legalValues);
		} else {
			return new Attribute(-1, name, defaultValue);
		}
	}

	private void dataTypeChanged(Observable observable) {
		//Depending on the selected data type, set the current value selector
		ValueSelector vs = ValueSelectorFactory.getValueSelectorForType(dataTypeComboBox.getSelectionModel().getSelectedItem());

		//The value of vs should not be null, or we have made a programming error
		currentSelector = vs;
		valueSelectionPane.setCenter((Node) vs); //This should be safe

		//We only resize the window if the content had been added to a dialog window
		if (valueSelectionPane.getScene() != null)
			valueSelectionPane.getScene().getWindow().sizeToScene(); //Resize to fit content

		//Clear all values when data type changes
		setDefaultValue(null);
		legalValues.clear();
	}

	/**
	 * Set the currently selected default value. It is assumed that it is part of the legal values.
	 *
	 * @param o the default value
	 */
	private void setDefaultValue(Object o) {
		this.defaultValue = o;

		if (o == null) {
			this.defaultValueField.clear();
		} else {
			this.defaultValueField.setText(o.toString());
		}
	}

	@FXML
	private void restrictionChanged(ActionEvent event) {
		boolean restricted = restrictedCheckBox.isSelected();

		//Update gui
		legalValuesPane.setVisible(restricted);
		addLegalButton.setDisable(!restricted);

		if (restricted) { //Clear default value when restricted
			this.setDefaultValue(null);
		} else { //Clear legal values when unrestricted
			legalValues.clear();
		}
	}

	@FXML
	private void addDefaultButtonOnAction(ActionEvent event) {
		Object value = currentSelector.getValue();
		if (value != null) {
			//If values are restricted, add value to legal values
			if (restrictedCheckBox.isSelected()) {
				legalValues.add(value);
			}

			//Set default value
			setDefaultValue(value);
		}
	}

	@FXML
	private void addLegalButtonOnAction(ActionEvent event) {
		//Do nothing if selected value is nonexistent
		Object[] values = currentSelector.getValues();
		if (values.length == 0) return;

		legalValues.addAll(Arrays.asList(values));

		//If the default value is null, set it
		if (defaultValue == null) {
			setDefaultValue(legalValuesListView.getItems().get(0));
		}
	}

	@FXML
	private void removeValueButtonOnAction(ActionEvent event) {
		Object selected = legalValuesListView.getSelectionModel().getSelectedItem();

		//Remove selected item, if any
		if (selected != null) {
			legalValues.remove(selected);

			//If the default value was removed, clear it
			if (selected.equals(defaultValue)) {
				//If there are legal values, set it to the first, otherwise null
				if (legalValues.size() > 0) {
					setDefaultValue(legalValuesListView.getItems().get(0));
				} else {
					setDefaultValue(null);
				}
			}
		}
	}
}