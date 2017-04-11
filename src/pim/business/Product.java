package pim.business;

import java.util.*;

/**
 * A business entity representation of a product in the PIM.
 *
 * @author Niels
 * @author Kasper
 */
public class Product {

	private final String id;
	private String name;
	private double price;
	private Set<Category> categories;
	private Set<Attribute.AttributeValue> attributes;
//	private Set<Integer> images;
//	private List<Tag> tags; TODO: Add tags

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
//		images = new HashSet<>();
//		tags = new ArrayList<>(); TODO: Add tags
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
	 * Set the name of this product.
	 *
	 * @param name the new name of this product
	 */
	public void setName(String name) {
		this.name = name;
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
	 * Set the price of this product.
	 *
	 * @param price the price of this product
	 */
	public void setPrice(double price) {
		this.price = price;
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
	 * Add a category to this product. Adding the same category twice will have no effect. When adding a new category,
	 * default attrbute values will be added for all new attributes.
	 *
	 * @param category the category to add
	 */
	public void addCategory(Category category) {
		boolean categoryWasNew = categories.add(category);
		if (!categoryWasNew) return;

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

		//Remove attribute values that are no longer valid for this product
		Set<Attribute> allAttributes = this.getAllAttributes();
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
	 * Set the value for the specified attribute. If none of the product's categories contain this attribute nothing
	 * will happen.
	 *
	 * @param attribute the attribute to set the value for
	 * @param value     the value to set
	 */
	public void setAttribute(Attribute attribute, Object value) {
		//Remove the existing value for this attribute
		attributes.removeIf(attributeValue -> attributeValue.getParent() == attribute);

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

	//TODO: Images as wrappers
//	public void addImage(int image) {
//		images.add(image);
//	}
//
//	public ArrayList<Integer> getImages() {
//		return images;
//	}

	/**
	 * Get a set containing all the attributes of the categories of this product.
	 *
	 * @return the set of all the product's attributes
	 */
	private Set<Attribute> getAllAttributes() {
		Set<Attribute> attribs = new HashSet<>();
		for (Category c : categories) {
			attribs.addAll(c.getAttributes());
		}
		return attribs;
	}
}
