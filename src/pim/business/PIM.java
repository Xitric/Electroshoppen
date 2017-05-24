package pim.business;

import shared.Image;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Interface describing the functionality that must be provided by all PIM implementations.
 *
 * @author Kasper
 * @author Emil
 */
public interface PIM {

	/**
	 * Synchronize the PIM with the information in the external ERP system. This is only possible if a connection can be
	 * established. Products that are already in the PIM are updated, while products that are not in the PIM, are
	 * created.
	 *
	 * @return true if the synchronize was successful, false otherwise
	 */
	boolean synchronize();

	/**
	 * To retrieve information from the PIM for a product.
	 *
	 * @param id the id of the product to retrieve
	 * @return the product with the specified id, or null if no such product could be retrieved
	 * @throws IOException if the operation failed
	 */
	Product getProductInformation(int id) throws IOException;

	Set<Product> getPopularProducts(int amount) throws IOException;

	/**
	 * Create an image from the specified url.
	 *
	 * @param url the url of the image
	 */
	Image createImage(String url) throws IOException;

	/**
	 * To retrieve products from a specific category
	 *
	 * @param categoryName the category to get products from
	 * @return a list of Products, or null if the operation failed
	 * @throws IOException if the operation failed
	 */
	List<Product> getProducts(String categoryName) throws IOException;

	/**
	 * Get a list of all products in the PIM.
	 *
	 * @return a list of all products
	 * @throws IOException if the operation failed
	 */
	List<Product> getProducts() throws IOException;

	/**
	 * Save the specified product.
	 *
	 * @param product the product to save
	 * @throws IOException if the operation failed
	 */
	void saveProduct(Product product) throws IOException;

	/**
	 * To remove an attribute from the PIM. When removing an attribute it will also be removed from all the products and
	 * related categories.
	 *
	 * @param attributeID the attribute to remove
	 * @throws IOException if the operation failed
	 */
	void removeAttribute(int attributeID) throws IOException;

	/**
	 * Save the specified attribute in the PIM.
	 *
	 * @param attribute the attribute to save
	 * @throws IOException if the operation failed
	 */
	void saveAttribute(Attribute attribute) throws IOException;

	/**
	 * Create a new attribute with the specified values.
	 *
	 * @param name         the name of the attribute
	 * @param defaultValue the default value of the attribute
	 * @param legalValues  allowed values for the attribute
	 * @throws IOException if the operation failed
	 */
	Attribute createAttribute(String name, Object defaultValue, Set<Object>legalValues) throws IOException;

	/**
	 * Get a list of all attributes from the PIM.
	 *
	 * @return a list of all attributes, or null if the operation failed
	 * @throws IOException if the operation failed
	 */
	List<Attribute> getAttributes() throws IOException;

	/**
	 * To retrieve an attribute.
	 *
	 * @param attributeID the id of the attribute
	 * @return the given attribute, or null if no such attribute could be retrieved
	 * @throws IOException if the operation failed
	 */
	Attribute getAttribute(int attributeID) throws IOException;

	/**
	 * To retrieve all currently loaded categories with the given attribute.
	 *
	 * @param attributeID the id of the attribute
	 * @return a list of categories with the attribute
	 * @throws IOException if the operation failed
	 */
	List<Category> getCategoriesWithAttribute(int attributeID) throws IOException;

	/**
	 * Get a list of all categories from the PIM.
	 *
	 * @return a list of all categories, or null if the operation failed
	 * @throws IOException if the operation failed
	 */
	List<Category> getCategories() throws IOException;

	/**
	 * Get the category with the specified name.
	 *
	 * @param categoryName the name of the category
	 * @return the category with the specified name
	 * @throws IOException if the operation failed
	 */
	Category getCategory(String categoryName) throws IOException;

	/**
	 * Get all the attributes for the selected category
	 *
	 * @param categoryName name of the selected category
	 * @throws IOException if the operation failed
	 */
	List<Attribute> getAttributesFromCategory(String categoryName) throws IOException;

	/**
	 * Get all the attributes that are not in the selected category
	 *
	 * @param categoryName name of the selected category
	 * @throws IOException if the operation failed
	 */
	List<Attribute> getAttributesNotInTheCategory(String categoryName) throws IOException;

	/**
	 * Save the specified category.
	 *
	 * @param category the category to save
	 * @throws IOException if the operation failed
	 */
	void saveCategory(Category category) throws IOException;

	/**
	 * Create a new category with the specified name, if such a category does not already exist.
	 *
	 * @param categoryName the name of the category
	 * @throws IOException if the operation failed
	 * @throws IllegalArgumentException if a category with the specified name already exists
	 */
	Category createCategory(String categoryName) throws IOException;

	/**
	 * Remove the specified category from the PIM.
	 *
	 * @param categoryName the name of the category
	 * @throws IOException if the operation failed
	 */
	void removeCategory(String categoryName) throws IOException;

	/**
	 * Create a new tag
	 * @param name name of the tag
	 * @return the tag that was created
	 */
	Tag createTag(String name);
}