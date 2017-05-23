package cms.business;

import cms.persistence.CMSPersistenceFactory;
import pim.business.PIM;
import pim.business.Product;
import shared.Image;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the CMS interface.
 *
 * @author Kasper
 */
public class CMSImpl implements CMS {

	private final CMSPersistenceFacade persistence;
	private final PageManager pageManager;
	private final PIM pim;
	/**
	 * Constructs a new CMS implementation.
	 */
	public CMSImpl(PIM pim) {
		persistence = CMSPersistenceFactory.createDatabaseMediator();
		pageManager = new PageManager(persistence);
		this.pim = pim;
	}

	@Override
	public String getPage(int id) throws IOException {
		return pageManager.constructPage(id);
	}

	@Override
	public String getLandingPage() throws IOException {
		//TODO: Store in database, perhaps?
		return getPage(5);
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
