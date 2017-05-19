package cms.presentation;

import cms.business.DocumentMarker;
import cms.business.XMLElement;
import cms.business.XMLParser;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Worker;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLElement;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * A wrapper for a {@link WebView} that lets the user select html elements on the web page.
 *
 * @author Kasper
 * @author Mikkel
 */
public class SelectableWebView extends StackPane {

	/**
	 * The script that is responsible for handling element selection and forwarding the events to this controller.
	 */
	private static final String selectScript = "<script>" +
			"function bodyClick(event) {" +
			"   var element = event.srcElement;" +
			"   element = element.nodeType? element : element.parentNode;" +
			"   if(!element.classList.contains('nonselectable')) {" +
			//			"       if (!element.parentNode.classList.contains('nonselectable')) {" +
			"           var inserters = document.getElementsByClassName(\"inserter\");" +
			"           for (i = inserters.length - 1; i >= 0; i--) {" +
			"               inserters[i].parentNode.removeChild(inserters[i]);" +
			"           }" +
			//			"       }" +
			"       controller.selectionChanged(element, window.getSelection());" +
			"   }" +
			"}" +
			"document.addEventListener('DOMContentLoaded', function() {" +
			"   document.body.addEventListener('click', bodyClick, true);" +
			"});" +
			"</script>";
	private static final String stylesheet =
			"<link rel=\"stylesheet\" type=\"text/css\" href=\"" + SelectableWebView.class.getResource("selectablewebview.css") + "\"/>";

	/**
	 * The name of the class to apply to selected elements to highlight them.
	 */
	private static final String selectedClass = "selectedElement";

	private ObjectProperty<HTMLElement> currentSelection;
	private WebView webView;

	/**
	 * Constructs a new web view for selecting html elements. By default this web view will be empty.
	 */
	public SelectableWebView() {
		currentSelection = new SimpleObjectProperty<>();
		webView = new WebView();
		getChildren().add(webView);
	}

	public static void printDocument(Document doc, OutputStream out) {
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "html");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			transformer.transform(new DOMSource(doc),
					new StreamResult(new OutputStreamWriter(out, "UTF-8")));
		} catch (UnsupportedEncodingException | TransformerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set the html markup to display. The markup must contain a head and a body element. Both of these elements can
	 * contain any child elements.
	 *
	 * @param html the html markup to display
	 * @throws IllegalArgumentException if the html markup is badly formatted
	 */
	public void setContent(String html) {
		//Detect head element of html
		XMLElement root = new XMLParser().parse(html);
		XMLElement head = root.getChildrenByTag("head").get(0);
		if (head == null) {
			throw new IllegalArgumentException("Invalid html: No head element detected!");
		}

		//Inject script into head
		XMLElement script = new XMLParser().parse(selectScript);
		head.addChild(script);
		XMLElement style = new XMLParser().parse(stylesheet);
		head.addChild(style);

		//Show result in web view
		webView.getEngine().loadContent(root.toString());
		webView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.equals(Worker.State.SUCCEEDED)) {
				bindDocumentToController();
			}
		});
	}

	/**
	 * Bind this controller to the javascript objects in the web view.
	 */
	private void bindDocumentToController() {
		JSObject window = (JSObject) webView.getEngine().executeScript("window");
		window.setMember("controller", this);
	}

	/**
	 * Called by the javascript object in the web view when the user makes a selection.
	 *
	 * @param selection the user's selection
	 */
	@SuppressWarnings("unused")
	public void selectionChanged(Object selection, Object range) {
		HTMLElement element = (HTMLElement) selection;

		//TODO: Before vs after
		DocumentMarker marker = new DocumentMarker((HTMLElement) selection, range.toString(), true);
		//			if (marker.hasRangeSelection()) {
		//				System.out.println("You selected part of an element: " + marker.getRangeSelection());
		//				System.out.println("The selection begins at index " + marker.getStartSelection() + " and ends at " + marker.getEndSelection());
		//			} else {
		//				System.out.println("You selected the entire element!");
		//			}
		//
		//			System.out.println("Selected element: " + marker.getSelectedElementID());
		//			System.out.println("--------------------------------------------------------------------------");

		selectInternal((HTMLElement) selection);
	}

	/**
	 * Force selection of the specified element. This element must be in the web view, or this method will do nothing.
	 *
	 * @param element the element to select
	 */
	public void select(HTMLElement element) {
		//Ensure that the specified element is actually in the DOM
		NodeList possibilities = webView.getEngine().getDocument().getElementsByTagName(element.getTagName());
		boolean success = false;
		for (int i = 0; i < possibilities.getLength(); i++) {
			if (possibilities.item(i) == element) {
				success = true;
				break;
			}
		}

		//Return if the element was not in the DOM
		if (!success) return;

		//The element was in the DOM, so we can safely select it
		selectInternal(element);
	}

	/**
	 * Internal method for changing the selection. If the specified element is the current selection, this method will
	 * do nothing.
	 *
	 * @param element the new element to select
	 */
	private void selectInternal(HTMLElement element) {
		//Ignore if the user selected the same element
		if (element != currentSelection.getValue()) {

			//Remove selection class from previous selection
			if (currentSelection.getValue() != null) {
				String currentClassName = currentSelection.getValue().getClassName();
				int breakIndex = currentClassName.indexOf(selectedClass);
				String oldClassName = currentClassName.substring(0, breakIndex) + currentClassName.substring(breakIndex + selectedClass.length());
				currentSelection.getValue().setClassName(oldClassName);
			}

			//Change selection
			currentSelection.setValue(element);

			//Add selection class to new selection
			element.setClassName(element.getClassName() + " " + selectedClass);
		}

		//Insertion buttons. This code has been extracted from the script area, because there seems to be a problem with
		//the innerHTML property and the JavaFX web view
		if (((HTMLElement) element.getParentNode()).getClassName() == null || !((HTMLElement) element.getParentNode()).getClassName().contains("nonselectable")) {
			String innerHTML = (String) webView.getEngine().executeScript("document.getElementById('" + element.getId() + "').innerHTML");
			webView.getEngine().executeScript("var element = document.getElementById('" + element.getId() + "'); element.innerHTML = '<div class=\"inserter nonselectable\"><button class=\"inserterbef nonselectable\">+</button></div>' + '" + innerHTML + "';" +
					"element.innerHTML += '<div class=\"inserter nonselectable\"><button class=\"inserteraft nonselectable\">+</button></div>';");
		}
	}

	/**
	 * Get the selection property.
	 *
	 * @return the selection property
	 */
	public ReadOnlyObjectProperty<HTMLElement> selectedElementProperty() {
		return currentSelection;
	}
}
