package cms.business;

/**
 * A factory for creating objects to interact with the business layer of the CMS.
 *
 * @author Kasper
 */
public class CMSFacade {

	/**
	 * Get an object for interacting with the business layer of the CMS.
	 *
	 * @return an object for interacting with the business layer
	 */
	public static CMS createCMS() {
		//Currently the only implementation
		return new CMSImpl();
	}
}
