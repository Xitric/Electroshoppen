package pim.business;

import java.util.HashMap;
import java.util.Set;

/**
 * Manages attribute creation and ensures no duplicates (same id) are made.
 *
 * @author mstruntze
 * @author Kasper
 */
public class AttributeManager {

	private HashMap<String, Attribute> attributes;

	/**
	 * Constructs a new attribute manager.
	 */
	public AttributeManager() {
		attributes = new HashMap<>();
	}

	/**
	 * Creates an attributes if one with the given id does not exist already.
	 * Otherwise a reference to the existing attribute will be returned.
	 *
	 * @param id           Id of the attribute.
	 * @param name         Name of the attribute.
	 * @param defaultValue Default value of the attribute.
	 * @return Returns a reference to the created/existing attribute with the given id.
	 */
	public Attribute createAttribute(String id, String name, Object defaultValue) {
		return attributes.computeIfAbsent(id, i -> new Attribute(i, name, defaultValue));
	}

	/**
	 * Creates an attributes if one with the given id does not exist already.
	 * Otherwise a reference to the existing attribute will be returned.
	 *
	 * @param id           Id of the attribute.
	 * @param name         Name of the attribute.
	 * @param defaultValue Default value of the attribute.
	 * @param legalValues  Allowed values for the attribute.
	 * @return Returns a reference to the created/existing attribute with the given id.
	 */
	public Attribute createAttribute(String id, String name, Object defaultValue, Set<Object> legalValues) {
		return attributes.computeIfAbsent(id, i -> new Attribute(i, name, defaultValue, legalValues));
	}
}
