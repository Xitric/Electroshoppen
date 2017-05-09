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
	 * The list of legal values for this attribute, or null if all values are allowed.
	 */
	private final Set<Object> legalValues;
	/**
	 * The id of this attribute.
	 */
	private int id;
	/**
	 * The name of this attribute to be displayed to the user.
	 */
	private String name;
	/**
	 * The default value of the attribute.
	 */
	private Object defaultValue;

	/**
	 * Constructs a new attribute with the specified name and with a missing id. The id is generated when the attribute
	 * is saved to the database for the first time.
	 *
	 * @param name         the name of the attribute
	 * @param defaultValue the default value of the attribute
	 */
	public Attribute(String name, Object defaultValue) {
		this(-1, name, defaultValue);
	}

	/**
	 * Constructs a new attribute with the specified id and name.
	 *
	 * @param id           the id of the attribute
	 * @param name         the name of the attribute
	 * @param defaultValue the default value of the attribute
	 */
	public Attribute(int id, String name, Object defaultValue) {
		this.id = id;
		this.name = name;
		this.defaultValue = defaultValue;
		this.legalValues = null;
	}

	/**
	 * Constructs a new attribute with the specified name and list of legal values. If all values are allowed, use
	 * {@link #Attribute(int, String, Object)} instead, or pass null as the 4th parameter.
	 *
	 * @param id           the id of the attribute
	 * @param name         the name of the attribute
	 * @param defaultValue the default value of the attribute
	 * @param legalValues  the list of legal values
	 */
	public Attribute(int id, String name, Object defaultValue, Set<Object> legalValues) {
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
	 * Test whether the id of this product is valid.
	 *
	 * @return true if the id is valid, false otherwise
	 */
	public boolean hasValidID() {
		return id >= 0;
	}

	/**
	 * Get the id of this attribute.
	 *
	 * @return the id of this attribute
	 */
	public int getID() {
		return id;
	}

	/**
	 * Set the id of this attribute. This operation will be ignored if the id is already set. The purpose of this method
	 * is to allow the persistence layer to assign an id to am attribute created in the domain layer.
	 *
	 * @param id the id of the attribute
	 */
	public void setID(int id) {
		if (this.id < 0) {
			this.id = id;
		}
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
	 * Set the name of this attribute.
	 *
	 * @param name the name of this attribute
	 */
	public void setName(String name) {
		this.name = name;
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
	 * Set the default value of this attribute.
	 *
	 * @param value the default value
	 * @throws IllegalArgumentException if the value is not allowed
	 */
	public void setDefaultValue(Object value) {
		AttributeValue temp = this.createValue(value);

		//If no error so far, set the value
		this.defaultValue = temp.getValue();
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
		return "[" + getID() + "] " + getName();
	}

	@Override
	public int compareTo(Attribute o) {
		return Integer.compare(this.getID(), o.getID());
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

		@Override
		public String toString() {
			return value.toString();
		}
	}
}
