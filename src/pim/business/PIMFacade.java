package pim.business;

/**
 * A factory for creating objects to interact with the business layer.
 *
 * @author Kasper
 */
public class PIMFacade {

	/**
	 * Get an object for interacting with the business layer.
	 *
	 * @return an object for interacting with the business layer
	 */
	public static PIM createPIM() {
		//Currently the only implementation
		return new PIMImpl();
	}
}
