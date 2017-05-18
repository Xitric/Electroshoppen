package cms.business;

import java.awt.image.BufferedImage;

/**
 * Abstract implementation of the {@link DynamicPage} interface.
 *
 * @author Kasper
 */
abstract class AbstractDynamicPage implements DynamicPage {

	/**
	 * The root of the XHTML that makes up the content of this page.
	 */
	private XMLElement content;

	/**
	 * Constructs a new dynamic page with the specified html content.
	 *
	 * @param html the content of the page
	 */
	public AbstractDynamicPage(String html) {
		content = new XMLParser().parse(html);
	}

	@Override
	public void insertHTML(DocumentMarker marker, String html) {
		//Get the element described by the marker
		XMLElement reference = content.getChildByID(marker.getSelectedElementID());

		//Generate a new XMLElement that describes the html
		XMLElement newElement = new XMLParser().parse(html);

		//TODO: Generate a new parent if reference is a "root"
		//Insert this new element after the reference element
		reference.getParent().addChildAfter(newElement, reference);
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

	public static void main(String[] args) {
		DynamicPage p = new Article("<html id=\"h1\"><head></head>" +
				"<body id=\"b1\">" +
				"<form action=\"/action_page.php\" id=\"f1\">" +
				"First name: <input id=\"i1\" type=\"text\" name=\"fname\"/><br/>" +
				"Last name: <input id=\"i2\" type=\"text\" name=\"lname\"/><br/>" +
				"<input id=\"i3\" type=\"submit\" value=\"Submit as normal\"/>" +
				"<input id=\"i4\" type=\"submit\" formtarget=\"_blank\" value=\"Submit to a new window/tab\"/>" +
				"</form>" +
				"<p id=\"p1\"><strong id=\"s1\">Note:</strong> The formtarget attribute of the input tag is not supported in Internet Explorer 9 and earlier versions.</p>" +
				"</body>" +
				"</html>");
		System.out.println(p);
	}
}
