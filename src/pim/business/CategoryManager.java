package pim.business;

import java.util.HashMap;
import java.util.Set;

/**
 * Manages category creation and ensures no duplicates (same name) are made.
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
}
