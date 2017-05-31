package pim.business;

import pim.persistence.DataCache;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

/**
 * Interface describing a class that can be used for accessing the persistence layer of the PIM.
 *
 * @author Kasper
 * @author Niels
 */
public interface PIMPersistenceFacade {

	/**
	 * Get the product with the specified id.
	 *
	 * @param id the id of the product
	 * @return the product with the specified id
	 * @throws IOException if the operation fails
	 */
	Product getProductByID(int id) throws IOException;

	/**
	 * Get the product with the specified name.
	 *
	 * @param name the name of the product
	 * @return the product with the specified name
	 * @throws IOException if the operation fails
	 */
	Set<Product> getProductsByName(String name) throws IOException;

	/**
	 * Get the products in the specified category.
	 *
	 * @param name the name of the category
	 * @return the products in the specified category
	 * @throws IOException if the operation fails
	 */
	Set<Product> getProductsByCategory(String name) throws IOException;

	/**
	 * Get the products with the specified tag.
	 *
	 * @param name the name of the tag
	 * @return the products with the specified tag
	 * @throws IOException if the operation fails
	 */
	Set<Product> getProductsByTag(String name) throws IOException;

	/**
	 * Get all the product reviews from the database
	 *
	 * @return set of ProductReviews
	 * @throws IOException if the operation fails
	 */
	Set<ProductReview> getProductReviews() throws IOException;

	/**
	 * Get all products.
	 *
	 * @return all products
	 * @throws IOException if the operation fails
	 */
	Set<Product> getProducts() throws IOException;

	/**
	 * Save the specified product. If the product has no id, a new one will be generated.
	 *
	 * @param product the product to save
	 * @throws IOException if the operation fails
	 */
	void saveProduct(Product product) throws IOException;

	/**
	 * Save the specified products. If any of the products have no ids, new ones will be generated.
	 *
	 * @param products the products to save
	 * @throws IOException if the operation fails
	 */
	void saveProducts(Collection<Product> products) throws IOException;

	/**
	 * Delete the product with the specified id.
	 *
	 * @param id the id of the product
	 * @throws IOException if the operation fails
	 */
	void deleteProduct(int id) throws IOException;

	/**
	 * Saves an image
	 */

	/**
	 * Get the category with the specified name.
	 *
	 * @param name the name of the category
	 * @return the category with the specified name
	 * @throws IOException if the operation fails
	 */
	Category getCategoryByName(String name) throws IOException;

	/**
	 * Get all categories.
	 *
	 * @return all categories
	 * @throws IOException if the operation fails
	 */
	Set<Category> getCategories() throws IOException;

	/**
	 * Save the specified category.
	 *
	 * @param category the category to save
	 * @throws IOException if the operation fails
	 */
	void saveCategory(Category category) throws IOException;

	/**
	 * Save the specified categories.
	 *
	 * @param categories the categories to save
	 * @throws IOException if the operation fails
	 */
	void saveCategories(Collection<Category> categories) throws IOException;

	/**
	 * Delete the category with the specified name.
	 *
	 * @param name the name of the category
	 * @throws IOException if the operation fails
	 */
	void deleteCategory(String name) throws IOException;

	/**
	 * Get the attribute with the specified id.
	 *
	 * @param id the id of the attribute
	 * @return the attribute with the specified id
	 * @throws IOException if the operation fails
	 */
	Attribute getAttributeByID(int id) throws IOException;

	/**
	 * Get all attributes.
	 *
	 * @return all attributes
	 * @throws IOException if the operation fails
	 */
	Set<Attribute> getAttributes() throws IOException;

	/**
	 * Save the specified attribute. If the attribute has no id, a new one will be generated.
	 *
	 * @param attribute the attribute to save
	 * @throws IOException if the operation fails
	 */
	void saveAttribute(Attribute attribute) throws IOException;

	/**
	 * Save the specified attributes. If any of the attributes have no ids, new ones will be generated.
	 *
	 * @param attributes the attributes to save
	 * @throws IOException if the operation fails
	 */
	void saveAttributes(Collection<Attribute> attributes) throws IOException;

	/**
	 * Delete the attribute with the specified id.
	 *
	 * @param id the id of the attribute
	 * @throws IOException if the operation fails
	 */
	void deleteAttribute(int id) throws IOException;

	/**
	 * Get the tag with the specified name.
	 *
	 * @param name the name of the tag
	 * @return the tag with the specified name
	 * @throws IOException if the operation fails
	 */
	Tag getTag(String name) throws IOException;

	/**
	 * Get all tags.
	 *
	 * @return all tags
	 * @throws IOException if the operation fails
	 */
	Set<Tag> getTags() throws IOException;

	/**
	 * Save the specified tag.
	 *
	 * @param tag the tag to save
	 * @throws IOException if the operation fails
	 */
	void saveTag(Tag tag) throws IOException;

	/**
	 * Save the specified tags.
	 *
	 * @param tags the tags to save
	 * @throws IOException if the operation fails
	 */
	void saveTags(Collection<Tag> tags) throws IOException;

	/**
	 * Delete the tag with the specified name.
	 *
	 * @param name the name of the tag
	 * @throws IOException if the operation fails
	 */
	void deleteTag(String name) throws IOException;

	/**
	 * Get the images for the specified product.
	 *
	 * @param id the id of the product
	 * @return the images for the specified product
	 * @throws IOException if the operation fails
	 */
	Set<Image> getImagesForProduct(int id) throws IOException;

	/**
	 * Get all images.
	 *
	 * @return all images
	 * @throws IOException if the operation fails
	 */
	Set<Image> getImages() throws IOException;

	/**
	 * Save the specified image.
	 *
	 * @param image the image to save
	 * @throws IOException if the operation fails
	 */
	void saveImage(Image image) throws IOException;

	/**
	 * Save the specified images.
	 *
	 * @param images the images to save
	 * @throws IOException if the operation fails
	 */
	void saveImages(Collection<Image> images) throws IOException;

	/**
	 * Get the data cache used by this persistence facade.
	 *
	 * @return the data cache used by this persistence facade
	 */
	DataCache getCache();

	/**
	 * Set the data cache to be used by this facade.
	 *
	 * @param cache the data cache to use
	 */
	void setCache(DataCache cache);

	/**
	 * Dispose all resources used by this persistence facade. This includes closing all currently open connections.
	 */
	void dispose();
}
