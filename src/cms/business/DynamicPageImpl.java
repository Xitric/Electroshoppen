package cms.business;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of the {@link DynamicPage} interface.
 *
 * @author Kasper
 */
public class DynamicPageImpl implements DynamicPage {

	/** The start of a link element. */
	private static final String LINK_START = "[@ref=%]";

	/** The end of a link element. */
	private static final String LINK_END = "[@]";

	/**
	 * The id of this dynamic page.
	 */
	private int pageID;

	/**
	 * The next available id for a content element.
	 */
	private int nextID;

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
		pageID = id;
		this.content = new HashMap<>();

		//Parse the content into an xml structure. At the same time, we keep track of the highest used element id, as we
		//need to be able to generate new, unique ids
		int maxID = -1;
		XMLParser parser = new XMLParser();
		for (Map.Entry<String, String> entry : content.entrySet()) {
			this.content.put(entry.getKey(), parser.parse(entry.getValue()));

			//Look through all element id's and find the greatest
			int max = getMaxIdIn(this.content.get(entry.getKey()));
			if (max > maxID) maxID = max;
		}

		//The next available id will either be 0 or one above the current, max id
		nextID = maxID + 1;
	}

	@Override
	public boolean hasValidID() {
		return pageID >= 0;
	}

	@Override
	public int getID() {
		return pageID;
	}

	@Override
	public void setID(int id) {
		if (this.pageID < 0) {
			this.pageID = id;
		}
	}

	@Override
	public XMLElement getContentForID(String id) {
		XMLElement result = content.get(id);

		if (result == null) {
			//If no content could be gathered, create a new content element for the requested id. We create a paragraph
			//element wrapped in a div. Both are assigned new, unique ids
			result = XMLElement.createRoot("div");
			result.setID(String.valueOf(nextID++));
			XMLElement p = result.createChild("p", "Missing content");
			p.setID(String.valueOf(nextID++));

			content.put(id, result);
		}

		return result;
	}

	/**
	 * Get the element in the content that has the specified id. This method is useful, since the page content is stored
	 * in fractions inside a map, and we want to operate on this content as a whole.
	 *
	 * @param id the id of the element to get
	 * @return the element with the specified id, or null if no such element exists
	 */
	private XMLElement getContentElementByID(String id) {
		for (XMLElement root : content.values()) {
			//Return as soon as we found a match
			if (root.getID().equals(id)) return root;

			//Test children
			XMLElement result = root.getChildByID(id);
			if (result != null) return result;
		}

		//Nothing was found, return null
		return null;
	}

	/**
	 * Go through all numeric ids in the specified element and record the maximum id. This will disregard all
	 * non-numeric ids, which should be fine, as they cannot interfere with the automatic id generation.
	 *
	 * @param element the element to scan through
	 * @return the maximum recorded numeric id in the specified element. A value of -1 indicates that no numeric ids
	 * were found
	 */
	private int getMaxIdIn(XMLElement element) {
		int max = -1;
		String elementIDString = element.getID();

		//If the element has a numeric id, and it is greater than max, record it
		if (elementIDString != null) {
			try {
				int elementID = Integer.parseInt(elementIDString);
				if (elementID > max) max = elementID;
			} catch (NumberFormatException e) {
				//Non-numeric id, so we ignore it
			}
		}

		//Scan through children using recursion
		for (XMLElement child : element.getChildren()) {
			int childMax = getMaxIdIn(child);
			if (childMax > max) max = childMax;
		}

		//Return whatever the value of max is
		return max;
	}

	/**
	 * Add new, unique ids to all elements in the in the tree described by the XMLElement. Existing ids are overwritten,
	 * to ensure that the user does not supply an already used id.
	 *
	 * @param element the element to scan through
	 */
	private void setUniqueIDs(XMLElement element) {
		element.setID(String.valueOf(nextID++));

		//Go through children using recursion
		for (XMLElement child : element.getChildren()) {
			setUniqueIDs(child);
		}
	}

	/**
	 * Helper method for inserting an xml element. This will automatically enrich the element and all of its children
	 * with unique ids.
	 *
	 * @param marker  the location to insert the element into
	 * @param element the element to insert
	 */
	private void insertElement(DocumentMarker marker, XMLElement element) {
		//Get the reference element described by the marker
		XMLElement reference = getContentElementByID(marker.getSelectedElementID());
		if (reference == null) return; //Not a valid selection

		//Insert the new element before, in, or after the selection. It should be safe to access the parent, as all
		//content is wrapped in divs, that are not sent to the gui. Also, we enrich the element with unique ids
		setUniqueIDs(element);
		if (marker.getDirection() == DocumentMarker.Direction.BEFORE) {
			reference.getParent().addChildBefore(element, reference);
		} else if (marker.getDirection() == DocumentMarker.Direction.AFTER) {
			reference.getParent().addChildAfter(element, reference);
		} else if (marker.getDirection() == DocumentMarker.Direction.IN) {
			//We can only insert into an element if it contains no text
			if (!reference.hasTextContent()) {
				reference.addChild(element);
			}
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
		//Create an XMLElement describing the text
		XMLElement p = XMLElement.createRoot("p", text);

		insertElement(marker, p);
	}

	@Override
	public void insertImage(DocumentMarker marker, BufferedImage image) {
		//Create an XMLElement describing the image
		XMLElement img = XMLElement.createRoot("img");

		//Attempt to transform the buffered image to a format supported in the web view
		try {
			//Source: http://stackoverflow.com/questions/22984430/javafx2-webview-and-in-memory-images#answer-37215917
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			ImageIO.write(image, "PNG", output);
			String imgData = "data:image/png;base64," + Base64.getMimeEncoder().encodeToString(output.toByteArray());
			img.setAttribute("src", imgData);

			insertElement(marker, img);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setTextLink(DocumentMarker marker, int link) {
		XMLElement reference = getContentElementByID(marker.getSelectedElementID());
		if (reference == null || !marker.hasRangeSelection()) return; //Not a valid selection

		String current = reference.getTextContent();
		if (hasLinkInRange(current, marker.getStartSelection(), marker.getEndSelection()))
			throw new IllegalArgumentException("Links cannot overlap!");

		//We have ensured that the user has specified sufficient and legal information, so we insert a new link at the
		//marker
		String result = current.substring(0, marker.getStartSelection());
		result += LINK_START.replace("%", String.valueOf(link));
		result += marker.getRangeSelection();
		result += LINK_END;
		result += current.substring(marker.getEndSelection());

		//Insert into reference element
		reference.setTextContent(result);
	}

	/**
	 * Tests if there is a link description in the specified string in the specified range.
	 *
	 * @param text  the string to test
	 * @param start the beginning of the range, inclusive
	 * @param end   the end of the range, exclusive
	 * @return true if a link was detected, false otherwise
	 */
	private boolean hasLinkInRange(String text, int start, int end) {
		Matcher startMatcher = Pattern.compile("(\\[@ref=)[\\w]+(])").matcher(text);
		Matcher endMatcher = Pattern.compile("([@])").matcher(text);

		//Loop through all links in the text
		int startLink = 0;
		int endLink = 0;
		while (startMatcher.find(endLink)) {
			startLink = startMatcher.start();

			if (endMatcher.find()) {
				endLink = endMatcher.start(startLink);

				//An existing link has been located, so we test for collision
				if (start < end && end > start) return true; //Collision detected
			}
		}

		//No collision detected
		return false;
	}

	@Override
	public void removeElement(DocumentMarker marker) {
		//Get the reference element described by the marker
		XMLElement reference = getContentElementByID(marker.getSelectedElementID());
		if (reference == null) return; //Not a valid selection

		//Remove selection from parent
		reference.getParent().removeChild(reference);
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
