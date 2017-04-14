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
public class Category implements Iterator<Attribute> {

	private final String name;
	private final Set<Attribute> attributes;

	/**
	 * Constructs a new category with the specified name.
	 *
	 * @param name the name of the category
	 */
	public Category(String name) {
		this.name = name;
		this.attributes = new HashSet<>();
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
	}

	/**
	 * Get the name of this category.
	 *
	 * @return the name of this category
	 */
	public String getName() {
		return name;
	}

	//TODO: Notify products using observer pattern
	public void removeAttribute(Attribute attribute) {
		attributes.remove(attribute);
	}

	public void addAttribute(Attribute attribute) {
		attributes.add(attribute);
	}

	/**
	 * Get the attributes of this category. This returns a copy of the internal set.
	 *
	 * @return the attributes of this category
	 */
	public Set<Attribute> getAttributes() {
		return new HashSet<>(attributes);
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

}
