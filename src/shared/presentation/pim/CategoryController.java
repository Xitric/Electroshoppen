package shared.presentation.pim;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import pim.business.Attribute;
import pim.business.Category;
import pim.business.PIM;
import shared.Utility;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * @author Kasper
 * @author Emil
 */
public class CategoryController implements Initializable {

	@FXML
	private ListView<Category> listViewCategory;
	@FXML
	private ListView<Attribute> listViewAdd;
	@FXML
	private ListView<Attribute> listViewRemove;
	@FXML
	private Label nameOutput;

	private ObservableList<Category> categoryList;
	private ObservableList<Attribute> attributeAddList, attributeRemoveList;

	/**
	 * The mediator for the business layer.
	 */
	private PIM pim;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		categoryList = FXCollections.observableArrayList();
		attributeAddList = FXCollections.observableArrayList();
		attributeRemoveList = FXCollections.observableArrayList();
		listViewCategory.setItems(categoryList);
		listViewAdd.setItems(attributeAddList);
		listViewRemove.setItems(attributeRemoveList);
		listViewCategory.getSelectionModel().selectedItemProperty().addListener(this::listViewCategorySelectionChanged);
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
		if (pim != null) {
			setItemsInLw();
		}
	}

	private void setItemsInLw() {
		List<Category> categories;
		try {
			categories = pim.getCategories();
			Collections.sort(categories);
			categoryList.setAll(categories);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void listViewCategorySelectionChanged(Observable observable) {
		Category selected = listViewCategory.getSelectionModel().getSelectedItem();
		if (selected != null) {
			nameOutput.setText(selected.getName());
			try {
				attributeAddList.setAll(pim.getAttributesFromCategory(selected.getName()));
				attributeRemoveList.setAll(pim.getAttributesNotInTheCategory(selected.getName()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@FXML
	private void addCategory(ActionEvent event) {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Add new category");
		dialog.setHeaderText("Specify category name");
		dialog.setContentText("Name:");

		Optional<String> result = dialog.showAndWait();
		result.ifPresent(name -> {
			try {
				pim.createCategory(name);
				categoryList.add(pim.getCategory(name));
				Collections.sort(categoryList);
			} catch (IOException e) {
				Utility.newErrorAlert("Changes are not accepted!",
						"Error when creating a category",
						"Something went wrong when creating a category")
						.showAndWait();
			}
		});
	}

	@FXML
	private void removeCategory(ActionEvent event) {
		Category selected = listViewCategory.getSelectionModel().getSelectedItem();
		try {
			pim.removeCategory(selected.getName());
			categoryList.remove(selected);
		} catch (IOException e) {
			Utility.newErrorAlert("Changes are not accepted!",
					"Error when removing category",
					"Could not remove selected category")
					.showAndWait();
		}
	}

	@FXML
	private void addAttribute(ActionEvent event) {
		Attribute selected = listViewRemove.getSelectionModel().getSelectedItem();
		if (selected != null) {
			attributeAddList.add(selected);
			attributeRemoveList.remove(selected);
		}
	}

	@FXML
	private void removeAttribute(ActionEvent event) {
		Attribute selected = listViewAdd.getSelectionModel().getSelectedItem();
		if (selected != null) {
			attributeAddList.remove(selected);
			attributeRemoveList.add(selected);
		}
	}

	@FXML
	private void save(ActionEvent event) {
		Category selection = listViewCategory.getSelectionModel().getSelectedItem();
		if (selection == null) return;

		selection.setAttributes(listViewAdd.getItems());
		//TODO:
		try {
			pim.saveCategory(selection);
		} catch (IOException e) {
			Utility.newErrorAlert("Changes are not accepted!",
					"Error when saving",
					"Something went wrong when saving")
					.showAndWait();
		}
	}
}

