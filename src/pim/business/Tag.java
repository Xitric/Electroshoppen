package pim.business;

/**
 * Tags are used to loosely group products by a string
 *
 * @author Mikkel
 */
public class Tag {
	private final String name;

	/**
	 * Constructs a new tag
	 *
	 * @param name name of the tag
	 */
	public Tag(String name) {
		this.name = name;
	}

	/**
	 * Get the name of the tag
	 *
	 * @return The name of the tag
	 */
	public String getName() {
		return name;
	}
}
