package pim.business;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Set;

/**
 * Interface describing the functionality that must be provided by all PIM implementations.
 *
 * @author Kasper
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
	 */
	Product getProductInformation(int id);

	/**
	 * To retrieve an image with the given url.
	 *
	 * @param url the url of the image
	 * @return the image with the specified url
	 */
	BufferedImage getImage(String url);

	/**
	 * Add image with the given ulr
	 * @param url the url of the image
	 */
	void addImage(String url);

	/**
	 * Remove image with the given url
	 * @param url the url of the image
	 */
	void removeImage(String url);

	/**
	 * To retrieve products from a specific category
	 *
	 * @param categoryName the category to get products from
	 * @return a list of Products, or null if the operation failed
	 */
	List<Product> getProducts(String categoryName);

	/**
	 * Get a list of all products in the PIM.
	 *
	 * @return a list of all products
	 */
	List<Product> getProducts();

	/**
	 * Creates and registers a new attribute in the pim.
	 *
	 * @param name         the name of the attribute
	 * @param defaultValue the default value of the attribute
	 * @param legalValues  the legal values of the attribute, or null if all values are allowed
	 * @return the id of the new attribute
	 */
	int addAttribute(String name, Object defaultValue, Set<Object> legalValues);

	/**
	 * To remove an attribute from the PIM. When removing an attribute it will also be removed from all the products and
	 * related categories.
	 *
	 * @param attributeID the attribute to remove
	 */
	void removeAttribute(int attributeID);

	/**
	 * Get a list of all attributes from the PIM.
	 *
	 * @return a list of all attributes, or null if the operation failed
	 */
	List<Attribute> getAttributes();

	/**
	 * To retrieve an attribute.
	 *
	 * @param attributeID the id of the attribute
	 * @return the given attribute, or null if no such attribute could be retrieved
	 */
	Attribute getAttribute(int attributeID);

	/**
	 * To retrieve all currently loaded categories with the given attribute.
	 *
	 * @param attributeID the id of the attribute
	 * @return a list of categories with the attribute
	 */
	List<Category> getCategoriesWithAttribute(int attributeID);

	/**
	 * Get a list of all categories from the PIM.
	 *
	 * @return a list of all categories, or null if the operation failed
	 */
	List<Category> getCategories();

	/**
	 * Get all the attributes for the selected category
	 *
	 * @param categoryName name of the selected category
	 */
	List<Attribute> getAttributesFromCategory(String categoryName);

	/**
	 * Get all the attributes that are not in the selected category
	 *
	 * @param categoryName name of the selected category
	 */
	List<Attribute> getAttributesNotInTheCategory(String categoryName);

	/**
	 * Remove an attribute from the selected category
	 *
	 * @param categoryName name of the selected category
	 */
	void deleteAttributeFromCategory(String categoryName);


}