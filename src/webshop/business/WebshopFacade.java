package webshop.business;

import cms.business.CMS;
import pim.business.PIM;

/**
 * A factory for creating objects to interact with the business layer of the webshop.
 *
 * @author Kasper
 */
public class WebshopFacade {

	/**
	 * Get an object for interacting with the business layer of the webshop.
	 *
	 * @return an object for interacting with the business layer
	 */
	public static Webshop createWebshop(CMS cms, PIM pim) {
		//Currently the only implementation
		return new WebshopImpl(cms, pim);
	}
}
