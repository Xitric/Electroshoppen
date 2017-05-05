package pim.presentation;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import pim.business.Attribute;
import pim.business.Category;
import pim.business.PIM;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Kasper
 */
public class CategoryController implements Initializable {

	@FXML
	private ListView<Category> listViewCategory;
	@FXML
	private ListView<Attribute> listViewAdd;
	@FXML
	private ListView<Attribute> listViewRemove;
	@FXML
	private Button btnAddCategory;
	@FXML
	private Button btnRemoveCategory;
	@FXML
	private Button btnAddAttribute;
	@FXML
	private Button btnRemoveAttribute;
	@FXML
	private Button btnSave;
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
		listViewAdd.getSelectionModel().selectedItemProperty().addListener(this::listViewAddSelectionChanged);
		listViewRemove.getSelectionModel().selectedItemProperty().addListener(this::listViewRemoveSelectionChanged);
	}

	/**
	 * Set the business mediator for this controller to use.
	 *
	 * @param pim the mediator for the pim
	 */
	public void setPIM(PIM pim) {
		this.pim = pim;
		setItemsInLw();
	}

	private void setItemsInLw() {
		//TODO:
		try {
			List<Category> categories = pim.getCategories();
			Collections.sort(categories);
			categoryList.setAll(categories);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void listViewCategorySelectionChanged(Observable observable) {
		Category selected = listViewCategory.getSelectionModel().getSelectedItem();
		nameOutput.setText(selected.getName());
		//TODO:
		try {
			attributeAddList.setAll(pim.getAttributesFromCategory(selected.getName()));
			attributeRemoveList.setAll(pim.getAttributesNotInTheCategory(selected.getName()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void listViewAddSelectionChanged(Observable observable) {
		Attribute selected = listViewAdd.getSelectionModel().getSelectedItem();

	}

	private void listViewRemoveSelectionChanged(Observable observable) {
		Attribute selected = listViewAdd.getSelectionModel().getSelectedItem();
	}

	@FXML
	private void addCategory(ActionEvent event) {
		Attribute selected;
	}

	@FXML
	private void removeCategory(ActionEvent event) {

	}

	@FXML
	private void addAttribute(ActionEvent event) {

	}

	@FXML
	private void removeAttribute(ActionEvent event) {

	}

	@FXML
	private void save(ActionEvent event) {

	}

}
