package pim.business;

import pim.persistence.PersistenceFacade;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

/**
 * Manages attribute creation and ensures no duplicates (same id) are made.
 *
 * @author Mikkel
 * @author Kasper
 */
class AttributeManager {

	private final HashMap<Integer, Attribute> attributes;
	private final PersistenceFacade persistence;

	/**
	 * Constructs a new attribute manager.
	 *
	 * @param persistence the persistence facade
	 */
	public AttributeManager(PersistenceFacade persistence) {
		attributes = new HashMap<>();
		this.persistence = persistence;
	}

	/**
	 * Constructs an attributes if one with the given id does not exist already. Otherwise a reference to the existing
	 * attribute will be returned.
	 *
	 * @param id           id of the attribute.
	 * @param name         name of the attribute.
	 * @param defaultValue default value of the attribute.
	 * @return returns a reference to the created/existing attribute with the given id.
	 */
	public Attribute constructAttribute(int id, String name, Object defaultValue) {
		Attribute a;

		if (attributes.get(id) == null) {
			a = new Attribute(id, name, defaultValue);
			attributes.put(id, a);
		} else {
			a = attributes.get(id);
			a.setName(name);
			a.setDefaultValue(defaultValue);
		}

		return a;
	}

	/**
	 * Constructs an attributes if one with the given id does not exist already. Otherwise a reference to the existing
	 * attribute will be returned.
	 *
	 * @param id           id of the attribute.
	 * @param name         name of the attribute.
	 * @param defaultValue default value of the attribute.
	 * @param legalValues  allowed values for the attribute.
	 * @return returns a reference to the created/existing attribute with the given id.
	 */
	public Attribute constructAttribute(int id, String name, Object defaultValue, Set<Object> legalValues) {
		Attribute a;

		if (attributes.get(id) == null) {
			a = new Attribute(id, name, defaultValue, legalValues);
			attributes.put(id, a);
		} else {
			a = attributes.get(id);
			a.setName(name);
			a.setDefaultValue(defaultValue);
			//Legal values are immutable
		}

		return a;
	}

	/**
	 * Create a new attribute with the specified data in the PIM. This attribute will automatically be saved.
	 *
	 * @param name         the name of the attribute
	 * @param defaultValue the default value of the attribute
	 * @param legalValues  allowed values for the attribute
	 * @return the new attribute
	 * @throws IOException if something goes wrong
	 */
	public Attribute createAttribute(String name, Object defaultValue, Set<Object> legalValues) throws IOException {
		Attribute attribute = new Attribute(name, defaultValue, legalValues);
		persistence.saveAttribute(attribute);
		attributes.put(attribute.getID(), attribute); //Attribute should have a valid id after it has been saved
		return attribute;
	}

	/**
	 * Get the attribute with the specified id.
	 *
	 * @param attributeID the id of the attribute
	 * @return the attribute with the specified id, or null if no such attribute could be retrieved
	 * @throws IOException if something goes wrong
	 */
	public Attribute getAttribute(int attributeID) throws IOException {
		return persistence.getAttributeByID(attributeID);
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
	 * Save the information about the specified attribute in the database.
	 *
	 * @param attribute the attribute to save
	 * @throws IOException if something goes wrong
	 */
	public void saveAttribute(Attribute attribute) throws IOException {
		persistence.saveAttribute(attribute);
		attributes.put(attribute.getID(), attribute);
	}

	/**
	 * Delete the specified attribute in the database.
	 *
	 * @param attributeID the id of the attribute
	 * @throws IOException if something goes wrong
	 */
	public void deleteAttribute(int attributeID) throws IOException {
		persistence.deleteAttribute(attributeID);
		attributes.remove(attributeID);
	}
}
