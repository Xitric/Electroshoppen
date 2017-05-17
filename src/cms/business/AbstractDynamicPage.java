package cms.business;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

/**
 * Abstract implementation of the {@link DynamicPage} interface.
 *
 * @author Kasper
 */
abstract class AbstractDynamicPage implements DynamicPage {

	/**
	 * A representation of the xml elements that make up this page.
	 */
	private Document content;

	/**
	 * Constructs a new dynamic page with the specified html content.
	 *
	 * @param html the content of the page
	 * @throws IllegalArgumentException if the html string could not be parsed
	 */
	public AbstractDynamicPage(String html) {
		try {
			//Construct xml document from the html string
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			content = builder.parse(new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8)));

			//We need to specify that the attribute "id" is actually the id of the elements (this id not the default
			//behavior). We could have used a DTD or an XML Schema, but this solution was simpler for a small app
			setIDAttributeRecursively(content);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new IllegalArgumentException("Could not parse html string!", e);
		}
	}

	/**
	 * Specify for this node and all its children that the attribute with the name "id" is the id of the node.
	 *
	 * @param node the node to work on
	 * @see <a href="http://stackoverflow.com/questions/3423430/java-xml-dom-how-are-id-attributes-special#answer-7466809">Java
	 * XML DOM: how are id Attributes special?</a>
	 */
	private void setIDAttributeRecursively(Node node) {
		//Set id attribute of the node
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element element = (Element) node;
			if (element.hasAttribute(ID_ATTRIB)) {
				element.setIdAttribute(ID_ATTRIB, true);
			}
		}

		//Do the same for all the children
		NodeList nl = node.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			setIDAttributeRecursively(nl.item(i));
		}
	}

	@Override
	public void insertHTML(DocumentMarker marker, String html) {

	}

	@Override
	public void removeHTML(DocumentMarker marker) {

	}

	int i = 0;

	@Override
	public void insertText(DocumentMarker marker, String text) {
		Element e = content.createElement("p");
		e.setTextContent(text);
		e.setAttribute("id", "pp" + i++);
		System.out.println(e.getClass().getName());
		content.getElementById(marker.getSelectedElementID()).getParentNode().insertBefore(e, content.getElementById(marker.getSelectedElementID()));
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
		try {
			//Source: http://stackoverflow.com/questions/2325388/what-is-the-shortest-way-to-pretty-print-a-org-w3c-dom-document-to-stdout
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "html"); //Adapted from "xml" to "html"
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(content), new StreamResult(writer));
			return writer.getBuffer().toString();
		} catch (TransformerException e) {
			e.printStackTrace();
			return "Error writing out document!";
		}
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
	}
}
