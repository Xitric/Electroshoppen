package pim.presentation;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import pim.business.Attribute;
import pim.business.Category;
import pim.business.PIM;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
    //private List<Attribute> tempAdd;
    //private List<Attribute> tempRemove;
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

    private void setItemsInLw(){
        List<Category> categories = null;
        try {
            categories = pim.getCategories();
            Collections.sort(categories);
            categoryList.setAll(categories);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //List<Attribute> attributes = pim.getAttributes();
        //Collections.sort(attributes);

        //attributeAddList.setAll(attributes);
        //attributeRemoveList.setAll(attributes);
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

    private void listViewAddSelectionChanged(Observable observable) {
        Attribute selected = listViewAdd.getSelectionModel().getSelectedItem();


    }
    private void listViewRemoveSelectionChanged(Observable observable){
        Attribute selected = listViewAdd.getSelectionModel().getSelectedItem();

    }

    @FXML
    private void addCategory(ActionEvent event) {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add new category");

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.NO);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, cancelButton);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        TextField cName = new TextField();
        grid.add(new Label("Name: "), 0, 1);
        grid.add(cName,1,1);

        dialog.getDialogPane().setContent(grid);
        dialog.showAndWait().ifPresent(result -> {
            if (result == saveButton && !cName.getText().trim().equals("")) {
                pim.addCategory(cName.getText());
                setItemsInLw();
            }
        });
    }

    @FXML
    private void removeCategory(ActionEvent event) {
        Category selected = listViewCategory.getSelectionModel().getSelectedItem();
        categoryList.remove(selected);
    }

    @FXML
    private void addAttribute(ActionEvent event) {
        Attribute selected = listViewAdd.getSelectionModel().getSelectedItem();
      //  List tempAdd = new ArrayList(pim.getAttributesFromCategory(selected.getName()));
        if(selected != null){
        attributeAddList.add(selected);
        attributeRemoveList.remove(selected);
    }}

    @FXML
    private void removeAttribute(ActionEvent event) {
        Attribute selected = listViewRemove.getSelectionModel().getSelectedItem();
        if(selected != null){
        attributeAddList.add(selected);
        attributeRemoveList.remove(selected);
    }}

    @FXML
    private void save(ActionEvent event) {
        List<Category> tempData = null;
        try {
            tempData = new ArrayList<>(pim.getCategories());
            tempData.removeAll(categoryList);
            if (!tempData.isEmpty()) {
                for (Category c : tempData) {
                    pim.removeCategory(c.getName());
                    System.out.println("Removed category: " + c.getName());
                }
            }
            } catch(IOException e){
                e.printStackTrace();
            }

        }

//        List<Category> tempCategories = new ArrayList<>(categoryList);
//        tempCategories.removeAll(pim.getCategories());
//        if (!tempCategories.isEmpty()) {
//            for (Category c : tempCategories) {
//                pim.addCategory(c.getName());
//                System.out.println("Added: " + c.getName());
//            }
//        }
    }

