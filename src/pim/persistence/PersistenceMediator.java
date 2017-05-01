package pim.persistence;

import pim.business.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

/**
 * Interface describing an object that can be used to interacting with the persistence layer.
 *
 * @author Kasper
 */
public interface PersistenceMediator {

	/**
	 * Get the product with the specified id.
	 *
	 * @param id the id of the product
	 * @return the product with the specified id, or null if no such product exists
	 * @throws IOException if something goes wrong
	 */
	Product getProductByID(String id) throws IOException;

	/**
	 * Get a set of all products with the specified name
	 *
	 * @param name the name of the products
	 * @return a set of all products with the specified name
	 * @throws IOException if something goes wrong
	 */
	Set<Product> getProductsByName(String name) throws IOException;

	/**
	 * Get a set of all products in the specified category stored in the database.
	 *
	 * @param category the name of the category
	 * @return a set of all products in the specified category stored in the database
	 * @throws IOException if something goes wrong
	 */
	public Set<Product> getProductsByCategory(String category) throws IOException;

	/**
	 * Gets all products with the associated tag.
	 *
	 * @param tag the tag to find products by
	 * @return a set of products with the provided tag
	 * @throws IOException if something goes wrong
	 */
	Set<Product> getProductsByTag(Tag tag) throws IOException;

	/**
	 * Get a set of all products.
	 *
	 * @return a set of all products
	 * @throws IOException if something goes wrong
	 */
	Set<Product> getProducts() throws IOException;

	/**
	 * Save the specified product. This will overwrite any existing data.
	 *
	 * @param product the product to save
	 */
	void saveProduct(Product product);

	/**
	 * Save the specified products. This will overwrite any existing data.
	 *
	 * @param products the products to save
	 */
	void saveProducts(Collection<Product> products);

	/**
	 * Delete all data for the product with the specified id.
	 *
	 * @param id the id of the product
	 */
	void deleteProduct(String id);

	/**
	 * Get a set of all tags.
	 *
	 * @return the set of all tags
	 * @throws IOException if something goes wrong
	 */
	Set<Tag> getTags() throws IOException;

	/**
	 * Save the specified tag. This will overwrite any existing data.
	 *
	 * @param tag the tag to save
	 */
	void saveTag(Tag tag);

	/**
	 * Delete all data for the tag with the specified name.
	 *
	 * @param name the name of the tag
	 */
	void deleteTag(String name);

	/**
	 * Get all the images stored for the specified product.
	 *
	 * @param productID the id of the product
	 * @return a set of all the images for the specified product
	 * @throws IOException if something goes wrong
	 */
	Set<Image> getImagesForProduct(String productID) throws IOException;

	/**
	 * Get the category with the specified name.
	 *
	 * @param name the name of the category
	 * @return the category with the specified name, or null if no such category exists
	 * @throws IOException if something goes wrong
	 */
	Category getCategoryByName(String name) throws IOException;

	/**
	 * Get a set of all categories.
	 *
	 * @return a set of all categories
	 * @throws IOException if something goes wrong
	 */
	Set<Category> getCategories() throws IOException;

	/**
	 * Save the specified category. This will overwrite any existing data.
	 *
	 * @param category the category to save
	 */
	void saveCategory(Category category);

	/**
	 * Save the specified categories. This will overwrite any existing data.
	 *
	 * @param categories the categories to save
	 */
	void saveCategories(Collection<Category> categories);

	/**
	 * Delete all data for the category with the specified name.
	 *
	 * @param name the name of the category
	 */
	void deleteCategory(String name);

	/**
	 * Get the attribute with the specified id.
	 *
	 * @param id the id of the attribute
	 * @return the attribute with the specified id, or null if no such attribute exists
	 * @throws IOException if something goes wrong
	 */
	Attribute getAttributeByID(String id) throws IOException;

	/**
	 * Get a set of all attributes.
	 *
	 * @return a set of all attributes
	 * @throws IOException if something goes wrong
	 */
	Set<Attribute> getAttributes() throws IOException;

	/**
	 * Save the specified attribute. This will overwrite any existing data.
	 *
	 * @param attribute the attribute to save
	 */
	void saveAttribute(Attribute attribute);

	/**
	 * Save the specified attributes. This will overwrite any existing data.
	 *
	 * @param attributes the attributes to save
	 */
	void saveAttributes(Collection<Attribute> attributes);

	/**
	 * Delete all data for the attribute with the specified id.
	 *
	 * @param id the id of the attribute
	 */
	void deleteAttribute(String id);

	/**
	 * Set the cache used by the persistence layer. This will delete all data in the current cache.
	 *
	 * @param cache the data cache
	 */
	void setCache(DataCache cache);

	/**
	 * Dispose this persistence mediator, closing all currently open connections.
	 */
	void dispose();
}
