package pim.business;

import pim.persistence.PersistenceMediator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

/**
 * Manages attribute creation and ensures no duplicates (same id) are made. Also responsible of managing attributes in
 * memory.
 *
 * @author mstruntze
 * @author Kasper
 */
public class AttributeManager {

	private final HashMap<String, Attribute> attributes;
	private final PersistenceMediator persistence;

	/**
	 * Constructs a new attribute manager.
	 *
	 * @param persistence the persistence mediator
	 */
	public AttributeManager(PersistenceMediator persistence) {
		attributes = new HashMap<>();
		this.persistence = persistence;
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

	/**
	 * Get the attribute with the specified id. If the attribute is not in memory, it will be loaded from the
	 * persistence layer.
	 *
	 * @param attributeID the id of the attribute
	 * @return the attribute with the specified id, or null if no such attribute could be retrieved
	 * @throws IOException if something goes wrong
	 */
	public Attribute getAttribute(String attributeID) throws IOException {
		//Look in memory first
		Attribute a = attributes.get(attributeID);

		//If this failed, look in persistence. This might also fail, leaving a as null
		if (a == null) {
			a = persistence.getAttributeByID(attributeID);
		}

		return a;
	}

	/**
	 * Get the attribute with the specified id in memory.
	 *
	 * @param attributeID the id of the attribute
	 * @return the attribute with the specified id, or null if no such attribute could be retrieved from memory
	 */
	public Attribute getLoadedAttribute(String attributeID) {
		return attributes.get(attributeID);
	}

	/**
	 * Get a set of all categories.
	 *
	 * @return a set of all categories
	 * @throws IOException if something goes wrong
	 */
	public Set<Attribute> getAttributes() throws IOException {
		return persistence.getAttributes();
	}

	/**
	 * Save the information about the specified attribute in the database.
	 *
	 * @param attribute the attribute to save
	 */
	public void saveAttribute(Attribute attribute) {
		persistence.saveAttribute(attribute);
	}

	/**
	 * Delete the specified atribute in the database.
	 *
	 * @param attributeID the id of the attribute
	 */
	public void deleteAttribute(String attributeID) {
		attributes.remove(attributeID);
		persistence.deleteAttribute(attributeID);
	}
}
