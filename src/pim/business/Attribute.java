package pim.business;

import java.util.HashSet;
import java.util.Set;

/**
 * An attribute represents a piece of information that must be provided by all products in a category having that
 * attribute. Each product stores an {@link AttributeValue} that provides the product's value for that attribute. This
 * class is immutable.
 *
 * @author Emil
 * @author Kasper
 */
public class Attribute implements Comparable<Attribute> {

	/**
	 * The id of this attribute.
	 */
	private final String id;

	/**
	 * The name of this attribute to be displayed to the user.
	 */
	private String name;

	/**
	 * The default value of the attribute.
	 */
	private Object defaultValue;

	/**
	 * The list of legal values for this attribute, or null if all values are allowed.
	 */
	private final Set<Object> legalValues;

	/**
	 * Constructs a new attribute with the specified id and name.
	 *
	 * @param id           the id of the attribute
	 * @param name         the name of the attribute
	 * @param defaultValue the default value of the attribute
	 */
	public Attribute(String id, String name, Object defaultValue) {
		this.id = id;
		this.name = name;
		this.defaultValue = defaultValue;
		this.legalValues = null;
	}

	/**
	 * Constructs a new attribute with the specified name and list of legal values. If all values are allowed, use
	 * {@link #Attribute(String, String, Object)} instead, or pass null as the 4th parameter.
	 *
	 * @param id           the id of the attribute
	 * @param name         the name of the attribute
	 * @param defaultValue the default value of the attribute
	 * @param legalValues  the list of legal values
	 */
	public Attribute(String id, String name, Object defaultValue, Set<Object> legalValues) {
		//Ensure that default value is among legal values, if any
		if (legalValues != null && !legalValues.contains(defaultValue)) {
			throw new IllegalArgumentException("Default value is not legal!");
		}

		this.id = id;
		this.name = name;
		this.defaultValue = defaultValue;
		//If the legal values are not null, we copy its contents into a new set, so that we can ensure that the legal
		//values cannot be changed
		this.legalValues = (legalValues == null ? null : new HashSet<>(legalValues));
	}

	/**
	 * Create a new attribute value object for this attribute with the default value. This method is provided, as the
	 * attribute class is responsible for knowing the default value.
	 *
	 * @return the resulting {@link AttributeValue} object
	 */
	public AttributeValue createValue() {
		return new AttributeValue(this, defaultValue);
	}

	/**
	 * Create a new attribute value object for this attribute. This method is provided, as the attribute class must be
	 * responsible for deciding whether a certain value for an attribute is allowed.
	 *
	 * @param value the value for the attribute
	 * @return the resulting {@link AttributeValue} object if the value is allowed
	 */
	public AttributeValue createValue(Object value) {
		if (legalValues == null || legalValues.contains(value)) {
			return new AttributeValue(this, value);
		}

		//Value not allowed
		throw new IllegalArgumentException("Value not allowed");
	}

	/**
	 * Get the id of this attribute.
	 *
	 * @return the id of this attribute
	 */
	public String getID() {
		return id;
	}

	/**
	 * Get the name of this attribute.
	 *
	 * @return the name of this attribute
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the default value of this attribute.
	 *
	 * @return the default value of this attribute
	 */
	public Object getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Get the set of legal values for this attribute or null if all values are allowed. This will return a copy of the
	 * internal set, so changing it will not affect this instance.
	 *
	 * @return the set of legal values for this attribute or null if all values are allowed
	 */
	public Set<Object> getLegalValues() {
		return legalValues == null ? null : new HashSet<>(legalValues);
	}

	@Override
	public String toString() {
		return getName().trim() + " : " + getID().trim();
	}

	@Override
	public int compareTo(Attribute o) {
		return getID().compareTo(o.getID());
	}

	/**
	 * Inner class for representing the values of attributes. This is an inner class to ensure that only {@link
	 * Attribute} can access its constructor. Also, this class is immutable. To change a value a new one must be created
	 * using {@link Attribute#createValue(Object)}.
	 */
	public class AttributeValue {

		/**
		 * The attribute for which this instance provides a value.
		 */
		private final Attribute parent;

		/**
		 * The value.
		 */
		private final Object value;

		/**
		 * Constructs a new attribute value object for the specified attribute and with the specified value.
		 *
		 * @param parent the attribute for which this provides a value
		 * @param value  the value
		 */
		private AttributeValue(Attribute parent, Object value) {
			this.parent = parent;
			this.value = value;
		}

		/**
		 * Get the attribute for which this instance provides a value.
		 *
		 * @return the attribute for which this instance provides a value
		 */
		public Attribute getParent() {
			return parent;
		}

		/**
		 * Get the value.
		 *
		 * @return the value
		 */
		public Object getValue() {
			return value;
		}
	}
}
