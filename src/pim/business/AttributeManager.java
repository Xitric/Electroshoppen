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

	private final HashMap<Integer, Attribute> attributes;
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
	 * Creates an attributes if one with the given id does not exist already. Otherwise a reference to the existing
	 * attribute will be returned.
	 *
	 * @param id           id of the attribute.
	 * @param name         name of the attribute.
	 * @param defaultValue default value of the attribute.
	 * @return returns a reference to the created/existing attribute with the given id.
	 */
	public Attribute createAttribute(int id, String name, Object defaultValue) {
		return attributes.computeIfAbsent(id, i -> new Attribute(i, name, defaultValue));
	}

	/**
	 * Creates an attributes if one with the given id does not exist already. Otherwise a reference to the existing
	 * attribute will be returned.
	 *
	 * @param id           id of the attribute.
	 * @param name         name of the attribute.
	 * @param defaultValue default value of the attribute.
	 * @param legalValues  allowed values for the attribute.
	 * @return returns a reference to the created/existing attribute with the given id.
	 */
	public Attribute createAttribute(int id, String name, Object defaultValue, Set<Object> legalValues) {
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
	public Attribute getAttribute(int attributeID) throws IOException {
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
	public Attribute getLoadedAttribute(int attributeID) {
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
	 * Register a new attribute.
	 *
	 * @param name         the name of the attribute
	 * @param defaultValue the default value of the attribute
	 * @param legalValues  the legal values of the attribute, or null if all values are allowed
	 * @return the id of the new attribute
	 * @throws IOException if something goes wrong
	 */
	public int registerAttribute(String name, Object defaultValue, Set<Object> legalValues) throws IOException {
		return persistence.createAttribute(name, defaultValue, legalValues);
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
	 * Delete the specified attribute in the database.
	 *
	 * @param attributeID the id of the attribute
	 */
	public void deleteAttribute(int attributeID) {
		attributes.remove(attributeID);
		persistence.deleteAttribute(attributeID);
	}
}
