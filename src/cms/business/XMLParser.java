package cms.business;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;

/**
 * A parser for reading xml content and constructing trees of {@link XMLElement XMLElements}.
 *
 * @author Kasper
 */
public class XMLParser extends DefaultHandler {

	private final StringBuilder contentAccumulator;
	private XMLElement currentElement;

	/**
	 * Constructs a new parser for xml content.
	 */
	public XMLParser() {
		contentAccumulator = new StringBuilder();
	}

	/**
	 * Parse the specified file using this XML parser.
	 *
	 * @param file the file to parse
	 * @return the root element of the xml content
	 * @throws IllegalArgumentException if an error occurred while parsing the file
	 */
	public XMLElement parse(File file) {
		try {
			return parse(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Could not parse file " + file.getAbsolutePath(), e);
		}
	}

	/**
	 * Parse the specified string using this XML parser.
	 *
	 * @param string the string to parse
	 * @return the root element of the xml content
	 * @throws IllegalArgumentException if an error occurred while parsing the string
	 */
	public XMLElement parse(String string) {
		return parse(new ByteArrayInputStream(string.getBytes()));
	}

	/**
	 * Parse the specified input stream using this XML parser.
	 *
	 * @param inputStream the input stream to parse
	 * @return the root element of the xml content
	 * @throws IllegalArgumentException if an error occurred while parsing the input stream
	 */
	public XMLElement parse(InputStream inputStream) {
		try {
			//Attempt on acquiring a SAXParser to read the input stream
			SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();

			//Parse input stream using this as a handler
			saxParser.parse(inputStream, this);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new IllegalArgumentException("Error parsing input stream!", e);
		}

		return currentElement;
	}

	@Override
	public void startDocument() throws SAXException {
		//Reset all parsing data
		currentElement = null;
		contentAccumulator.setLength(0);
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		//If this is the first element, create a root element
		if (currentElement == null) {
			currentElement = XMLElement.createRoot(qName, null);
		} else {
			currentElement = currentElement.createChild(qName, null);
		}

		//Add attributes
		for (int i = 0; i < attributes.getLength(); i++) {
			currentElement.setAttribute(attributes.getQName(i), attributes.getValue(i));
		}
	}

	public void characters(char ch[], int start, int length) throws SAXException {
		//Read characters
		contentAccumulator.append(ch, start, length);
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		//Store element content, if any
		String content = contentAccumulator.toString().trim();
		currentElement.setTextContent(content);

		//Reset content accumulator for new run
		contentAccumulator.setLength(0);

		//The current element is now the parent, unless the parent is null, which means we reached the end of the xml
		//and the current element should be left unchanged
		if (currentElement.getParent() != null) {
			currentElement = currentElement.getParent();
		}
	}
}
