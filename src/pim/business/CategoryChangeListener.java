package pim.business;

/**
 * Interface describing a class that can listen to changes on a category in terms of adding and removing attributes.
 *
 * @author Kasper
 */
public interface CategoryChangeListener {

	/**
	 * Called when an attribute has been added to the category.
	 *
	 * @param attribute the attribute that was added
	 */
	void attributeAdded(Attribute attribute);

	/**
	 * Called when an attribute has been removed from a category.
	 *
	 * @param attribute the attribute that was removed
	 */
	void attributeRemoved(Attribute attribute);
}
