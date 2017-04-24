package pim.presentation;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * Controller for the dialog responsible for gathering data for creating new attributes.
 *
 * @author Kasper
 */
public class CreateAttributeDialog implements Initializable {

	/**
	 * The data types that are supported by this dialogue window.
	 */
	private static final String[] dataTypes = {"String", "Character", "Integer", "Color"};

	@FXML
	private TextField nameField;

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
		dataTypeComboBox.setItems(FXCollections.observableArrayList(dataTypes));
		dataTypeComboBox.getSelectionModel().select(0);
	}

	private void dataTypeChanged(Observable observable) {
		//Depending on the selected data type, set the current value selector
		ValueSelector vs = null;

		switch (dataTypeComboBox.getSelectionModel().getSelectedItem()) {
			case "String":
				vs = new StringValueSelector();
				break;
			case "Character":
				vs = new CharValueSelector();
				break;
			case "Integer":
				vs = new IntegerValueSelector();
				break;
			case "Color":
				vs = new ColorValueSelector();
				break;
		}

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
		Object[] values = currentSelector.getValues();
		if (values.length > 0) {
			//By convention, only the first value is added as default
			Object value = values[0];

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
		legalValues.addAll(Arrays.asList(currentSelector.getValues()));

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
	 * Describes a value selector for a single character.
	 */
	private class CharValueSelector extends HBox implements ValueSelector<Character> {

		private TextField textField;

		private CharValueSelector() {
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
		public Character[] getValues() {
			//If nothing or too much is written, return empty array
			if (textField.getText().length() == 0 || textField.getText().length() > 1) return new Character[0];

			//Otherwise return single valued array. This is safe because we have ensured that the size is exactly 1
			//(negative lengths not possible)
			return new Character[]{textField.getText().charAt(0)};
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

	/**
	 * Describes a value selector for a single string.
	 */
	private class ColorValueSelector extends HBox implements ValueSelector<Color> {

		private ColorPicker colorPicker;

		private ColorValueSelector() {
			super();

			setAlignment(Pos.CENTER);

			//Add color picker
			colorPicker = new ColorPicker();
			getChildren().add(colorPicker);
		}

		@Override
		public Color[] getValues() {
			//The color picker ensures that something is always selected
			return new Color[]{colorPicker.getValue()};
		}
	}
}
