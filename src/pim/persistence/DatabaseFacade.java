package pim.persistence;

import pim.business.AttributeManager;
import pim.business.CategoryManager;
import pim.business.ImageManager;
import pim.business.TagManager;

/**
 * A factory for creating objects to interact with the persistence layer.
 *
 * @author Kasper
 */
public class DatabaseFacade {

	/**
	 * Get an object for interacting with the persistence layer.
	 *
	 * @return an object for interacting with the persistence layer
	 */
	public static PersistenceMediator createDatabaseMediator(CategoryManager cm, AttributeManager am, TagManager tm, ImageManager im) {
		//Currently the only implementation
		return new DatabaseMediator(cm, am, tm, im);
	}
}
