package cms.business;

import pim.business.PIM;

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
	public static CMS createCMS(PIM pim) {
		//Currently the only implementation
		return new CMSImpl(pim);
	}
}
