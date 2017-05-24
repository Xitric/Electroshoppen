package shared.presentation.pim;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import pim.business.Attribute;
import pim.business.PIM;
import shared.presentation.AlertUtil;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * Controller for the dialog responsible for gathering data for creating new attributes.
 *
 * @author Kasper
 */
public class CreateAttributeDialog extends Dialog<Attribute> implements Initializable {

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
	 * The mediator for the business layer.
	 */
	private PIM pim;

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

	private Attribute attribute;

	/**
	 * Constructs a new dialog for creating new attributes.
	 *
	 * @param pim the mediator for the pim
	 */
	public CreateAttributeDialog(PIM pim) {
		this.pim = pim;

		setTitle("Create attribute");
		setHeaderText("Specify attribute information");
		ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

		//Style the dialog
		getDialogPane().getStylesheets().add(getClass().getResource("../electroshop.css").toExternalForm());

		//Load fxml content
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CreateAttributeDialog.fxml"));
			fxmlLoader.setController(this);
			Parent root = fxmlLoader.load();
			getDialogPane().setContent(root);
			getDialogPane().getScene().getWindow().sizeToScene();
		} catch (IOException e) {
			e.printStackTrace();
			AlertUtil.newErrorAlert("Error opening dialog!",
					"There was an error opening the \"create attribute\" dialog. Contact the system administrator.")
					.showAndWait();
		}

		//Specify how a result is gathered
		setResultConverter(button -> {
			if (button == saveButtonType) {
				return attribute;
			}

			return null;
		});

		//Ensure that the user cannot press OK unless data is specified properly. We also construct the attribute when
		//the save button is pressed
		final Button saveButton = (Button) getDialogPane().lookupButton(saveButtonType);
		saveButton.addEventFilter(ActionEvent.ACTION, event -> {
			attribute = getAttribute();
			if (attribute == null) {
				event.consume();
				AlertUtil.newErrorAlert("Insufficient information specified",
						"You must specify at least a name and a default value.")
						.showAndWait();
			}
		});
	}

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
	private Attribute getAttribute() {
		if (nameField.getText().isEmpty() || defaultValue == null)
			return null;
		String name = nameField.getText();

		try {
			if (restrictedCheckBox.isSelected()) {
				return pim.createAttribute(name, defaultValue, legalValues);
			} else {
				return pim.createAttribute(name, defaultValue, null);
			}
		} catch (IOException e) {
			return null;
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