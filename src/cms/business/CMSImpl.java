package cms.business;

import cms.persistence.CMSPersistenceFacade;
import cms.persistence.CMSPersistenceFactory;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;

/**
 * Implementation of the CMS interface.
 *
 * @author Kasper
 */
public class CMSImpl implements CMS {

	private final CMSPersistenceFacade persistence;
	private final PageManager pageManager;

	/**
	 * Constructs a new CMS implementation.
	 */
	public CMSImpl() {
		persistence = CMSPersistenceFactory.createDatabaseMediator();
		pageManager = new PageManager(persistence);
	}

	@Override
	public String getPage(int id) throws IOException {
		return null;
	}

	@Override
	public Collection<Template> getTemplatesForPageType(PageType pageType) throws IOException {
		return persistence.getTemplates(pageType.toString());
	}

	@Override
	public String createNewPage(PageType pageType, int templateID) {
		return pageManager.createNewPage(pageType, templateID).toString();
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
	public String insertImage(DocumentMarker marker, BufferedImage image) {
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
}
