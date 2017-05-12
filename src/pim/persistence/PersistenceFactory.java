package pim.persistence;

/**
 * A factory for creating objects to interact with the persistence layer.
 *
 * @author Kasper
 */
public class PersistenceFactory {

	/**
	 * Get an object for interacting with the persistence layer.
	 *
	 * @return an object for interacting with the persistence layer
	 */
	public static PersistenceFacade createDatabaseMediator() {
		//Currently the only implementation
		return new DatabaseFacade();
	}
}