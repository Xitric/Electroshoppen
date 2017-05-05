package pim.presentation;

import javafx.beans.Observable;
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
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

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
	private TreeView<Object> productTreeView;

	@FXML
	private TextArea tagTextArea;

	private Image packageImage;
	private Image redPackageImage;

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
		List<Product> allProducts = pim.getProducts();
		List<Category> allCategories = pim.getCategories();

		TreeItem<Object> treeRoot = new TreeItem<>("All categories");
		treeRoot.setExpanded(true);

		//TODO: Super inefficient!
		//Add categories and products to tree view
		TreeItem<Object> uncategorised = new TreeItem<>("Uncategorised", new ImageView(redPackageImage));
		for (Product p : allProducts) {
			if (p.getCategories().isEmpty()) {

				//Only make uncategorised category if some products belong to it
				if (treeRoot.getChildren().isEmpty()) {
					treeRoot.getChildren().add(uncategorised);
				}

				uncategorised.getChildren().add(new TreeItem<>(p));
			}
		}

		for (Category c : allCategories) {
			TreeItem<Object> category = new TreeItem<>(c, new ImageView(packageImage));
			treeRoot.getChildren().add(category);

			for (Product p : allProducts) {
				if (p.hasCategory(c)) {
					category.getChildren().add(new TreeItem<>(p));
				}
			}
		}

		productTreeView.setRoot(treeRoot);
	}

	private void removeImage(RemoveableImage img) {
		//TODO
		productImagePane.getChildren().remove(img);
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
		Object selection = productTreeView.getSelectionModel().getSelectedItem().getValue();

		if (selection instanceof Product) { //If the selection is a product, set the fields
			Product product = (Product) selection;

			//Set basic information
			idLabel.setText(String.valueOf(product.getID()));
			nameLabel.setText(product.getName());
			priceLabel.setText(product.getPrice() + "$");
			//TODO: Description
			//TODO: Categories
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
			for (pim.business.Image img: product.getImages()) {
				productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
			}
		} else { //Else clear the fields
			//TODO
		}
	}
}
