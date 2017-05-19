package cms.business;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A representation of a static page layout that is used as the basis for creating pages in the CMS.
 *
 * @author Niels
 * @author Kasper
 */
public class Template {

	private final String type;
	private final XMLElement template;
	private int id;

	/**
	 * Constructs a new template from the specified html layout.
	 *
	 * @param id   the id of this template, or -1 if it has no id yet
	 * @param type the type of page that this template is intended for
	 * @param html the html layout
	 */
	public Template(int id, String type, String html) {
		this.id = id;
		this.type = type;
		template = new XMLParser().parse(html);
	}

	/**
	 * Test whether the id of this template is valid.
	 *
	 * @return true if the id is valid, false otherwise
	 */
	public boolean hasValidID() {
		return id >= 0;
	}

	/**
	 * Get the id of this template.
	 *
	 * @return the id of this template
	 */
	public int getID() {
		return id;
	}

	/**
	 * Set the id of this template. This operation will be ignored if the id is already set. The purpose of this method
	 * is to allow the persistence layer to assign an id to a template created in the domain layer.
	 *
	 * @param id the id of the template
	 */
	public void setID(int id) {
		if (this.id < 0) {
			this.id = id;
		}
	}

	/**
	 * Get the type of the page that this template is intended for.
	 *
	 * @return the type of the page that this template is intended for
	 */
	public String getType() {
		return type;
	}

	/**
	 * Get the ids of the container elements in this template.
	 *
	 * @return the ids of the container elements in this template
	 */
	public Set<String> getElementIDs() {
		return getElementIDs(template);
	}

	/**
	 * Internal method for getting the container ids recursively.
	 *
	 * @param element the element to scan through
	 * @return the ids in the specified element
	 */
	private Set<String> getElementIDs(XMLElement element) {
		if (element.getID() != null) {
			//If this element has an id, then no child elements can legally have ids too
			return Collections.singleton(element.getID());
		} else {
			//Recursively get the ids of the children
			Set<String> idSet = new HashSet<>();
			for (XMLElement child : element.getChildren()) {
				idSet.addAll(getElementIDs(child));
			}
			return idSet;
		}
	}

	/**
	 * Use the content of the specified dynamic page along with this template to create a complete web page.
	 *
	 * @param page the page to use
	 * @return a complete web page containing the content of the specified dynamic page
	 */
	public XMLElement enrichPage(DynamicPage page) {
		Set<String> ids = getElementIDs();

		//We want to retain the template, so we make a copy of it
		XMLElement templateCopy = template.clone();
		for (String id : ids) {
			XMLElement child = templateCopy.getChildByID(id);
			child.clear();
			//We clone the content from the dynamic page to prevent changing the parent of the initial content
			child.addChildren(page.getContentForID(id).clone().getChildren());
		}

		return templateCopy;
	}
}
