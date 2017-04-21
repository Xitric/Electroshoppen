package pim.business;

import java.util.HashMap;
import java.util.Set;

/**
 * Manages attribute creation and ensures no duplicates (same id) are made.
 *
 * @author mstruntze
 */
public class AttributeManager {
	/**
	 * The singleton instance for the attribute manager.
	 */
	private static AttributeManager instance;

	private HashMap<String, Attribute> attributes;

	/**
	 * Internal constructor.
	 */
	private AttributeManager() {
		attributes = new HashMap<>();
	}

	/**
	 * Get the singleton instance for the attribute manager.
	 *
	 * @return the singleton instance.
	 */
	public static AttributeManager getInstance() {
		if (instance == null) {
			instance = new AttributeManager();
		}

		return instance;
	}

	/**
	 * Creates an attributes if one with the given id does not exist already.
	 * Otherwise a reference to the existing attribute will be returned.
	 *
	 * @param id Id of the attribute.
	 * @param name Name of the attribute.
	 * @param defaultValue Default value of the attribute.
	 * @return Returns a reference to the created/existing attribute with the given id.
	 */
	public Attribute createAttribute(String id, String name, Object defaultValue) {
		if(!attributes.containsKey(id)) {
			Attribute att = new Attribute(id, name, defaultValue);
			attributes.put(id, att);
			return att;
		}

		return attributes.get(id);
	}

	/**
	 * Creates an attributes if one with the given id does not exist already.
	 * Otherwise a reference to the existing attribute will be returned.
	 *
	 * @param id Id of the attribute.
	 * @param name Name of the attribute.
	 * @param defaultValue Default value of the attribute.
	 * @param legalValues Allowed values for the attribute.
	 * @return Returns a reference to the created/existing attribute with the given id.
	 */
	public Attribute createAttribute(String id, String name, Object defaultValue, Set<Object> legalValues) {
		if(!attributes.containsKey(id)) {
			Attribute att = new Attribute(id, name, defaultValue, legalValues);
			attributes.put(id, att);
			return att;
		}

		return attributes.get(id);
	}
}
