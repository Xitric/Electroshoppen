package cms.business;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the {@link DynamicPage} interface.
 *
 * @author Kasper
 */
public class DynamicPageImpl implements DynamicPage {

	private int id;

	/**
	 * The roots of the XHTML that makes up the content of this page along with the ids of those elements to associate
	 * the content with in the template.
	 */
	private Map<String, XMLElement> content;

	/**
	 * Constructs a new dynamic page with the specified html content.
	 *
	 * @param id      the id of this dynamic page, or -1 if it has no id yet
	 * @param content the content of the page
	 */
	public DynamicPageImpl(int id, Map<String, String> content) {
		this.content = new HashMap<>();
		XMLParser parser = new XMLParser();

		for (Map.Entry<String, String> entry : content.entrySet()) {
			this.content.put(entry.getKey(), parser.parse(entry.getValue()));
		}
	}

	@Override
	public boolean hasValidID() {
		return id >= 0;
	}

	@Override
	public int getID() {
		return id;
	}

	@Override
	public void setID(int id) {
		if (this.id < 0) {
			this.id = id;
		}
	}

	@Override
	public XMLElement getContentForID(String id) {
		XMLElement result = content.get(id);

		if (result == null) {
			//If no content could be gathered, return a paragraph element wrapped in a div
			result = XMLElement.createRoot("div");
			XMLElement p = result.createChild("p", "Missing content");
			p.setID("missingElement" + id);
		}

		return result;
	}

	@Override
	public void insertHTML(DocumentMarker marker, String html) {


		//		//Get the element described by the marker
		//		XMLElement reference = content.getChildByID(marker.getSelectedElementID());
		//
		//		//Generate a new XMLElement that describes the html
		//		XMLElement newElement = new XMLParser().parse(html);
		//
		//		//TODO: Generate a new parent if reference is a "root"
		//		//Insert this new element after the reference element
		//		reference.getParent().addChildAfter(newElement, reference);
	}

	@Override
	public void removeHTML(DocumentMarker marker) {

	}

	@Override
	public void insertText(DocumentMarker marker, String text) {

	}

	@Override
	public void removeText(DocumentMarker marker) {

	}

	@Override
	public void insertImage(DocumentMarker marker, BufferedImage image) {

	}

	@Override
	public void removeImage(DocumentMarker marker) {

	}

	@Override
	public String getTextSelection(DocumentMarker marker) {
		return null;
	}

	@Override
	public Link getLinkSelection(DocumentMarker marker) {
		return null;
	}

	@Override
	public void setTextLink(DocumentMarker marker, Link link) {

	}

	@Override
	public void removeLink(DocumentMarker marker) {

	}

	/**
	 * Get a string representation of this dynamic page. This will return the html markup of the page.
	 *
	 * @return the html markup of this page
	 */
	@Override
	public String toString() {
		return content.toString();
	}
}
