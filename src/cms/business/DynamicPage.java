package cms.business;

import shared.Image;

/**
 * Interface describing a page, that can be generated by the CMS.
 *
 * @author Kasper
 */
public interface DynamicPage {

	/**
	 * The name of the attribute to use as id.
	 */
	String ID_ATTRIB = "id";

	/** The start of a link element. */
	String LINK_START = "[@ref=%]";

	/** The end of a link element. */
	String LINK_END = "[@]";

	/**
	 * Test whether the id of this page is valid.
	 *
	 * @return true if the id is valid, false otherwise
	 */
	boolean hasValidID();

	/**
	 * Get the id of this page.
	 *
	 * @return the id of this page
	 */
	int getID();

	/**
	 * Set the id of this page. This operation will be ignored if the id is already set. The purpose of this method is
	 * to allow the persistence layer to assign an id to a page created in the domain layer.
	 *
	 * @param id the id of the page
	 */
	void setID(int id);

	/**
	 * Set the name of this page.
	 *
	 * @param name the new name
	 */
	void setName(String name);

	/**
	 * Get the name of this page.
	 *
	 * @return the name of this page
	 */
	String getName();

	/**
	 * Get the content for the template element with the specified id. The content will be wrapped in a {@code <div>}
	 * element.
	 *
	 * @param id the id of the template element
	 * @return the content for the template element with the specified id
	 */
	XMLElement getContentForID(String id);

	/**
	 * Insert the specified html markup at the location specified by the {@link DocumentMarker}.
	 *
	 * @param marker the location to insert the markup into
	 * @param html   the markup to insert
	 */
	void insertHTML(DocumentMarker marker, String html);

	/**
	 * Insert the specified text at the location specified by the {@link DocumentMarker}.
	 *
	 * @param marker the location to insert the text into
	 * @param text   the text to insert
	 */
	void insertText(DocumentMarker marker, String text);

	/**
	 * Insert the specified image at the location specified by the {@link DocumentMarker}.
	 *
	 * @param marker the location to insert the image into
	 * @param image  the image to insert
	 */
	void insertImage(DocumentMarker marker, Image image);

	/**
	 * Replace the text at the location specified by the {@link DocumentMarker} with the specified link.
	 *
	 * @param marker the location of the text to replace
	 * @param link   the id of the page to link to
	 * @throws IllegalArgumentException if the new link overlaps an existing link
	 */
	void setTextLink(DocumentMarker marker, int link);

	/**
	 * Remove the element at the location specified by the {@link DocumentMarker}.
	 *
	 * @param marker the location to remove the element from
	 */
	void removeElement(DocumentMarker marker);

	/**
	 * Test if the element with the specified id is a text element.
	 *
	 * @param id the id of the element
	 * @return true if the element if a text element, false otherwise
	 */
	boolean isTextElement(String id);

	/**
	 * Get the text from the specified element.
	 *
	 * @param id the id of the element
	 * @return the text in the specified element
	 * @throws IllegalArgumentException if the element is not a text element
	 * @see #isTextElement(String)
	 */
	String getTextFromElement(String id);

	/**
	 * Set the text of the element with the specified id.
	 *
	 * @param id   the id of the element
	 * @param text the text to set
	 * @throws IllegalArgumentException if the element is not a text element
	 * @see #isTextElement(String)
	 */
	void setText(String id, String text);
}
