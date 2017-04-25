package pim.presentation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import pim.business.Category;
import pim.business.PIM;

import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;

/**
 * @author Kasper
 */
public class CategoryController implements Initializable {

	@FXML
	private ListView<Category> listViewCategory;
	@FXML
	private ListView<Category> listViewAdd;
	@FXML
	private ListView<Category> listViewRemove;
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

	/**
	 * The mediator for the business layer.
	 */
	private PIM pim;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		categoryList = FXCollections.observableArrayList(pim.getCategories());
		Collections.sort(categoryList);
		listViewCategory.setItems(categoryList);
	}

	/**
	 * Set the business mediator for this controller to use.
	 *
	 * @param pim the mediator for the pim
	 */
	public void setPIM(PIM pim) {
		this.pim = pim;
	}

	@FXML
	private void addCategory(ActionEvent event) {

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
