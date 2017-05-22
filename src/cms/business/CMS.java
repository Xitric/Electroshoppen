package cms.business;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;

/**
 * Interface describing the functionality that must be provided by all CMS implementations.
 *
 * @author Kasper
 */
public interface CMS {

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
	 * Remove the element at the location specified by the {@link DocumentMarker} in the currently active page.
	 *
	 * @param marker the location to remove the element from
	 * @return the html representation of the active page after the operation
	 * @throws IllegalStateException if there is no active page
	 */
	String removeElement(DocumentMarker marker);

	/**
	 * A type of page handled by this CMS.
	 */
	enum PageType {
		PRODUCT_PAGE,
		ARTICLE,
		GUIDE,
		LANDING_PAGE
	}
}
