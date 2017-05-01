package pim.business;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A business entity representation of a product in the PIM.
 *
 * @author Niels
 * @author Kasper
 */
public class Product implements CategoryChangeListener {

	private final String id;
	private String name;
	private double price;
	private Set<Category> categories;
	private Set<Attribute.AttributeValue> attributes;
	private Set<Tag> tags;
	private Set<Image> images;

	/**
	 * Constructs a new product.
	 *
	 * @param id    the id of the product
	 * @param name  the name of the product
	 * @param price the price of the product
	 */
	public Product(String id, String name, double price) {
		this.id = id;
		this.name = name;
		this.price = price;

		//Initialize lists
		categories = new HashSet<>();
		attributes = new HashSet<>();
		images = new HashSet<>();
		tags = new HashSet<>();
	}

	/**
	 * Get the id of this product.
	 *
	 * @return the id of this product
	 */
	public String getID() {
		return id;
	}

	/**
	 * Get the name of this product.
	 *
	 * @return the name of this product
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of this product.
	 *
	 * @param name the new name of this product
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the price of this product.
	 *
	 * @return the price of this product
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * Set the price of this product.
	 *
	 * @param price the price of this product
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * Add a category to this product. Adding the same category twice will have no effect. When adding a new category,
	 * default attribute values will be added for all new attributes.
	 *
	 * @param category the category to add
	 */
	public void addCategory(Category category) {
		boolean categoryWasNew = categories.add(category);
		if (!categoryWasNew) return;

		//Listen to category
		category.addChangeListener(this);

		//When adding a new category, add attribute values (default values) for all new attributes
		Set<Attribute> newAttributes = category.getAttributes();
		Set<Attribute> allAttributes = this.getAllAttributes();
		for (Attribute attrib : newAttributes) {
			if (!allAttributes.contains(attrib)) {
				attributes.add(attrib.createValue());
			}
		}
	}

	/**
	 * Remove a category from this product. Removing a category will also remove all attribute values for those
	 * attributes that were removed from the product.
	 *
	 * @param category the category to remove
	 */
	public void removeCategory(Category category) {
		boolean categoryWasRemoved = categories.remove(category);
		if (!categoryWasRemoved) return;

		//Stop listening to category
		category.removeChangeListener(this);

		//Remove attribute values that are no longer valid for this product
		Set<Attribute> allAttributes = this.getAllCategoryAttributes();
		attributes.removeIf(attributeValue -> !allAttributes.contains(attributeValue.getParent()));
	}

	/**
	 * Get the categories of this product. This will return a copy of the internal collection.
	 *
	 * @return the categories of this product
	 */
	public List<Category> getCategories() {
		return new ArrayList<>(categories);
	}

	/**
	 * Test whether this product has the specified category.
	 *
	 * @param category the category to test for
	 * @return true if this product has the category, false otherwise
	 */
	public boolean hasCategory(Category category) {
		return categories.contains(category);
	}

	/**
	 * Set the value for the specified attribute. If none of the product's categories contain this attribute nothing
	 * will happen.
	 *
	 * @param attribute the attribute to set the value for
	 * @param value     the value to set
	 */
	public void setAttribute(Attribute attribute, Object value) {
		//Remove the existing value for this attribute
		boolean attributeExists = attributes.removeIf(attributeValue -> attributeValue.getParent() == attribute);
		if (!attributeExists) return;

		//Add new value
		attributes.add(attribute.createValue(value));
	}

	/**
	 * Get the values for all the attributes of this product. This will return a copy of the internal collection.
	 *
	 * @return the values for all the attributes of this product
	 */
	public List<Attribute.AttributeValue> getAttributeValues() {
		return new ArrayList<>(attributes);
	}

	/**
	 * Get a set containing all the attributes currently registered on this product.
	 *
	 * @return the set of all the product's attributes
	 */
	private Set<Attribute> getAllAttributes() {
		Set<Attribute> attribs = new HashSet<>();
		for (Attribute.AttributeValue a : attributes) {
			attribs.add(a.getParent());
		}
		return attribs;
	}

	/**
	 * Get a set containing all the attributes of the categories of this product.
	 *
	 * @return the set of all the product's attributes
	 */
	private Set<Attribute> getAllCategoryAttributes() {
		Set<Attribute> attribs = new HashSet<>();
		for (Category c : categories) {
			attribs.addAll(c.getAttributes());
		}
		return attribs;
	}

	/**
	 * Add a new tag to this product.
	 *
	 * @param tag the tag to add
	 */
	public void addTag(Tag tag) {
		tags.add(tag);
	}

	/**
	 * Remove a tag from this product.
	 *
	 * @param tag the tag to remove
	 * @return true if this product contained the tag
	 */
	public boolean removeTag(Tag tag) {
		return tags.remove(tag);
	}

	/**
	 * Get the set of tags for this product. This returns a copy of the internal set.
	 *
	 * @return the set of tags
	 */
	public Set<Tag> getTags() {
		return new HashSet<>(tags);
	}

	/**
	 * Test whether this product contains the specified tag.
	 *
	 * @param tag the tag to test for
	 * @return true if this product contains the tag, false otherwise
	 */
	public boolean containsTag(Tag tag) {
		return tags.contains(tag);
	}

	/**
	 * Add an image to this product.
	 *
	 * @param image the image to add
	 */
	public void addImage(Image image) {
		this.images.add(image);
	}

	/**
	 * Remove an image from this product.
	 *
	 * @param image the image to remove
	 */
	public void removeImage(Image image) {
		this.images.remove(image);
	}

	/**
	 * Get the set of images for this product. This returns a copy of the internal set.
	 *
	 * @return the set of images
	 */
	public Set<Image> getImages() {
		return new HashSet<>(images);
	}

	@Override
	public void attributeAdded(Attribute attribute) {
		//Ensure that the attribute is not already present
		if (!getAllAttributes().contains(attribute)) {
			setAttribute(attribute, attribute.getDefaultValue());
		}
	}

	@Override
	public void attributeRemoved(Attribute attribute) {
		//Ensure that the attribute is not present in another category before removing
		if (!getAllCategoryAttributes().contains(attribute)) {
			attributes.removeIf(attributeValue -> attributeValue.getParent() == attribute);
		}
	}
}
