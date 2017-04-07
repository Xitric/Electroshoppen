package pim.business;

import java.util.ArrayList;
import java.util.List;

/**
 * An attribute represents a piece of information that must be provided by all products in a category having that
 * attribute. Each product stores an {@link AttributeValue} that provides the product's value for that attribute. This
 * class is immutable.
 *
 * @param <T> the type of the values for this attribute
 * @author Emil
 * @author Kasper
 */
public class Attribute<T> {

	/**
	 * The name of this attribute to be displayed to the user.
	 */
	private final String name;

	/**
	 * The list of legal values for this attribute, or null if all values are allowed.
	 */
	private final List<T> legalValues;

	/**
	 * Constructs a new attribute with the specified name.
	 *
	 * @param name the name of the attribute
	 */
	public Attribute(String name) {
		this.name = name;
		this.legalValues = null;
	}

	/**
	 * Constructs a new attribute with the specified name and list of legal values. If all values are allowed, use
	 * {@link #Attribute(String)} instead.
	 *
	 * @param name        the name of the attribute
	 * @param legalValues the list of legal values
	 */
	public Attribute(String name, List<T> legalValues) {
		this.name = name;
		this.legalValues = legalValues;
	}

	/**
	 * Create a new attribute value object for this attribute. This method is provided, as the attribute class must be
	 * responsible for deciding whether a certain value for an attribute is allowed.
	 *
	 * @param value the value for the attribute
	 * @return the resulting {@link AttributeValue} object if the value is allowed
	 */
	public AttributeValue createValue(T value) {
		if (legalValues == null || legalValues.contains(value)) {
			return new AttributeValue<T>(this, value);
		}

		//Value not allowed
		throw new IllegalArgumentException("Value not allowed");
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
	 * Get the list of legal values for this attribute or null if all values are allowed. This will return a copy of the
	 * internal list, so changing it will not affect this instance.
	 *
	 * @return the list of legal values for this attribute or null if all values are allowed
	 */
	public List<T> getLegalValues() {
		return legalValues == null ? null : new ArrayList<T>(legalValues);
	}

	/**
	 * Inner class for representing the values of attributes. This is an inner class to ensure that only {@link
	 * Attribute} can access its constructor. Also, this class is immutable. To change a value a new one must be created
	 * using {@link Attribute#createValue(T)}.
	 *
	 * @param <T> the type of the value
	 */
	public class AttributeValue<T> {

		/**
		 * The attribute for which this instance provides a value.
		 */
		private final Attribute parent;

		/**
		 * The value.
		 */
		private final T value;

		/**
		 * Constructs a new attribute value object for the specified attribute and with the specified value.
		 *
		 * @param parent the attribute for which this provides a value
		 * @param value  the value
		 */
		private AttributeValue(Attribute parent, T value) {
			this.parent = parent;
			this.value = value;
		}

		/**
		 * Get the attribute for which this instance provides a value.
		 *
		 * @return the attribute for which this instance provides a value
		 */
		public Attribute<T> getParent() {
			return parent;
		}

		/**
		 * Get the value.
		 *
		 * @return the value
		 */
		public T getValue() {
			return value;
		}
	}
}
