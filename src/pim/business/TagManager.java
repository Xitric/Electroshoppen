package pim.business;

import java.util.HashMap;

/**
 * Manages tags (creation, deletion) and prevents duplication
 *
 * @author Mikkel
 * @author Kasper
 */
class TagManager {

	private HashMap<String, Tag> tags;
	private final PIMPersistenceFacade persistence;

	/**
	 * Constructs a new tag manager.
	 *
	 * @param persistence the persistence facade
	 */
	public TagManager(PIMPersistenceFacade persistence) {
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
