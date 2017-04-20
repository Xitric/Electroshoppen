package pim.business;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A category is used to group products. A category can also have attributes, that the products in the category will
 * inherit. A product in a category must provide a value for all the attributes associated with that category.
 *
 * @author Mads
 * @author Kasper
 */
public class Category implements Iterator<Attribute>, Comparable<Category> {

	private final String name;
	private final Set<Attribute> attributes;
	private final Set<CategoryChangeListener> changeListeners;

	/**
	 * Constructs a new category with the specified name.
	 *
	 * @param name the name of the category
	 */
	public Category(String name) {
		this.name = name;
		this.attributes = new HashSet<>();
		this.changeListeners = new HashSet<>();
	}

	/**
	 * Constructs a new category with the specified name and set of attributes.
	 *
	 * @param name       the name of the category
	 * @param attributes the attributes of the category
	 */
	public Category(String name, Set<Attribute> attributes) {
		this.name = name;
		//We must ensure that the attribute set is not null (otherwise we cannot add new attributes), and that we copy
		//the specified array so that new attributes can only be added using the methods below
		this.attributes = (attributes == null ? new HashSet<>() : new HashSet<>(attributes));
		this.changeListeners = new HashSet<>();
	}

	/**
	 * Get the name of this category.
	 *
	 * @return the name of this category
	 */
	public String getName() {
		return name;
	}

	/**
	 * Add an attribute to this category.
	 *
	 * @param attribute the attribute to add
	 */
	public void addAttribute(Attribute attribute) {
		boolean success = attributes.add(attribute);

		if (success) {
			for (CategoryChangeListener listener : changeListeners) {
				listener.attributeAdded(attribute);
			}
		}
	}

	/**
	 * Remove an attribute from this category.
	 *
	 * @param attribute the attribute to remove
	 */
	public void removeAttribute(Attribute attribute) {
		boolean success = attributes.remove(attribute);

		if (success) {
			for (CategoryChangeListener listener : changeListeners) {
				listener.attributeRemoved(attribute);
			}
		}
	}

	/**
	 * Get the attributes of this category. This returns a copy of the internal set.
	 *
	 * @return the attributes of this category
	 */
	public Set<Attribute> getAttributes() {
		return new HashSet<>(attributes);
	}

	/**
	 * Add a change listener to this category.
	 *
	 * @param listener the listener to add
	 */
	public void addChangeListener(CategoryChangeListener listener) {
		changeListeners.add(listener);
	}

	/**
	 * Remove a change listener from this category.
	 *
	 * @param listener the listener to remove
	 */
	public void removeChangeListener(CategoryChangeListener listener) {
		changeListeners.remove(listener);
	}

	@Override
	public boolean hasNext() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void remove() {
		Iterator.super.remove(); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Attribute next() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public String toString() {
		return getName();
	}
	@Override
	public int compareTo(Category category) {
		return getName().compareTo(category.getName());
	}
}
