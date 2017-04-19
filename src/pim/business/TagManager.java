package pim.business;

import java.util.HashMap;

/**
 * Manages tags (creation, deletion) and prevents duplication
 *
 * @author mstruntze
 */
public class TagManager {
    /**
     * The singleton instance for the tag manager.
     */
    private static TagManager instance;

    private HashMap<String, Tag> tags;

    /**
     * Internal constructor.
     */
    private TagManager() {
        tags = new HashMap<>();
    }

    /**
     * Get the singleton instance for the tag manager.
     *
     * @return the singleton instance.
     */
    public static TagManager getInstance() {
        if (instance == null) {
            instance = new TagManager();
        }

        return instance;
    }

    /**
     * Creates a tag or returns the existing one with the same name if it already exists.
     *
     * @param name Name of the tag
     * @return The created tag object
     */
    public Tag createTag(String name) {
        if (tags.containsKey(name)) {
            return tags.get(name);
        }

        Tag tag = new Tag(name);
        tags.put(name, tag);

        return tag;
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
