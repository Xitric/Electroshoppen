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
import org.w3c.dom.html.HTMLElement;

import java.util.function.Consumer;

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
			"var lastElement;" +
			"function bodyClick(event) {" +
			"   var element = event.srcElement;" +
			"   element = element.nodeType? element : element.parentNode;" +
			"   if(element == lastElement) { return; }" +
			"   if(!element.classList.contains('nonselectable')) {" +
			"       var inserters = document.querySelectorAll('.inserterh,.inserterv');" +
			"       for (i = inserters.length - 1; i >= 0; i--) {" +
			"          inserters[i].parentNode.removeChild(inserters[i]);" +
			"       }" +
			"       setSelected(element.id);" +
			"       lastElement = element;" +
			"   } else {" +
			"       if (element.classList.contains('inserteraft')) {" +
			"           controller.insertOnAction(1, window.getSelection());" +
			"       } else if (element.classList.contains('inserterbef')) {" +
			"           controller.insertOnAction(-1, window.getSelection());" +
			"       } else if (element.classList.contains('inserterin')) {" +
			"           controller.insertOnAction(0, window.getSelection());" +
			"       }" +
			"   }" +
			"} " +
			"function setSelected(elementID) {" +
			"   var selections = document.getElementsByClassName('selectedElement');" +
			"   for (i = selections.length - 1; i >= 0; i--) {" +
			"       selections[i].classList.remove('selectedElement');" +
			"   }" +
			"   element = document.getElementById(elementID);" +
			"   if (element) {" +
			"       element.classList.add('selectedElement');" +
			"       if(!element.classList.contains('nonselectable')) {" +
			"           element.insertAdjacentHTML('afterbegin', '&lt;div class=\"inserterv nonselectable\"&gt;&lt;button class=\"inserterin nonselectable\"&gt;&lt;/button&gt;&lt;/div&gt;');" +
			"           if (! element.parentNode.classList.contains('nonselectable')) {" +
			"               element.insertAdjacentHTML('afterbegin', '&lt;div class=\"inserterh nonselectable\"&gt;&lt;button class=\"inserterbef nonselectable\"&gt;&lt;/button&gt;&lt;/div&gt;');" +
			"               element.insertAdjacentHTML('beforeend', '&lt;div class=\"inserterh nonselectable\"&gt;&lt;button class=\"inserteraft nonselectable\"&gt;&lt;/button&gt;&lt;/div&gt;');" +
			"           }" +
			"       }" +
			"   }" +
			"   controller.selectionChanged(element);" +
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

	private final Consumer<DocumentMarker> insertListener;
	private final ObjectProperty<HTMLElement> currentSelection;
	private final WebView webView;

	/**
	 * Constructs a new web view for selecting html elements. By default this web view will be empty.
	 */
	public SelectableWebView(Consumer<DocumentMarker> insertListener) {
		this.insertListener = insertListener;
		currentSelection = new SimpleObjectProperty<>();
		webView = new WebView();
		getChildren().add(webView);
	}

	/**
	 * Set the html markup to display. The markup must contain a head and a body element. Both of these elements can
	 * contain any child elements.
	 *
	 * @param html          the html markup to display
	 * @param keepSelection true if the web view should preserve the current selection, false otherwise
	 * @throws IllegalArgumentException if the html markup is badly formatted
	 */
	public void setContent(String html, boolean keepSelection) {
		HTMLElement oldSelection = selectedElementProperty().getValue();

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

				//Preserve selection
				if (keepSelection) select(oldSelection);
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
		if (selection instanceof HTMLElement) {
			HTMLElement element = (HTMLElement) selection;

			//Ignore if the user selected the same element
			if (element != currentSelection.getValue()) {

				//Change selection
				currentSelection.setValue(element);
			}
		}
	}

	/**
	 * Force selection of the specified element. This element must be in the web view, or this method will do nothing.
	 * If null is passed as a parameter, the current selection will be removed.
	 *
	 * @param element the element to select, or null to remove the current selection
	 */
	public void select(HTMLElement element) {
		//Delegate call to javascript
		if (element == null) {
			webView.getEngine().executeScript("setSelected(null);");
		} else {
			webView.getEngine().executeScript("setSelected(" + element.getId() + ");");
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

	/**
	 * Called when the user presses one of the insert buttons in the web view.
	 *
	 * @param direction -1 for inserting before, 1 for inserting after and 0 for inserting into
	 */
	@SuppressWarnings("unused")
	public void insertOnAction(int direction, String selection) {
		//Get the direction object
		DocumentMarker.Direction dir;
		if (direction < 0) {
			dir = DocumentMarker.Direction.BEFORE;
		} else if (direction > 0) {
			dir = DocumentMarker.Direction.AFTER;
		} else {
			dir = DocumentMarker.Direction.IN;
		}

		//Create document marker to describe selection
		DocumentMarker marker = new DocumentMarker(selectedElementProperty().getValue(), selection, dir);

		//Call listener
		insertListener.accept(marker);
	}

	public void log(String text) {
		System.out.println(text);
	}
}
