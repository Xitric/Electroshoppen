package pim.business;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

/**
 * Manages category creation and ensures no duplicates (same name) are made.
 *
 * @author Mikkel
 * @author Kasper
 * @author Emil
 */
class CategoryManager {

	private final PIMPersistenceFacade persistence;
	private HashMap<String, Category> categories;

	/**
	 * Constructs a new category manager.
	 *
	 * @param persistence the persistence facade
	 */
	public CategoryManager(PIMPersistenceFacade persistence) {
		categories = new HashMap<>();
		this.persistence = persistence;
	}

	/**
	 * Constructs a category if one with the given name does not exist already. Otherwise a reference to the existing
	 * category will be returned.
	 *
	 * @param name name of the category
	 * @return a reference to the created/existing category with the given name
	 */
	public Category constructCategory(String name) {
		return categories.computeIfAbsent(name, Category::new);
	}

	/**
	 * Constructs a category if one with the given name does not exist already. Otherwise a reference to the existing
	 * category will be returned.
	 *
	 * @param name       name of the category
	 * @param attributes the attributes of the category
	 * @return a reference to the created/existing category with the given name
	 */
	public Category constructCategory(String name, Set<Attribute> attributes) {
		Category c;

		if (categories.get(name) == null) {
			c = new Category(name, attributes);
			categories.put(name, c);
		} else {
			c = categories.get(name);
			c.setAttributes(attributes);
		}

		return c;
	}

	/**
	 * Create a new category with the specified name in the PIM, if no such category already exists. This category will
	 * automatically be saved.
	 *
	 * @param name the name of the category
	 * @return the new category
	 * @throws IOException              if something goes wrong
	 * @throws IllegalArgumentException if a category with the specified name already exists
	 */
	public Category createCategory(String name) throws IOException {
		//Ensure that the category does not exist - if it is loaded, we can save a trip to the db
		if (categories.containsKey(name) || persistence.getCategoryByName(name) != null) {
			throw new IllegalArgumentException("A category with the name " + name + " already exists!");
		}

		//Category guaranteed to not exist, so make it
		Category category = new Category(name);
		persistence.saveCategory(category);
		categories.put(name, category); //Add to memory only is save is successful
		return category;
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
	 * Get the category with the specified name.
	 *
	 * @param categoryName the name of the category
	 * @return the category with the specified name, or null if no such category could be retrieved
	 * @throws IOException if something goes wrong
	 */
	public Category getCategory(String categoryName) throws IOException {
		return persistence.getCategoryByName(categoryName);
	}

	/**
	 * Get the instance of the category with the specified name if it is currently in memory. Otherwise, this method
	 * will return null.
	 *
	 * @param name the name of the category
	 * @return the category with the specified name, or null if it is not in memory
	 */
	public Category getCategoryIfLoaded(String name) {
		return categories.get(name);
	}

	/**
	 * Remove the specified attribute name from the currently loaded categories.
	 *
	 * @param attribute the attribute to remove
	 */
	public void removeAttributeFromCategories(Attribute attribute) {
		for (Category c : categories.values()) {
			c.removeAttribute(attribute);
		}
	}

	/**
	 * Save the specified category.
	 *
	 * @param category the category to save
	 * @throws IOException if something goes wrong
	 */
	public void saveCategory(Category category) throws IOException {
		persistence.saveCategory(category);
	}

	/**
	 * Delete the category with the specified name.
	 *
	 * @param categoryName the name of the category
	 * @throws IOException if something goes wrong
	 */
	public void deleteCategory(String categoryName) throws IOException {
		persistence.deleteCategory(categoryName);
		categories.remove(categoryName);
	}
}
