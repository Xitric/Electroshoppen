package shared.presentation.pim;

import dam.business.DAM;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import pim.business.*;
import shared.presentation.AlertUtil;
import shared.presentation.ListViewDialog;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * @author Kasper
 * @author Mikkel
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
	private ListView<Category> availableCategoriesView;

	@FXML
	private ListView<Category> containedCategoriesView;

	@FXML
	private VBox attributeVBox;

	@FXML
	private TextArea tagTextArea;

	private Image packageImage;
	private Image redPackageImage;
	private Image gearImage;

	private ObservableList<Category> availableCategories;
	private ObservableList<Category> containedCategories;

	private Map<Button, Pair<Attribute.AttributeValue, Label>> attributeValues;

	/** The mediator for the business layer. */
	private PIM pim;

	/** The mediator for the dam. */
	private DAM dam;

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		//Product list
		packageImage = new Image(getClass().getResourceAsStream("res/package.png"));
		redPackageImage = new Image(getClass().getResourceAsStream("res/packageRed.png"));
		productTreeView.getSelectionModel().selectedItemProperty().addListener(this::treeViewSelectionChanged);
		refreshButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("res/refreshButton.png"))));

		//Category lists
		availableCategories = FXCollections.observableArrayList();
		containedCategories = FXCollections.observableArrayList();
		availableCategoriesView.setItems(availableCategories);
		containedCategoriesView.setItems(containedCategories);

		//Attributes
		attributeValues = new HashMap<>();
		gearImage = new Image(getClass().getResourceAsStream("res/gear.png"));
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
	 * Set the dam mediator for this controller to use.
	 *
	 * @param dam the mediator for the dam
	 */
	public void setDAM(DAM dam) {
		this.dam = dam;
	}

	/**
	 * Call this when the view for this controller is entered in the GUI.
	 */
	public void onEnter() {
		if (pim != null) {
			try {
				populateTreeView(pim.getProducts(), true);
			} catch (IOException e) {
				AlertUtil.newAlertDialog(
						Alert.AlertType.ERROR,
						"Error",
						"PIM Error",
						"Unable to load products")
						.showAndWait();
			}
		}
	}

	private void removeImage(RemoveableImage img) {
		productImagePane.getChildren().remove(img);
	}

	@FXML
	private void refreshButtonOnAction(ActionEvent event) {
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
	private void synchronizeButtonOnAction(ActionEvent event) {
		boolean success = pim.synchronize();

		if (success) {
			//Refresh product list
			try {
				populateTreeView(pim.getProducts(), true);
			} catch (IOException e) {
				AlertUtil.newErrorAlert("Error", "PIM Error", "Error retrieving products from the PIM").showAndWait();
			}
		} else {
			AlertUtil.newErrorAlert("Error", "Synchronization Error", "Synchronization failed, try again later (as in right now)").showAndWait();
		}
	}

	@FXML
	private void addButtonOnAction(ActionEvent event) {
		Category selection = availableCategoriesView.getSelectionModel().getSelectedItem();
		if (selection != null) {
			availableCategories.remove(selection);
			containedCategories.add(selection);

			//New attributes have not been handled. The user must save and refresh manually
		}
	}

	@FXML
	private void removeButtonOnAction(ActionEvent event) {
		Category selection = containedCategoriesView.getSelectionModel().getSelectedItem();
		if (selection != null) {
			availableCategories.add(selection);
			containedCategories.remove(selection);

			//Lost attributes have not been handled. The user must save and refresh manually
		}
	}

	@FXML
	private void browseButtonOnAction(ActionEvent event) {
		pim.business.Image img = dam.getImage();

		if (img == null || img.getURL() == null) {
			browseTextField.clear();
		} else {
			browseTextField.setText(img.getURL());
		}
	}

	@FXML
	private void cancelButtonOnAction(ActionEvent event) {
		browseTextField.clear();
	}

	@FXML
	private void saveButtonOnAction(ActionEvent event) {
		TreeItem selection = productTreeView.getSelectionModel().getSelectedItem();

		if (selection != null && selection.getValue() instanceof Product) { //If the selection is a product
			Product product = (Product) selection.getValue();

			//Description
			product.setDescription(descriptionTextArea.getText());

			//Categories
			product.setCategories(containedCategories);

			//Attribute values
			for (Map.Entry<Button, Pair<Attribute.AttributeValue, Label>> entry : attributeValues.entrySet()) {
				Attribute.AttributeValue value = entry.getValue().getKey();
				product.setAttribute(value.getParent(), value.getValue());
			}

			//Tags
			ArrayList<Tag> allTags = new ArrayList<>();
			if (!tagTextArea.getText().isEmpty()) {
				String[] tagArray = tagTextArea.getText().split(", ");
				for (String tag : tagArray) {
					allTags.add(pim.createTag(tag));
				}
			}
			product.setTags(allTags);

			List<pim.business.Image> images = new ArrayList<>();
			for (Node n : productImagePane.getChildren()) {
				if (n instanceof RemoveableImage) {
					RemoveableImage image = (RemoveableImage) n;
					images.add(image.getImage());
				}
			}
			product.setImages(images);

			try {
				pim.saveProduct(product);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@FXML
	private void uploadButtonOnAction(ActionEvent event) {
		try {
			String url = browseTextField.getText();
			pim.business.Image image = pim.createImage(url);
			productImagePane.getChildren().add(new RemoveableImage(image, this::removeImage));
		} catch (IOException e) {
			AlertUtil.newAlertDialog(
					Alert.AlertType.ERROR,
					"Error",
					"PIM Error",
					"Unable to upload image to PIM")
					.showAndWait();
		}
	}

	private void treeViewSelectionChanged(Observable observable) {
		TreeItem selection = productTreeView.getSelectionModel().getSelectedItem();

		if (selection != null && selection.getValue() instanceof Product) { //If the selection is a product, set the fields
			Product product = (Product) selection.getValue();

			//Set basic information
			idLabel.setText(String.valueOf(product.getID()));
			nameLabel.setText(product.getName());
			priceLabel.setText(product.getPrice() + "kr.");
			descriptionTextArea.setText(product.getDescription());

			//Categories
			try {
				List<Category> categories = pim.getCategories();
				categories.removeAll(product.getCategories());
				availableCategories.setAll(categories);
			} catch (IOException e) {
				AlertUtil.newAlertDialog(
						Alert.AlertType.ERROR,
						"Error",
						"PIM Error",
						"Unable to load categories")
						.showAndWait();
			}
			containedCategories.setAll(product.getCategories());

			//Attributes
			attributeVBox.getChildren().clear();
			for (Attribute.AttributeValue value : product.getAttributeValues()) {
				HBox valueBox = new HBox(4);
				valueBox.setAlignment(Pos.CENTER_LEFT);

				//Labels
				valueBox.getChildren().add(new Label(value.getParent().toString() + ": "));
				Label valueLabel = new Label(value.getValue().toString());
				valueLabel.setUnderline(true);
				valueBox.getChildren().add(valueLabel);

				//Change button
				Button changeButton = new Button("", new ImageView(gearImage));
				changeButton.setOnAction(this::attributeValueEditOnAction);
				valueBox.getChildren().add(changeButton);
				attributeVBox.getChildren().add(valueBox);

				//Add to map for lookup
				attributeValues.put(changeButton, new Pair<>(value, valueLabel));
			}

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
				productImagePane.getChildren().add(new RemoveableImage(img, this::removeImage));
			}
		} else { //Else clear the fields
			idLabel.setText("");
			nameLabel.setText("");
			priceLabel.setText("");
			descriptionTextArea.clear();
			descriptionTextArea.clear();
			availableCategories.clear();
			containedCategories.clear();
			attributeVBox.getChildren().clear();
			tagTextArea.clear();
			productImagePane.getChildren().clear();

		}
	}

	private void attributeValueEditOnAction(ActionEvent event) {
		if (event.getSource() instanceof Button) {
			Button button = (Button) event.getSource();
			Attribute.AttributeValue attributeValue = attributeValues.get(button).getKey();
			Label attributeLabel = attributeValues.get(button).getValue();

			if (attributeValue != null) {
				Optional result;

				if (attributeValue.getParent().getLegalValues() == null) { //Unrestricted
					//Show attribute dialog and get result
					result = ValueSelectorFactory.getDialogValueSelectorForType(
							ValueSelectorFactory.AttributeType.fromClass(attributeValue.getValue().getClass())).showAndWait();
				} else { //Restricted
					Dialog dialog = new ListViewDialog<>(attributeValue.getParent().getLegalValues());
					result = dialog.showAndWait();
				}

				//If a value was selected, update it
				if (result.isPresent()) {
					try {
						Attribute.AttributeValue newValue = attributeValue.getParent().createValue(result.get());
						attributeValues.put(button, new Pair<>(newValue, attributeLabel));
						attributeLabel.setText(newValue.toString());
					} catch (IllegalArgumentException e) {
						AlertUtil.newErrorAlert("Error",
								"Illegal Value!",
								"Please choose a legal value as input")
								.showAndWait();
					}
				}
			}
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
			AlertUtil.newAlertDialog(
					Alert.AlertType.ERROR,
					"Error",
					"PIM Error",
					"Unable to load all categories")
					.showAndWait();
		}
	}
}
