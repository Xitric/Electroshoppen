package pim.business;

import pim.persistence.BufferedImageSerializable;
import pim.persistence.PersistenceFacade;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages loading products from the persistence layer and storing them in memory for faster retrieval. This manager
 * ensures that only one instance of any product is made.
 *
 * @author Kasper
 */
public class ProductManager {

	private final Map<Integer, Product> products;
	private final PersistenceFacade persistence;
	private HashMap<BufferedImage, Image> images;

	/**
	 * Constructs a new product manager.
	 *
	 * @param persistence the persistence facade
	 */
	public ProductManager(PersistenceFacade persistence) {
		products = new HashMap<>();
		images = new HashMap<>();
		this.persistence = persistence;
	}

	/**
	 * Creates a product if one with the given id does not exist already. Otherwise a reference to the existing product
	 * will be returned.
	 *
	 * @param id          the id of the product
	 * @param name        the name of the product
	 * @param description the description of the product
	 * @param price       the price of the product
	 * @return the created product
	 */
	public Product createProduct(int id, String name, String description, double price) {
		return products.computeIfAbsent(id, i -> new Product(id, name, description, price));
	}

	/**
	 * Get a set of all products.
	 *
	 * @return a set of all products
	 * @throws IOException if something goes wrong
	 */
	public Set<Product> getProducts() throws IOException {
		return persistence.getProducts();
	}

	/**
	 * Get a set of all products currently in memory.
	 *
	 * @return a set of all products in memory
	 */
	public Set<Product> getLoadedProducts() {
		return new HashSet<>(products.values());
	}

	/**
	 * Get the product with the specified id. If the product is not in memory, it will be loaded from the
	 * persistence layer.
	 *
	 * @param productID the id of the product
	 * @return the product with the specified id, or null if no such product could be retrieved
	 * @throws IOException if something goes wrong
	 */
	public Product getProduct(int productID) throws IOException {
		//Look in memory first
		Product p = products.get(productID);

		//If this failed, look in persistence. This might also fail, leaving p as null
		if (p == null) {
			p = persistence.getProductByID(productID);
		}

		return p;
	}

	/**
	 * Get the product with the specified id in memory.
	 *
	 * @param productID the id of the product
	 * @return the product with the specified id, or null if no such product could be retrieved from memory
	 */
	public Product getLoadedProduct(int productID) {
		return products.get(productID);
	}

	/**
	 * Get the products in the specified category.
	 *
	 * @param categoryName the name of the category
	 * @return the products in the specified category
	 * @throws IOException if something goes wrong
	 */
	public Set<Product> getProductsByCategory(String categoryName) throws IOException {
		//We cannot know how many products are in the category, so we need to retrieve all products in the category from
		//the persistence layer
		return persistence.getProductsByCategory(categoryName);
	}

	/**
	 * Get the products in the specified category that are currently in memory.
	 *
	 * @param category the category
	 * @return the products in the specified category
	 */
	public Set<Product> getLoadedProductsByCategory(Category category) {
		return products.values().stream()
				.filter(product -> product.hasCategory(category)).collect(Collectors.toSet());
	}

	/**
	 * Save the information about the specified product in the database.
	 *
	 * @param product the product to save
	 * @throws IOException if something goes wrong
	 */
	public void saveProduct(Product product) throws IOException {
		persistence.saveProduct(product);
		products.put(product.getID(), product);
	}

	/**
	 * Save the information about all the specified products in the database.
	 *
	 * @param productCollection the products to save
	 * @throws IOException if something goes wrong
	 */
	public void saveProducts(Collection<Product> productCollection) throws IOException {
		persistence.saveProducts(productCollection);

		for (Product product : productCollection) {
			products.put(product.getID(), product);
		}
	}

	/**
	 * Creates an image or returns the existing one with the same url if it already exists.
	 *
	 * @return the created image object
	 */
	public Image createImage(BufferedImage img) {
		return images.computeIfAbsent(img, Image::new);
	}

	/**
	 * Removes an image from the list of images.
	 *
	 * @param url the url of the image to remove
	 */
	public void removeImage(String url) {
		//TODO: Free from memory if all references are gone
		images.remove(url);
	}
}
