package webshop.business;

import cms.business.CMS;
import pim.business.PIM;
import pim.business.Product;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

	@Override
	public String getLandingPage() throws IOException {
		return cms.getLandingPage();
	}

	@Override
	public String getPage(int id) throws IOException {
		return cms.getPage(id);
	}

	@Override
	public String getProductPage(int id) throws IOException {
		return cms.getProductPage(id);
	}

	@Override
	public Map<Integer, String> getGuidePages() throws IOException {
		return cms.getGuidePages();
	}

	@Override
	public Map<Integer, String> getArticlePages() throws IOException {
		return cms.getArticlePages();
	}

	@Override
	public List<Product> getAllProducts() throws IOException {
		return pim.getProducts();
	}
}
