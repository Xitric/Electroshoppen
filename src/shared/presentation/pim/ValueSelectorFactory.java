package shared.presentation.pim;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.awt.*;

/**
 * Factory class for creating value selectors for attribute selection. This factory also has convenience methods for
 * wrapping the selectors in dialog panes.
 *
 * @author Kasper
 */
public class ValueSelectorFactory {

	/**
	 * Get the value selector associated with the specified attribute type.
	 *
	 * @param type the attribute type
	 * @return the value selector associated with the specified type, or null if no such value selector could be made
	 */
	public static ValueSelector<?> getValueSelectorForType(AttributeType type) {
		switch (type) {
			case STRING:
				return new StringValueSelector();
			case SEPARATED_STRINGS:
				return new SeparatedStringsValueSelector();
			case CHARACTER:
				return new CharValueSelector();
			case INTEGER:
				return new IntegerValueSelector();
			case FLOATING_POINT:
				return new DoubleValueSelector();
			case COLOR:
				return new ColorValueSelector();
		}

		return null;
	}

	/**
	 * Get the value selector associated with the specified attribute type wrapped in a dialog.
	 *
	 * @param type the attribute type
	 * @return the value selector associated with the specified type wrapped in a dialog, or null if no such value
	 * selector could be made
	 */
	public static Dialog<Object> getDialogValueSelectorForType(AttributeType type) {
		//Setup dialog
		Dialog<Object> dialog = new Dialog<>();
		dialog.setTitle("Select value");
		dialog.setHeaderText("Specify attribute value");

		//The value selectors should also all be JavaFX nodes
		ValueSelector<?> selector = getValueSelectorForType(type);
		dialog.getDialogPane().setContent((Node) selector);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		//Specify how the result is gathered
		dialog.setResultConverter(button -> {
			if (button == ButtonType.OK && selector != null) {
				return selector.getValue();
			}

			return null;
		});

		return dialog;
	}

	public enum AttributeType {
		STRING("String", String.class),
		SEPARATED_STRINGS("Separated Strings", String.class),
		CHARACTER("Character", Character.class),
		INTEGER("Integer", Integer.class),
		FLOATING_POINT("Floating Point", Double.class),
		COLOR("Color", Color.class);

		private final String name;
		private final Class jType;

		AttributeType(String name, Class jType) {
			this.name = name;
			this.jType = jType;
		}

		/**
		 * Get the AttributeType enum associated with the specified class, or null if no such association exists.
		 *
		 * @param c the class to test for
		 * @return the AttributeType enum associated with the specified class, or null if no such association exists
		 */
		public static AttributeType fromClass(Class c) {
			for (AttributeType a : values()) {
				if (a.jType == c) {
					return a;
				}
			}

			return null;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	/**
	 * Describes a value selector for a single string.
	 */
	private static class StringValueSelector extends HBox implements ValueSelector<String> {

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
			String text = getValue();
			if (text == null) return new String[0];

			//Otherwise return single valued array
			return new String[]{text};
		}

		@Override
		public String getValue() {
			//If nothing is written, return null
			if (textField.getText().isEmpty()) return null;

			return textField.getText();
		}
	}

	/**
	 * Describes a value selector for a multiple strings separated by semicolons (;).
	 */
	private static class SeparatedStringsValueSelector extends VBox implements ValueSelector<String> {

		private TextField textField;

		private SeparatedStringsValueSelector() {
			super();

			setSpacing(8);

			HBox inputRow = new HBox(8);

			//Add label
			inputRow.getChildren().add(new Label("Value:"));

			//Add text field
			textField = new TextField();
			HBox.setHgrow(textField, Priority.ALWAYS);
			inputRow.getChildren().add(textField);
			getChildren().add(inputRow);

			//Help message
			getChildren().add(new Label("Separate strings with semicolons ';'"));
		}

		@Override
		public String[] getValues() {
			//If nothing is written, return empty array
			if (textField.getText().isEmpty()) return new String[0];

			//Otherwise return split string
			return textField.getText().split(";");
		}

		@Override
		public String getValue() {
			if (textField.getText().isEmpty()) {
				return null;
			}

			//Return first value
			return getValues()[0];
		}
	}

	/**
	 * Describes a value selector for a single character.
	 */
	private static class CharValueSelector extends HBox implements ValueSelector<Character> {

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
			//If value is null, return empty array
			Character c = getValue();
			if (c == null) return new Character[0];

			//Otherwise return single valued array
			return new Character[]{c};
		}

		@Override
		public Character getValue() {
			//If nothing or too much is written, return null
			if (textField.getText().isEmpty() || textField.getText().length() > 1) return null;

			//Otherwise return the single character. This is safe because we have ensured that the size is exactly 1
			//(negative lengths not possible)
			return textField.getText().charAt(0);
		}
	}

	/**
	 * Describes a value selector for a range of integers.
	 */
	private static class IntegerValueSelector extends VBox implements ValueSelector<Integer> {

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
				//TODO: Notify user
			}

			try {
				max = Integer.parseInt(maxField.getText());
				isMaxSpecified = true; //Skipped if above throws an exception
			} catch (NumberFormatException e) {
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

		@Override
		public Integer getValue() {
			//If no values are selected, return null
			Integer[] ints = getValues();
			if (ints.length == 0) return null;

			//Otherwise return first value
			return ints[0];
		}
	}

	/**
	 * Describes a value selector for a single double.
	 */
	private static class DoubleValueSelector extends HBox implements ValueSelector<Double> {

		private TextField textField;

		private DoubleValueSelector() {
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
		public Double[] getValues() {
			//If input is illegal, return empty array
			Double d = getValue();
			if (d == null) return new Double[0];

			//Otherwise return single valued array
			return new Double[]{d};
		}

		@Override
		public Double getValue() {
			try {
				//Try reading user input
				double number = Double.parseDouble(textField.getText());
				return number;
			} catch (NumberFormatException e) {
				e.printStackTrace();
				//TODO: Notify user

				//Illegal input, return null
				return null;
			}
		}
	}

	/**
	 * Describes a value selector for a single string.
	 */
	private static class ColorValueSelector extends HBox implements ValueSelector<Color> {

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
			return new Color[]{getValue()};
		}

		@Override
		public Color getValue() {
			//The color picker ensures that something is always selected
			javafx.scene.paint.Color color = colorPicker.getValue();
			return new Color((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue());
		}
	}
}
