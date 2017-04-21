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
				StringValueSelector svs = new StringValueSelector();
				currentSelector = svs;
				valueSelectionPane.setCenter(svs);
				break;
			case "Integer":
				IntegerValueSelector ivs = new IntegerValueSelector();
				currentSelector = ivs;
				valueSelectionPane.setCenter(ivs);
				break;
			default:
				currentSelector = null;
				valueSelectionPane.setCenter(null);
		}

		valueSelectionPane.getScene().getWindow().sizeToScene();
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
			if (!restrictedCheckBox.isSelected() || legalValues.contains(value)) {
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
	private class StringValueSelector extends HBox implements ValueSelector<String> {

		private TextField textField;

		private StringValueSelector() {
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
			return new String[]{textField.getText()};
		}
	}

	/**
	 * Describes a value selector for a range of integers.
	 */
	private class IntegerValueSelector extends VBox implements ValueSelector<Integer> {

		private TextField minField;
		private TextField maxField;

		private IntegerValueSelector() {
			super();

			setSpacing(8);

			//Min row
			HBox minRow = new HBox(8);
			minRow.getChildren().add(new Label("Min (inclusive):"));
			minField = new TextField();
			HBox.setHgrow(minField, Priority.ALWAYS);
			minRow.getChildren().add(minField);
			getChildren().add(minRow);

			//Max row
			HBox maxRow = new HBox(8);
			maxRow.getChildren().add(new Label("Max (exclusive):"));
			maxField = new TextField();
			HBox.setHgrow(maxField, Priority.ALWAYS);
			maxRow.getChildren().add(maxField);
			getChildren().add(maxRow);
		}

		@Override
		public Integer[] getValues() {
			boolean isMinSpecified = false;
			boolean isMaxSpecified = false;
			int min = 0;
			int max = 0;

			//Read user input
			try {
				min = Integer.parseInt(minField.getText());
				isMinSpecified = true; //Skipped if above throws an exception
			} catch (NumberFormatException e) {
				e.printStackTrace();
				//TODO: Notify user
			}

			try {
				max = Integer.parseInt(maxField.getText());
				isMaxSpecified = true; //Skipped if above throws an exception
			} catch (NumberFormatException e) {
				e.printStackTrace();
				//TODO: Notify user
			}

			if (!isMaxSpecified) {
				//If max is not specified, but minimum is, then return minimum. Otherwise, if minimum is not specified
				//either, return empty array
				if (isMinSpecified) {
					return new Integer[]{min};
				} else {
					return new Integer[0];
				}
			} else if (isMinSpecified) {
				//If both max and min are specified, return array of all integers between min and max
				Integer[] values = new Integer[max - min];
				for (int i = min; i < max; i++) {
					values[i - min] = i;
				}

				return values;
			} else {
				//Max is specified, but minimum is not, so return empty array
				return new Integer[0];
			}
		}
	}

	//TODO: More selectors
}
