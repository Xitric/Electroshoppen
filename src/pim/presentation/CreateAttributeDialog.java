package pim.presentation;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * @author Kasper
 */
public class CreateAttributeDialog implements Initializable {

	@FXML
	private TextField nameField;

	//TODO: Type instead of string
	@FXML
	private ComboBox<String> dataTypeComboBox;

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

	private ObservableSet<Object> legalValues;

	private ValueSelector currentSelector;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dataTypeComboBox.getSelectionModel().selectedItemProperty().addListener(this::dataTypeChanged);

		//See: http://stackoverflow.com/questions/33901891/binding-a-listview-to-an-observableset
		legalValues = FXCollections.observableSet();
		legalValues.addListener((SetChangeListener.Change<?> c) -> {
			if (c.wasAdded()) {
				legalValuesListView.getItems().add(c.getElementAdded());
			} else if (c.wasRemoved()) {
				legalValuesListView.getItems().remove(c.getElementRemoved());
			}
		});

		//TODO: START Temp
		dataTypeComboBox.setItems(FXCollections.observableArrayList("String", "Color", "Integer", "Character"));
		//TODO: END Temp
	}

	private void dataTypeChanged(Observable observable) {
		//TODO: Set value selection
		//TODO: START Temp
		switch (dataTypeComboBox.getSelectionModel().getSelectedItem()) {
			case "String":
				StringValueSelection svs = new StringValueSelection();
				currentSelector = svs;
				valueSelectionPane.setCenter(svs);
				break;
			default:
				currentSelector = null;
				valueSelectionPane.setCenter(null);
		}
		//TODO: END Temp

		//Clear all values when data type changes
		defaultValueField.clear();
		legalValues.clear();
	}

	@FXML
	private void restrictionChanged(ActionEvent event) {
		boolean restricted = restrictedCheckBox.isSelected();

		//Update gui
		legalValuesPane.setVisible(restricted);
		legalValuesPane.setManaged(restricted);
		addLegalButton.setDisable(!restricted);

		if (restricted) { //Clear default value when restricted
			defaultValueField.clear();
		} else { //Clear legal values when unrestricted
			legalValues.clear();
		}
	}

	@FXML
	private void addDefaultButtonOnAction(ActionEvent event) {
		//TODO: No need to handle null selector in final code
		if (currentSelector == null) return;

		Object[] values = currentSelector.getValues();
		if (values.length > 0) {
			//By convention, only the first value is added as default
			Object value = values[0];

			//Set default only if value is among legal values, or if values are unrestricted
			if (! restrictedCheckBox.isSelected() || legalValues.contains(value)) {
				defaultValueField.setText(value.toString());

				//TODO: Save object
			}
		}
	}

	@FXML
	private void addLegalButtonOnAction(ActionEvent event) {
		//TODO: No need to handle null selector in final code
		if (currentSelector == null) return;

		legalValues.addAll(Arrays.asList(currentSelector.getValues()));
	}

	/**
	 * Interface describing an element that can be used to select attribute values.
	 *
	 * @param <T> the type of the value
	 */
	private interface ValueSelector<T> {

		/**
		 * Get the currently selected values. May be empty.
		 *
		 * @return the currently selected values
		 */
		T[] getValues();
	}

	/**
	 * Describes a value selector for a single string.
	 */
	private class StringValueSelection extends HBox implements ValueSelector<String> {

		private TextField textField;

		private StringValueSelection() {
			super();

			setSpacing(8);

			//Add label
			getChildren().add(new Label("Value:"));

			//Add text field
			textField = new TextField();
			HBox.setHgrow(textField, Priority.ALWAYS);
			getChildren().add(textField);
		}

		@Override
		public String[] getValues() {
			//If nothing is written, return empty array
			if (textField.getText().length() == 0) return new String[0];

			//Otherwise return single valued array
			return new String[] {textField.getText()};
		}
	}

	//TODO: More selectors
}
