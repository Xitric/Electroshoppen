package cms.presentation;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Worker;
import javafx.scene.layout.Region;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLElement;

/**
 * A wrapper for a {@link WebView} that lets the user select html elements on the web page.
 *
 * @author Kasper
 */
public class SelectableWebView extends Region {

	/**
	 * The script that is responsible for handling element selection and forwarding the events to this controller.
	 */
	private static final String selectScript = "<script>" +
			"function bodyClick(event) {" +
			"var element = event.srcElement;" +
			"controller.selectionChanged(element.nodeType == 1? element : element.parentNode);" +
			"}" +
			"document.addEventListener('DOMContentLoaded', function() {" +
			"document.body.addEventListener('click', bodyClick, true);" +
			"});" +
			"</script>" +
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

	/**
	 * Set the html markup to display. The markup must contain a head and a body element. Both of these elements can
	 * contain any child elements.
	 *
	 * @param html the html markup to display
	 * @throws IllegalArgumentException if the html markup is badly formatted
	 */
	public void setContent(String html) {
		//Detect the end of the head element
		int index = html.indexOf("</head");
		if (index == -1) throw new IllegalArgumentException("Invalid html: No head element detected!");

		//Inject selection script
		String adaptedHtml = html.substring(0, index) + selectScript + html.substring(index);

		//Load up html and bind this controller to it
		webView.getEngine().loadContent(adaptedHtml);
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
	public void selectionChanged(Object selection) {
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
		if (element == currentSelection.getValue()) return;

		//Remove selection class from previous selection
		if (currentSelection.getValue() != null) {
			String currentClassName = currentSelection.getValue().getClassName();
			int breakIndex = currentClassName.indexOf(selectedClass);
			String oldClassName = currentClassName.substring(0, breakIndex) + currentClassName.substring(breakIndex + selectedClass.length());
			currentSelection.getValue().setClassName(oldClassName);
			//TODO: Handle repeating spaces
		}

		//Change selection
		currentSelection.setValue(element);

		//Add selection class to new selection
		element.setClassName(element.getClassName() + " " + selectedClass);
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
