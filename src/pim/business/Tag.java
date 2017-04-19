package pim.business;

import pim.persistence.DatabaseMediator;

/**
 * Tags are used to loosely group products by a string
 * @author mstruntze
 */
public class Tag {
    private final String name;

    /**
     * Constructs a new tag
     * @param name name of the tag
     */
    public Tag(String name) {
        this.name = name;
    }

    /**
     * Get the name of the tag
     * @return The name of the tag
     */
    public String getName() {
        return name;
    }

	/**
	 * Saves the tag to the database using the database mediator.
	 */
	public void save() {
        DatabaseMediator.getInstance().saveTag(this);
    }
}
