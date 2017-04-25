package pim.business;

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

	private HashMap<String, Category> categories;

	/**
	 * Constructs a new category manager.
	 */
	public CategoryManager() {
		categories = new HashMap<>();
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
	 * Get the set of all categories with the specified attribute.
	 *
	 * @param attribute the attribute to filter by
	 * @return the categories with the specified attribute
	 */
	public Set<Category> getCategoriesWithAttribute(Attribute attribute) {
		return categories.values().stream()
				.filter(category -> category.hasAttribute(attribute)).collect(Collectors.toSet());
	}
}
