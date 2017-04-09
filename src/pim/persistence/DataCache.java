package pim.persistence;

import pim.business.Attribute;
import pim.business.Category;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Class responsible for caching Categories, Attributes, and Tags read from the database. This is to ensure that only
 * one instance of each is ever constructed.
 *
 * @author Kasper
 */
class DataCache {

	private final Set<Category> categories;
	private final Set<Attribute> attributes;
//	private final Set<Tag> categories; TODO: Add tags

	/**
	 * Constructs a new data cache.
	 */
	public DataCache() {
		categories = new HashSet<>();
		attributes = new HashSet<>();
	}

	/**
	 * Add the specified category to this cache if it has not already been registered.
	 *
	 * @param category the category to register
	 */
	public void registerCategoryIfAbsent(Category category) {
		categories.add(category);
	}

	/**
	 * Add the specified categories to this cache if they have not already been registered.
	 *
	 * @param categories the categories to register
	 */
	public void registerCategoryIfAbsent(Collection<Category> categories) {
		this.categories.addAll(categories);
	}

	/**
	 * Get the category with the specified name if it has been registered. Otherwise this method returns null.
	 *
	 * @param name the name of the category
	 * @return the category with the specified name, or null if no such category has been registered
	 */
	public Category getCategoryIfPresent(String name) {
		for (Category c : categories) {
			if (c.getCategoryName().equals(name)) {
				return c;
			}
		}

		return null;
	}

	/**
	 * Add the specified attribute to this cache if it has not already been registered.
	 *
	 * @param attribute the attribute to register
	 */
	public void registerAttributeIfAbsent(Attribute attribute) {
		attributes.add(attribute);
	}

	/**
	 * Add the specified attributes to this cache if they have not already been registered.
	 *
	 * @param attributes the attributes to register
	 */
	public void registerAttributeIfAbsent(Collection<Attribute> attributes) {
		this.attributes.addAll(attributes);
	}

	/**
	 * Get the attribute with the specified id if it has been registered. Otherwise this method returns null.
	 *
	 * @param id the id of the attribute
	 * @return the attribute with the specified id, or null if no such attribute has been registered
	 */
	public Attribute getAttributeIfPresent(String id) {
		for (Attribute a : attributes) {
			if (a.getID().equals(id)) {
				return a;
			}
		}

		return null;
	}
}
