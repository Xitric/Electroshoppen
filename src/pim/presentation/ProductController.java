package pim.presentation;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import pim.business.Category;
import pim.business.PIM;
import pim.business.Product;
import pim.business.Tag;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * @author Kasper
 */
public class ProductController implements Initializable {

	@FXML
	private Label idLabel;

	@FXML
	private Label nameLabel;

	@FXML
	private Label priceLabel;

	@FXML
	private TextArea descriptionTextArea;

	@FXML
	private FlowPane productImagePane;

	@FXML
	private TextField browseTextField;

	@FXML
	private Button refreshButton;

	@FXML
	private TextField searchBar;

	@FXML
	private TreeView<Object> productTreeView;

	@FXML
	private TextArea tagTextArea;

	@FXML
	private ListView<Category> availableCategoriesView;

	@FXML
	private ListView<Category> containedCategoriesView;

	private Image packageImage;
	private Image redPackageImage;

	private ObservableList<Category> availableCategories;
	private ObservableList<Category> containedCategories;

	/**
	 * The mediator for the business layer.
	 */
	private PIM pim;

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		packageImage = new Image(getClass().getResourceAsStream("../../package.png"));
		redPackageImage = new Image(getClass().getResourceAsStream("../../packageRed.png"));
		productTreeView.getSelectionModel().selectedItemProperty().addListener(this::treeViewSelectionChanged);
		refreshButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("../../refreshButton.png"))));
		availableCategories = FXCollections.observableArrayList();
		containedCategories = FXCollections.observableArrayList();
		availableCategoriesView.setItems(availableCategories);
		containedCategoriesView.setItems(containedCategories);
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
		//TODO:
		try {
			populateTreeView(pim.getProducts(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void removeImage(RemoveableImage img) {
		//TODO
		productImagePane.getChildren().remove(img);
	}

	@FXML
	void refreshButtonOnAction(ActionEvent event) {
		String searchValue = searchBar.getText();

		try {
			if (searchValue.isEmpty()) {
				populateTreeView(pim.getProducts(), true);
			} else {
				List<Product> products = pim.getProducts();

				//Ignore case when searching
				products.removeIf(product -> !product.getName().toLowerCase().contains(searchValue.toLowerCase()));
				populateTreeView(products, false);

				//Select first product, if any
				TreeItem<Object> firstCategory = productTreeView.getRoot().getChildren().get(0);
				if (firstCategory != null) {
					firstCategory.setExpanded(true);
					productTreeView.getSelectionModel().select(firstCategory.getChildren().get(0));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void browseButtonOnAction(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.bmp", "*.gif", "*.png", "*.jpeg", "*.wbmp"));
		File selectedFile = fileChooser.showOpenDialog(browseTextField.getScene().getWindow());
		if (selectedFile != null) {
			browseTextField.setText(selectedFile.getPath());
		}
	}

	@FXML
	void cancelButtonOnAction(ActionEvent event) {
		browseTextField.clear();
	}

	@FXML
	void saveButtonOnAction(ActionEvent event) {

	}

	@FXML
	void uploadButtonOnAction(ActionEvent event) {
		try {
			pim.business.Image img = new pim.business.Image(browseTextField.getText());
			productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		} catch (IllegalArgumentException e) {
			//TODO: Whatever
		}
	}

	private void treeViewSelectionChanged(Observable observable) {
		TreeItem selection = productTreeView.getSelectionModel().getSelectedItem();

		if (selection != null && selection.getValue() instanceof Product) { //If the selection is a product, set the fields
			Product product = (Product) selection.getValue();

			//Set basic information
			idLabel.setText(String.valueOf(product.getID()));
			nameLabel.setText(product.getName());
			priceLabel.setText(product.getPrice() + "$");
			descriptionTextArea.setText(product.getDescription());
			//TODO: Exception handling
			try {
				List<Category> categories = pim.getCategories();
				categories.removeAll(product.getCategories());
				availableCategories.setAll(categories);
			} catch (IOException e) {
				e.printStackTrace();
			}
			containedCategories.setAll(product.getCategories());
			//TODO: Attributes

			//Set tags
			StringBuilder tagText = new StringBuilder();
			for (Iterator<Tag> iter = product.getTags().iterator(); iter.hasNext(); ) {
				tagText.append(iter.next().getName());
				if (iter.hasNext()) tagText.append(", ");
			}
			tagTextArea.setText(tagText.toString());

			//Set images
			productImagePane.getChildren().clear();
			for (pim.business.Image img : product.getImages()) {
				productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
			}
		} else { //Else clear the fields
			idLabel.setText("");
			nameLabel.setText("");
			priceLabel.setText("");
			descriptionTextArea.clear();
			descriptionTextArea.clear();
			availableCategories.clear();
			containedCategories.clear();
			//TODO: Attributes
			tagTextArea.clear();
			productImagePane.getChildren().clear();

		}
	}

	/**
	 * Populate the tree view with the specified products.
	 *
	 * @param products      the products to place in the tree view.
	 * @param allCategories if true, the tree view will contain all categories. Otherwise the tree view will only
	 *                      contain the categories of the specified products
	 */
	private void populateTreeView(List<Product> products, boolean allCategories) {
		//TODO:
		try {
			TreeItem<Object> treeRoot = new TreeItem<>();
			treeRoot.setExpanded(true);

			//Add categories and products to tree view
			TreeItem<Object> uncategorised = new TreeItem<>("Uncategorised", new ImageView(redPackageImage));
			for (Product p : products) {
				if (p.getCategories().isEmpty()) {

					//Only make uncategorised category if some products belong to it
					if (treeRoot.getChildren().isEmpty()) {
						treeRoot.getChildren().add(uncategorised);
					}

					uncategorised.getChildren().add(new TreeItem<>(p));
				}
			}

			if (allCategories) { //Populate with all categories
				for (Category c : pim.getCategories()) {
					TreeItem<Object> category = new TreeItem<>(c, new ImageView(packageImage));
					treeRoot.getChildren().add(category);

					for (Product p : products) {
						if (p.hasCategory(c)) {
							category.getChildren().add(new TreeItem<>(p));
						}
					}
				}
			} else { //Populate only with used categories
				Map<Category, TreeItem<Object>> categoryNodes = new HashMap<>();

				for (Product p : products) {
					for (Category c : p.getCategories()) {
						TreeItem<Object> category = categoryNodes.getOrDefault(c, new TreeItem<>(c, new ImageView(packageImage)));
						treeRoot.getChildren().add(category);
						category.getChildren().add(new TreeItem<>(p));
						categoryNodes.put(c, category);
					}
				}
			}

			productTreeView.setRoot(treeRoot);
			productTreeView.setShowRoot(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
