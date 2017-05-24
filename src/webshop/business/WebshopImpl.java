package webshop.business;

import cms.business.CMS;
import pim.business.PIM;

/**
 * Implementation of the webshop interface.
 *
 * @author Kasper
 */
public class WebshopImpl implements Webshop {

	private final CMS cms;
	private final PIM pim;

	/**
	 * Constructs a new webshop implementation.
	 *
	 * @param cms the mediator for the cms subsystem
	 * @param pim the mediator for the pim subsystem
	 */
	public WebshopImpl(CMS cms, PIM pim) {
		this.cms = cms;
		this.pim = pim;
	}
}
