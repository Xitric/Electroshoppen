package cms.business;

import cms.persistence.CMSPersistenceFacade;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The manager for loading, saving and editing dynamic pages.
 *
 * @author Kasper
 */
class PageManager {

	private final CMSPersistenceFacade persistence;
	private Template activeTemplate;
	private DynamicPage activePage;

	/**
	 * Constructs a new page manager.
	 *
	 * @param persistence the persistence facade
	 */
	public PageManager(CMSPersistenceFacade persistence) {
		this.persistence = persistence;
	}

	/**
	 * Create a new page with the specified page type and template. This page will be set as active, discarding current,
	 * unsaved data.
	 *
	 * @param pageType   the type of page
	 * @param templateID the id of the template to use
	 * @return the representation of the newly loaded page, or null if no such page exists
	 * @throws IllegalArgumentException if the specified template does not support the specified page type, or if the
	 *                                  template is unknown
	 */
	public XMLElement createNewPage(CMS.PageType pageType, int templateID) {
		try {
			//Read template and ensure compatibility
			Template template = persistence.getTemplate(templateID);
			if (template.getType() != pageType)
				throw new IllegalArgumentException("The specified template with id " + templateID + " does not support the page type " + pageType);

			//Construct a new dynamic page with no id (created upon saving in database), and the default content of the
			//template
			Map<String, String> defaultContent = new HashMap<>();
			for (String id: template.getElementIDs()) {
				defaultContent.put(id, template.getDefaultHMTLForElement(id));
			}

			activePage = new DynamicPageImpl(-1, defaultContent);
			activeTemplate = template;
			return template.enrichPage(activePage);
		} catch (IOException e) {
			throw new IllegalArgumentException("Unable to read template with id " + templateID);
		}
	}

	/**
	 * Start editing the page with the specified id. This will load the page information from the persistence layer and
	 * activate the loaded page. If no such page exists, this method will deactivate the current page regardless, losing
	 * all unsaved data.
	 *
	 * @param id the id of the page to edit
	 * @return the representation of the newly loaded page, or null if no such page exists
	 * @throws IOException if there was an error loading the page
	 */
	public XMLElement editPage(int id) throws IOException {
		//Read the page template
		Template template = persistence.getTemplateForPage(id);
		if (template != null) {

			//If this succeeded, read the page content
			DynamicPage page = persistence.getPage(id);
			if (page != null) {

				//If this succeeded too, activate the page and return the xml representation
				this.activeTemplate = template;
				this.activePage = page;

				return template.enrichPage(page);
			}
		}

		//Something went wrong, so deactivate current page and template and return null
		this.activeTemplate = null;
		this.activePage = null;
		return null;
	}

	/**
	 * Insert the specified html markup at the location specified by the {@link DocumentMarker} in the currently active
	 * page.
	 *
	 * @param marker the location to insert the markup into
	 * @param html   the markup to insert
	 * @return the representation of the active page after the operation
	 * @throws IllegalStateException if there is no active page
	 */
	public XMLElement insertHTML(DocumentMarker marker, String html) {
		if (activePage == null)
			throw new IllegalStateException("No active page to insert into!");

		activePage.insertHTML(marker, html);

		return activeTemplate.enrichPage(activePage);
	}

	/**
	 * Insert the specified text at the location specified by the {@link DocumentMarker} in the currently active page.
	 *
	 * @param marker the location to insert the text into
	 * @param text   the text to insert
	 * @return the representation of the active page after the operation
	 * @throws IllegalStateException if there is no active page
	 */
	public XMLElement insertText(DocumentMarker marker, String text) {
		if (activePage == null)
			throw new IllegalStateException("No active page to insert into!");

		activePage.insertText(marker, text);

		return activeTemplate.enrichPage(activePage);
	}

	/**
	 * Insert the specified image at the location specified by the {@link DocumentMarker} in the currently active page.
	 *
	 * @param marker the location to insert the image into
	 * @param image  the image to insert
	 * @return the representation of the active page after the operation
	 * @throws IllegalStateException if there is no active page
	 */
	public XMLElement insertImage(DocumentMarker marker, BufferedImage image) {
		if (activePage == null)
			throw new IllegalStateException("No active page to insert into!");

		activePage.insertImage(marker, image);

		return activeTemplate.enrichPage(activePage);
	}

	/**
	 * Remove the element at the location specified by the {@link DocumentMarker} in the currently active page.
	 *
	 * @param marker the location to remove the element from
	 * @return the representation of the active page after the operation
	 * @throws IllegalStateException if there is no active page
	 */
	public XMLElement removeElement(DocumentMarker marker) {
		if (activePage == null)
			throw new IllegalStateException("No active page to remove from!");

		activePage.removeElement(marker);

		return activeTemplate.enrichPage(activePage);
	}
}
