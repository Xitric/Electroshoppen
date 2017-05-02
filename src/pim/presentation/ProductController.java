package pim.presentation;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.FlowPane;
import pim.business.Category;
import pim.business.Image;
import pim.business.PIM;
import pim.business.Product;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Kasper
 */
public class ProductController implements Initializable {

	@FXML
	private FlowPane productImagePane;

	private PIM pim;

	private TreeItem<String> root = new TreeItem<>("root");

	@FXML
	private TreeView<String> productTreeView;

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {

		Image img = new Image("res/omen-ax005no.jpg");
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));
		productImagePane.getChildren().add(new RemoveableImage(img.getImage(), this::removeImage));


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
	 * Used to populate the TreeView that makes it possible to choose the different products
	 * available in the PIM for modification
	 */
	private void populateTreeView() {
		for (Category category : pim.getCategories()) {
			TreeItem<String> categoryBranch = new TreeItem<>(category.getName());
			root.getChildren().add(categoryBranch);
			for (Product product : pim.getProducts(category.getName())) {
				TreeItem<String> productBranch = new TreeItem<>(product.getName());
				categoryBranch.getChildren().add(productBranch);
			}
		}
		root.setExpanded(true);
		productTreeView.setRoot(root);
		productTreeView.setShowRoot(false);
	}

	/**
	 * Execute this when the product tab is selected
	 */
	public void onEnter() {
		populateTreeView();
	}

	private void removeImage(RemoveableImage img) {
		//TODO
		productImagePane.getChildren().remove(img);
	}
}
