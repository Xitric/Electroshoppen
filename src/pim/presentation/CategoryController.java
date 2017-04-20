package pim.presentation;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import static javafx.collections.FXCollections.observableArrayList;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import pim.business.Category;
import pim.business.PIMFacade;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        categoryList = FXCollections.observableArrayList(PIMFacade.getPIM().getCategories());
        Collections.sort(categoryList);
        listViewCategory.setItems(categoryList);
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
