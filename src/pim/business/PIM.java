package pim.business;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

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
	 * @throws IOException if the operation failed
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

	/**
	 * To retrieve an image with the given url.
	 *
	 * @param url the url of the image
	 * @return the image with the specified url
	 * @throws IOException if the operation failed
	 */
	BufferedImage getImage(String url) throws IOException;

	/**
	 * Add image with the given ulr
	 * @param url the url of the image
	 */
	void addImage(String url);

	/**
	 * Remove image with the given url
	 *
	 * @param url the url of the image
	 * @throws IOException if the operation failed
	 */
	void removeImage(String url) throws IOException;

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

	Category getCategory (String categoryName);
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
	 * Remove an attribute from the selected category
	 *
	 * @param categoryName name of the selected category
	 * @throws IOException if the operation failed
	 */
	void deleteAttributeFromCategory(Category categoryName, Attribute attributeName) throws IOException;

	void addAttributeToCategory (Category categoryName, Attribute attributeName) throws  IOException;

	void addCategory (String categoryName);

	void removeCategory(String categoryName);


}