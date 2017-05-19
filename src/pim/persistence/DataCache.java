package pim.persistence;

import pim.business.*;
import shared.Image;

import java.awt.image.BufferedImage;
import java.util.Set;

/**
 * Interface describing an object to be used for caching data read from the database. This cache should both ensure
 * that only one instance of every object is created and allow for faster retrieval of cached objects.
 *
 * @author Kasper
 */
public interface DataCache {

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
	Product createProduct(int id, String name, String description, double price);

	/**
	 * Creates an attribute if one with the given id does not exist already. Otherwise a reference to the existing
	 * attribute will be returned.
	 *
	 * @param id           id of the attribute
	 * @param name         name of the attribute
	 * @param defaultValue default value of the attribute
	 * @return a reference to the created/existing attribute with the given id
	 */
	Attribute createAttribute(int id, String name, Object defaultValue);

	/**
	 * Creates an attribute if one with the given id does not exist already. Otherwise a reference to the existing
	 * attribute will be returned.
	 *
	 * @param id           id of the attribute
	 * @param name         name of the attribute
	 * @param defaultValue default value of the attribute
	 * @param legalValues  allowed values for the attribute
	 * @return a reference to the created/existing attribute with the given id
	 */
	Attribute createAttribute(int id, String name, Object defaultValue, Set<Object> legalValues);

	/**
	 * Creates a category if one with the given name does not exist already. Otherwise a reference to the existing
	 * category will be returned.
	 *
	 * @param name name of the category
	 * @return a reference to the create/existing category with the given name
	 */
	Category createCategory(String name);

	/**
	 * Creates a category if one with the given name does not exist already. Otherwise a reference to the existing
	 * category will be returned.
	 *
	 * @param name       name of the category
	 * @param attributes the attributes of the category.
	 * @return a reference to the create/existing category with the given name.
	 */
	Category createCategory(String name, Set<Attribute> attributes);

	/**
	 * Creates a tag or returns the existing one with the same name if it already exists.
	 *
	 * @param name name of the tag
	 * @return the created tag object
	 */
	public Tag createTag(String name);

	/**
	 * Creates an image or returns the existing one with the same url if it already exists.
	 *
	 * @param id  id of the image
	 * @param img the image data
	 * @return the created image object
	 */
	public Image createImage(int id, BufferedImage img);
}
