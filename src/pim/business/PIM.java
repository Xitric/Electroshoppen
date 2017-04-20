package pim.business;

import java.awt.image.BufferedImage;
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
	 */
	boolean synchronize();

	/**
	 * To retrieve information from the PIM for a product.
	 *
	 * @param id the id of the product to retrieve
	 * @return the product with the specified id
	 */
	Product getProductInformation(int id);

	/**
	 * To retrieve an image with the given id.
	 *
	 * @param id the id of the image
	 * @return the image with the specified id
	 */
	BufferedImage getMediaInformation(int id);

	/**
	 * To retrieve products from a specific category
	 *
	 * @param categoryName the category to get products from
	 * @return a list of Products
	 */
	List<Product> getProducts(String categoryName);

	/**
	 * To remove an attribute from the PIM. When removing an attribute it will also be removed from all the products and
	 * related categories.
	 *
	 * @param attributeName the attribute to remove
	 */
	void removeAttribute(String attributeName);

	/**
	 * Get a list of all attributes from the PIM.
	 *
	 * @return a list of all attributes
	 */
	List<Attribute> getAttributes();

	/**
	 * To retrieve an attribute.
	 *
	 * @param attributeName the name of the attribute
	 * @return the given attribute
	 */
	Attribute getAttribute(String attributeName);

	/**
	 * To retrieve all categories with the given attribute.
	 *
	 * @param attributeName the name of the attribute
	 * @return a list of Categories with the attribute name.
	 */
	List<Category> getCategoriesWithAttribute(String attributeName);
        
        /**
	 * Get a list of all categories from the PIM.
	 *
	 * @return a list of all categories.
	 */
        List<Category> getCategories();
        
}