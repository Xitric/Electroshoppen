package pim.business;

import pim.persistence.PersistenceMediator;

import java.util.HashMap;

/**
 * Manages tags (creation, deletion) and prevents duplication
 *
 * @author mstruntze
 * @author Kasper
 */
public class TagManager {

	private HashMap<String, Tag> tags;
	private final PersistenceMediator persistence;

	/**
	 * Constructs a new tag manager.
	 *
	 * @param persistence the persistence mediator
	 */
	public TagManager(PersistenceMediator persistence) {
		tags = new HashMap<>();
		this.persistence = persistence;
	}

	/**
	 * Creates a tag or returns the existing one with the same name if it already exists.
	 *
	 * @param name Name of the tag
	 * @return The created tag object
	 */
	public Tag createTag(String name) {
		return tags.computeIfAbsent(name, Tag::new);
	}

	/**
	 * Removes a tag from the list of tags.
	 *
	 * @param name Name of the tag to be removed.
	 */
	public void removeTag(String name) {
		tags.remove(name);
	}
}
