package cms.business;

import cms.persistence.CMSPersistenceFacade;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

	//TODO: Product list from CMSImpl

	/**
	 * Construct the page with the specified id and make it ready for web use.
	 *
	 * @param pageID the id of the page to get
	 * @return the html representation of the page, or null if no such page was found
	 * @throws IOException if there was an error loading the page
	 */
	public String constructPage(int pageID) throws IOException {
		//Read the page template
		Template template = persistence.getTemplateForPage(pageID);
		if (template != null) {

			//If this succeeded, read the page content
			DynamicPage page = persistence.getPage(pageID);
			if (page != null) {
				//Compile page links
				String html = template.enrichPage(page).toString();
				html = html.replaceAll("(\\[@ref=(\\w+)])", "<a href=\"$2\">");
				html = html.replaceAll("(\\[@])", "</a>");

				return html;
			}
		}

		return null;
	}
	//String output = input.replaceAll("foob(..)foo", "foof$1foo");

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
			for (String id : template.getElementIDs()) {
				//We must wrap the default content in a new div with a new id
				XMLElement content = XMLElement.createRoot("div");
				content.setID(id + 'c');
				content.addChildren(template.getDefaultContentForElement(id));
				defaultContent.put(id, content.toString());
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
	 * Get the text of the specified element in the currently active page. If the element does not contain text, null
	 * will be returned.
	 *
	 * @param id the id of the element
	 * @return the text of the element, or null if it contains no text
	 * @throws IllegalStateException if there is no active page
	 */
	public String getElementText(String id) {
		if (activePage == null)
			throw new IllegalStateException("No active page to get text from!");

		try {
			return activePage.getTextFromElement(id);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	/**
	 * Set the text of the element with the specified id in the currently active page.
	 *
	 * @param id   the id of the element
	 * @param text the new text to set
	 * @return the representation of the active page after the operation
	 * @throws IllegalStateException if there is no active page
	 */
	public XMLElement editElementText(String id, String text) {
		if (activePage == null)
			throw new IllegalStateException("No active page to edit text in!");

		try {
			activePage.setText(id, text);
		} catch (IllegalArgumentException e) {
			//We simply ignore this
		}

		return activeTemplate.enrichPage(activePage);
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
	 * Create a link to the page with the specified id. The link will be inserted at the location specified by the
	 * {@link DocumentMarker} in the currently active page.
	 *
	 * @param marker the location to insert the link into
	 * @param pageID the id of the page to link to
	 * @return the representation of the active page after the operation
	 * @throws IllegalStateException if there is no active page
	 */
	public XMLElement createLink(DocumentMarker marker, int pageID) {
		if (activePage == null)
			throw new IllegalStateException("No active page to remove from!");

		activePage.setTextLink(marker, pageID);

		return activeTemplate.enrichPage(activePage);
	}

	/**
	 * Create a reference of the specified type to the specified product. The reference will be inserted at the location
	 * specified by the {@link DocumentMarker} in the currently active page.
	 *
	 * @param marker    the location to insert the reference into
	 * @param productID the id of the product to reference
	 * @param type      the type of reference to create
	 * @return the representation of the active page after the operation
	 * @throws IllegalStateException if there is no active page
	 */
	public XMLElement createReference(DocumentMarker marker, int productID, CMS.ReferenceType type) {
		return null;
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

	/**
	 * Save the currently active page. If no page is active, this method will do nothing.
	 *
	 * @throws IOException if the operation failed
	 */
	public void savePage() throws IOException {
		if (activePage != null && activeTemplate != null) {
			persistence.savePage(activePage, activeTemplate);
		}
	}

	/**
	 * Returns all page ids from the database
	 *
	 * @return
	 * @throws IOException
	 */
	public Set<Integer> getPageIDs() throws IOException {
		return persistence.getPageIDs();
	}

	/**
	 * Returns all page names from the database
	 *
	 * @return
	 * @throws IOException
	 */
	public Set<String> getPageNames() throws IOException {
		return persistence.getPageNames();
	}
}
