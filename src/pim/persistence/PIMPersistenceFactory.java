package pim.persistence;

/**
 * A factory for creating objects to interact with the persistence layer of the PIM.
 *
 * @author Kasper
 */
public class PIMPersistenceFactory {

	/**
	 * Get an object for interacting with the persistence layer.
	 *
	 * @return an object for interacting with the persistence layer
	 */
	public static PIMPersistenceFacade createDatabaseMediator() {
		//Currently the only implementation
		return new PIMDatabaseFacade();
	}
}
