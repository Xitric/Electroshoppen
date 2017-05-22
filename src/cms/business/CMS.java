package cms.business;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Interface describing the functionality that must be provided by all CMS implementations.
 *
 * @author Kasper
 */
public interface 	CMS {

	/**
	 * get the html of the page with the specified id.
	 *
	 * @param id the id of the page
	 * @return the html of page with the specified id, or null if the page does not exist
	 * @throws IOException if the operation failed
	 */
	String getPage(int id) throws IOException;

	/**
	 * Get a collection of templates that support the specified type of page.
	 *
	 * @param pageType the type of page
	 * @return a collection of templates that support the specified type of page
	 * @throws IOException if the operation failed
	 */
	Collection<Template> getTemplatesForPageType(PageType pageType) throws IOException;

	/**
	 * Create a new page with the specified page type and template. This page will be set as active in the CMS,
	 * discarding current, unsaved data.
	 *
	 * @param pageType   the type of page
	 * @param templateID the id of the template to use
	 * @return the html representation of the page, or null if the page does not exist
	 * @throws IllegalArgumentException if the specified template does not support the specified page type, or if the
	 *                                  template is unknown
	 */
	String createNewPage(PageType pageType, int templateID);

	/**
	 * Start editing the page with the specified id. This will activate the specified page in the CMS.
	 *
	 * @param id the id of the page to edit
	 * @return the html representation of the page, or null if the page does not exist
	 * @throws IOException if the operation failed
	 */
	String editPage(int id) throws IOException;

	/**
	 * Get the text of the specified element in the currently active page. If the element does not contain text, null
	 * will be returned.
	 *
	 * @param id the id of the element
	 * @return the text of the element, or null if it contains no text
	 * @throws IllegalStateException if there is no active page
	 */
	String getElementText(String id);

	/**
	 * Set the text of the element with the specified id in the currently active page.
	 *
	 * @param id   the id of the element
	 * @param text the new text to set
	 * @return the html representation of the active page after the operation
	 * @throws IllegalStateException if there is no active page
	 */
	String editElementText(String id, String text);

	/**
	 * Insert the specified html markup at the location specified by the {@link DocumentMarker} in the currently active
	 * page.
	 *
	 * @param marker the location to insert the markup into
	 * @param html   the markup to insert
	 * @return the html representation of the active page after the operation
	 * @throws IllegalStateException if there is no active page
	 */
	String insertHTML(DocumentMarker marker, String html);

	/**
	 * Insert the specified text at the location specified by the {@link DocumentMarker} in the currently active page.
	 *
	 * @param marker the location to insert the text into
	 * @param text   the text to insert
	 * @return the html representation of the active page after the operation
	 * @throws IllegalStateException if there is no active page
	 */
	String insertText(DocumentMarker marker, String text);

	/**
	 * Insert the specified image at the location specified by the {@link DocumentMarker} in the currently active page.
	 *
	 * @param marker the location to insert the image into
	 * @param image  the image to insert
	 * @return the html representation of the active page after the operation
	 * @throws IllegalStateException if there is no active page
	 */
	String insertImage(DocumentMarker marker, BufferedImage image);

	/**
	 * Create a link to the page with the specified id. The link will be inserted at the location specified by the
	 * {@link DocumentMarker} in the currently active page.
	 *
	 * @param marker the location to insert the link into
	 * @param pageID the id of the page to link to
	 * @return the html representation of the active page after the operation
	 * @throws IllegalStateException if there is no active page
	 */
	String createLink(DocumentMarker marker, int pageID);

	/**
	 * Create a reference of the specified type to the specified product. The reference will be inserted at the location
	 * specified by the {@link DocumentMarker} in the currently active page.
	 *
	 * @param marker    the location to insert the reference into
	 * @param productID the id of the product to reference
	 * @param type      the type of reference to create
	 * @return the html representation of the active page after the operation
	 * @throws IllegalStateException if there is no active page
	 */
	String createReference(DocumentMarker marker, int productID, ReferenceType type);

	/**
	 * Remove the element at the location specified by the {@link DocumentMarker} in the currently active page.
	 *
	 * @param marker the location to remove the element from
	 * @return the html representation of the active page after the operation
	 * @throws IllegalStateException if there is no active page
	 */
	String removeElement(DocumentMarker marker);

	/**
	 * Save the currently active page. If no page is active, this method will do nothing.
	 *
	 * @throws IOException if the operation failed
	 */
	void savePage() throws IOException;

	/**
	 * Getting IDs and Names of all pages from the CMS Database
	 * @return Returns a Map where ID is the key and Name of a
	 * page is the Value
	 * @throws IOException Rather than throwing SQLException
	 */
	Map<Integer, String> getPageInfo() throws IOException;

	/**
	 * Get all page IDs from the database
	 * @return Returns a Set of ints (Wrapper Integer) representing
	 * the IDs of all pages
	 * @throws IOException Rather than throwing SQLException
	 */
	Set<Integer> getPageIDs() throws IOException;

	/**
	 * Get all page Names from the database
	 * @return Returns a Set of Strings representing the names
	 * of all pages
	 * @throws IOException Rather than throwing SQLException
	 */
	Set<String> getPageNames() throws IOException;

	/**
	 * A type of page handled by this CMS.
	 */
	enum PageType {
		PRODUCT_PAGE,
		ARTICLE,
		GUIDE,
		LANDING_PAGE
	}

	/**
	 * A type of product reference that can be handled by this CMS.
	 */
	enum ReferenceType {
		NAME,
		PRICE,
		IMAGE,
		DESCRIPTION,
		TAGS
	}
}
