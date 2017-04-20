package pim.business;

import java.util.HashMap;
import java.util.Set;

/**
 * Manages category creation and ensures no duplicates (same name) are made.
 *
 * @author mstruntze
 */
public class CategoryManager {
	/**
	 * The singleton instance for the category manager.
	 */
	private static CategoryManager instance;

	private HashMap<String, Category> categories;

	/**
	 * Internal constructor.
	 */
	private CategoryManager() {
		categories = new HashMap<>();
	}

	/**
	 * Get the singleton instance for the category manager.
	 *
	 * @return the singleton instance.
	 */
	public static CategoryManager getInstance() {
		if (instance == null) {
			instance = new CategoryManager();
		}

		return instance;
	}

	/**
	 * Creates a category if one with the given name does not exist already.
	 * Otherwise a reference to the existing category will be returned.
	 *
	 * @param name Name of the category.
	 * @return Returns a reference to the create/existing category with the given name.
	 */
	public Category createCategory(String name) {
		if(!categories.containsKey(name)) {
			Category cat = new Category(name);
			categories.put(name, cat);
			return cat;
		}

		return categories.get(name);
	}

	/**
	 * Creates a category if one with the given name does not exist already.
	 * Otherwise a reference to the existing category will be returned.
	 *
	 * @param name Name of the category
	 * @param attributes The attributes of the category.
	 * @return Returns a reference to the create/existing category with the given name.
	 */
	public Category createCategory(String name, Set<Attribute> attributes) {
		if(!categories.containsKey(name)) {
			Category cat = new Category(name, attributes);
			categories.put(name, cat);
			return cat;
		}

		return categories.get(name);
	}
}
