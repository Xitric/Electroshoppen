package cms.business;

import cms.persistence.CMSPersistenceFactory;
import pim.business.PIM;
import pim.business.Product;
import pim.business.Image;

import java.io.IOException;
import java.util.*;

/**
 * Implementation of the cms interface.
 *
 * @author Kasper
 * @author Emil
 */
public class CMSImpl implements CMS {

	private final CMSPersistenceFacade persistence;
	private final PageManager pageManager;
	private final PIM pim;

	/**
	 * Constructs a new cms implementation.
	 *
	 * @param pim the mediator for the pim subsystem
	 */
	public CMSImpl(PIM pim) {
		persistence = CMSPersistenceFactory.createDatabaseMediator();
		pageManager = new PageManager(persistence);
		this.pim = pim;
	}

	@Override
	public String getPage(int id) throws IOException {
		//Get ids of referenced products on page
		List<Integer> productIDs = pageManager.getProductIDsFromPage(id);
		Map<Integer, Product> products = new HashMap<>();

		//Get products from the pim
		for (int i : productIDs) {
			products.put(i, pim.getProductInformation(i));
		}

		//Construct the page
		return pageManager.constructPage(id, products);
	}

	@Override
	public String getLandingPage() throws IOException {
		// We know the pageID probably should've been retrieved from the database,
		// but there's no proper user interface to change such things, so it was left out for now.
		Set<Product> popularProducts = pim.getPopularProducts(5);
		Map<Integer, Product> products = new HashMap<>();
		for(Product product : popularProducts){
			products.put(product.getID(), product);
		}
		return pageManager.constructPage(8, products);
	}

	@Override
	public String getProductPage(int productID) throws IOException {
		//Get product from pim
		Map<Integer, Product> product = new HashMap<>();
		product.put(productID, pim.getProductInformation(productID));

		//Construct the page | We know the pageID probably should've been retrieved from the database,
		// but there's no proper user interface to change such things, so it was left out for now.
		return pageManager.constructPage(7, product);
	}

	@Override
	public Map<Integer, String> getGuidePages() throws IOException {
		return persistence.getPagesByType(PageType.GUIDE.toString());
	}

	@Override
	public Map<Integer, String> getArticlePages() throws IOException {
		return persistence.getPagesByType(PageType.ARTICLE.toString());
	}

	@Override
	public Collection<Template> getTemplatesForPageType(PageType pageType) throws IOException {
		return persistence.getTemplates(pageType.toString());
	}

	@Override
	public String createNewPage(String name, PageType pageType, int templateID) {
		return pageManager.createNewPage(name, pageType, templateID).toString();
	}

	@Override
	public String editPage(int id) throws IOException {
		return pageManager.editPage(id).toString();
	}

	@Override
	public String getElementText(String id) {
		return pageManager.getElementText(id);
	}

	@Override
	public String editElementText(String id, String text) {
		return pageManager.editElementText(id, text).toString();
	}

	@Override
	public String insertHTML(DocumentMarker marker, String html) {
		return pageManager.insertHTML(marker, html).toString();
	}

	@Override
	public String insertText(DocumentMarker marker, String text) {
		return pageManager.insertText(marker, text).toString();
	}

	@Override
	public String insertImage(DocumentMarker marker, Image image) {
		return pageManager.insertImage(marker, image).toString();
	}

	@Override
	public String createLink(DocumentMarker marker, int pageID) {
		return pageManager.createLink(marker, pageID).toString();
	}

	@Override
	public String createReference(DocumentMarker marker, int productID, ReferenceType type) {
		return pageManager.createReference(marker, productID, type).toString();
	}

	@Override
	public String removeElement(DocumentMarker marker) {
		return pageManager.removeElement(marker).toString();
	}

	@Override
	public void savePage() throws IOException {
		pageManager.savePage();
	}

	@Override
	public Map<Integer, String> getPageInfo() throws IOException {
		return persistence.getPageInfo();
	}

	@Override
	public List<Product> getAllProducts() throws IOException {
		return pim.getProducts();
	}

}
