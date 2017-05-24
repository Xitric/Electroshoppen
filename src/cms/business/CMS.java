package cms.business;

import pim.business.Product;
import shared.Image;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Interface describing the functionality that must be provided by all cms implementations.
 *
 * @author Kasper
 */
public interface CMS {

	/**
	 * Get the html of the page with the specified id.
	 *
	 * @param id the id of the page
	 * @return the html of page with the specified id, or null if the page does not exist
	 * @throws IOException if the operation failed
	 */
	String getPage(int id) throws IOException;

	/**
	 * Get the html of the landing page.
	 *
	 * @return the html of the landing page, or null if the page does no exist
	 * @throws IOException if the operation failed
	 */
	String getLandingPage() throws IOException;

	/**
	 * Get e product page for the product with the specified id.
	 *
	 * @param productID the id of the product
	 * @return the html of the product page, or null if the page does no exist
	 * @throws IOException if the operation failed
	 */
	String getProductPage(int productID) throws IOException;

	/**
	 * Get a collection of templates that support the specified type of page.
	 *
	 * @param pageType the type of page
	 * @return a collection of templates that support the specified type of page
	 * @throws IOException if the operation failed
	 */
	Collection<Template> getTemplatesForPageType(PageType pageType) throws IOException;

	/**
	 * Create a new page with the specified name, page type, and template. This page will be set as active in the cms,
	 * discarding current, unsaved data.
	 *
	 * @param name       the name of the page to create
	 * @param pageType   the type of page
	 * @param templateID the id of the template to use
	 * @return the html representation of the page, or null if the page does not exist
	 * @throws IllegalArgumentException if the specified template does not support the specified page type, or if the
	 *                                  template is unknown
	 */
	String createNewPage(String name, PageType pageType, int templateID);

	/**
	 * Start editing the page with the specified id. This will activate the specified page in the cms.
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
	String insertImage(DocumentMarker marker, Image image);

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
	 * Getting IDs and Names of all pages from the cms Database.
	 *
	 * @return Returns a Map where ID is the key and Name of a page is the Value
	 * @throws IOException if the operation failed
	 */
	Map<Integer, String> getPageInfo() throws IOException;

	/**
	 * Getting all products from the PIM
	 * @return a list of all products in the system
	 */
	List<Product> getAllProducts() throws IOException;

	/**
	 * A type of page handled by this cms.
	 */
	enum PageType {
		PRODUCT_PAGE,
		ARTICLE,
		GUIDE,
		LANDING_PAGE
	}

	/**
	 * A type of product reference that can be handled by this cms.
	 */
	enum ReferenceType {
		NAME,
		PRICE,
		IMAGE,
		DESCRIPTION,
		TAGS
	}
}
