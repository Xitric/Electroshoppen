package cms.business;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
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

	/**
	 * Get the element in the content that has the specified id. This method is useful, since the page content is stored
	 * in fractions inside a map.
	 *
	 * @param id the id of the element to get
	 * @return the element with the specified id, or null if no such element exists
	 */
	private XMLElement getContentElementByID(String id) {
		for (XMLElement root : content.values()) {
			XMLElement result = root.getChildByID(id);

			//Return as soon as we found a match
			if (result != null) return result;
		}

		//Nothing was found, return null
		return null;
	}

	/**
	 * Helper method for inserting an xml element.
	 *
	 * @param marker  the location to insert the element into
	 * @param element the element to insert
	 */
	private void insertElement(DocumentMarker marker, XMLElement element) {
		//Get the reference element described by the marker
		XMLElement reference = getContentElementByID(marker.getSelectedElementID());
		if (reference == null) return; //Not a valid selection

		//Insert the new element before or after the selection. It should be safe to access the parent, as all content
		//is wrapped in divs, that are not sent to the gui
		if (marker.pointsToBefore()) {
			reference.getParent().addChildBefore(element, reference);
		} else {
			reference.getParent().addChildAfter(element, reference);
		}
	}

	@Override
	public void insertHTML(DocumentMarker marker, String html) {
		//Generate a new XMLElement that describes the html
		XMLElement newElement = new XMLParser().parse(html);

		insertElement(marker, newElement);
	}

	@Override
	public void insertText(DocumentMarker marker, String text) {
		//TODO: ID generation
		//Create an XMLElement describing the text
		XMLElement p = XMLElement.createRoot("p", text);

		insertElement(marker, p);
	}

	@Override
	public void insertImage(DocumentMarker marker, BufferedImage image) {
		//TODO: ID generation
		//Create an XMLElement describing the image
		XMLElement img = XMLElement.createRoot("img");

		//Attempt to transform the buffered image to a format supported in the web view
		try {
			//Source: http://stackoverflow.com/questions/22984430/javafx2-webview-and-in-memory-images#answer-37215917
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			ImageIO.write(image, "PNG", output);
			String imgData = "data:base64," + Base64.getMimeEncoder().encodeToString(output.toByteArray());
			img.setAttribute("src", imgData);

			insertElement(marker, img);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void removeElement(DocumentMarker marker) {
		//Get the reference element described by the marker
		XMLElement reference = getContentElementByID(marker.getSelectedElementID());
		if (reference == null) return; //Not a valid selection

		//Remove selection from parent
		reference.getParent().removeChild(reference);
	}

	//TODO: Is this irrelevant?
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
