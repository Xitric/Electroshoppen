package pim.business;

import pim.persistence.PersistenceMediator;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Manages loading products from the persistence layer and storing them in memory for faster retrieval. This manager
 * ensures that only one instance of any product is made.
 *
 * @author Kasper
 */
public class ProductManager {

	private final Map<Integer, Product> products;
	private final PersistenceMediator persistence;

	/**
	 * Constructs a new product manager.
	 *
	 * @param persistence the persistence mediator
	 */
	public ProductManager(PersistenceMediator persistence) {
		products = new HashMap<>();
		this.persistence = persistence;
	}

	/**
	 * Creates a product if one with the given id does not exist already. Otherwise a reference to the existing product
	 * will be returned.
	 *
	 * @param id    the id of the product
	 * @param name  the name of the product
	 * @param price the price of the product
	 * @return the created product
	 */
	public Product createProduct(int id, String name, double price) {
		return products.computeIfAbsent(id, i -> new Product(id, name, price));
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
	 */
	public void saveProduct(Product product) {
		persistence.saveProduct(product);
	}
}
