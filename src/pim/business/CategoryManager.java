package pim.business;

import pim.persistence.PersistenceMediator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Manages category creation and ensures no duplicates (same name) are made. Also acts as the central storage of all
 * categories currently in memory.
 *
 * @author mstruntze
 * @author Kasper
 */
public class CategoryManager {

	private final PersistenceMediator persistence;
	private HashMap<String, Category> categories;

	/**
	 * Constructs a new category manager.
	 *
	 * @param persistence the persistence mediator
	 */
	public CategoryManager(PersistenceMediator persistence) {
		categories = new HashMap<>();
		this.persistence = persistence;
	}

	/**
	 * Creates a category if one with the given name does not exist already.
	 * Otherwise a reference to the existing category will be returned.
	 *
	 * @param name Name of the category.
	 * @return Returns a reference to the create/existing category with the given name.
	 */
	public Category createCategory(String name) {
		return categories.computeIfAbsent(name, Category::new);
	}

	/**
	 * Creates a category if one with the given name does not exist already.
	 * Otherwise a reference to the existing category will be returned.
	 *
	 * @param name       Name of the category
	 * @param attributes The attributes of the category.
	 * @return Returns a reference to the create/existing category with the given name.
	 */
	public Category createCategory(String name, Set<Attribute> attributes) {
		return categories.computeIfAbsent(name, n -> new Category(n, attributes));
	}

	/**
	 * Get a set of all categories.
	 *
	 * @return a set of all categories
	 * @throws IOException if something goes wrong
	 */
	public Set<Category> getCategories() throws IOException {
		return persistence.getCategories();
	}

	/**
	 * Get the category with the specified name. If the category is not in memory, it will be loaded from the
	 * persistence layer.
	 *
	 * @param categoryName the name of the category
	 * @return the category with the specified name, or null if no such category could be retrieved
	 * @throws IOException if something goes wrong
	 */
	public Category getCategory(String categoryName) throws IOException {
		//Look in memory first
		Category c = categories.get(categoryName);

		//If this failed, look in persistence. This might also fail, leaving c as null
		if (c == null) {
			c = persistence.getCategoryByName(categoryName);
		}

		return c;
	}

	/**
	 * Get the set of all categories with the specified attribute in memory. Categories that are not in memory are not
	 * considered.
	 *
	 * @param attribute the attribute to filter by
	 * @return the categories with the specified attribute currently in memory
	 */
	public Set<Category> getLoadedCategoriesWithAttribute(Attribute attribute) {
		return categories.values().stream()
				.filter(category -> category.hasAttribute(attribute)).collect(Collectors.toSet());
	}
}
