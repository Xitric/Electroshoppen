package cms.business;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An element in an xml structure. An element can be the root element, it can have children, text content, and
 * attributes. This class has been specifically tuned to handle XHTML.
 * <p>
 * This is a class that I have developed over a long period of time, taking inspiration from many sources in both code
 * and on the Internet, and adding to it my own ideas.
 *
 * @author Kasper
 */
public class XMLElement implements Cloneable {

	/**
	 * The name of the attribute to consider as the id of an element.
	 */
	private static final String ID_ATTRIBUTE = "id";

	/**
	 * The name of the attribute to consider as the classes of an element.
	 */
	private static final String CLASS_ATTRIBUTE = "class";

	/**
	 * The symbol to use for separating classes in a list of classes on an element.
	 */
	private static final String CLASS_SEPARATOR = " ";

	/**
	 * The name of the tag of this xml element.
	 */
	private final String tagName;
	/**
	 * The child elements of this xml element.
	 */
	private List<XMLElement> children;
	/**
	 * The attributes of this element.
	 */
	private Map<String, String> attributes;
	/**
	 * The parent of this element. If the parent is null, this element is the root element.
	 */
	private XMLElement parent;
	/**
	 * The text inside this element.
	 */
	private String textContent;

	/**
	 * Constructs a new xml element.
	 *
	 * @param tagName     the name of the element tag, must not be null
	 * @param textContent the text content, if any
	 * @param attributes  the map of attributes, or null if it has none
	 * @param parent      the parent of this element, if any
	 * @throws IllegalArgumentException if the tag name is null or empty
	 */
	private XMLElement(String tagName, String textContent, Map<String, String> attributes, XMLElement parent) {
		if (tagName == null || tagName.isEmpty())
			throw new IllegalArgumentException("Cannot create an xml element with an unspecified tag!");

		this.tagName = tagName;
		this.textContent = (textContent == null ? "" : textContent);
		this.attributes = (attributes == null ? new HashMap<>() : new HashMap<>(attributes));
		this.parent = parent;
		this.children = new ArrayList<>();
	}

	/**
	 * Get a new root xml element.
	 *
	 * @param tagName the name of the element tag, must not be null
	 * @return a root xml element
	 * @throws IllegalArgumentException if the tag name is null or empty
	 */
	public static XMLElement createRoot(String tagName) {
		return new XMLElement(tagName, null, null, null);
	}

	/**
	 * Get a new root xml element.
	 *
	 * @param tagName     the name of the element tag, must not be null
	 * @param textContent the text content, if any
	 * @return a root xml element
	 * @throws IllegalArgumentException if the tag name is null or empty
	 */
	public static XMLElement createRoot(String tagName, String textContent) {
		return new XMLElement(tagName, textContent, null, null);
	}

	/**
	 * Get a new root xml element.
	 *
	 * @param tagName     the name of the element tag, must not be null
	 * @param textContent the text content, if any
	 * @param attributes  the attributes of this element
	 * @return a root xml element
	 * @throws IllegalArgumentException if the tag name is null or empty
	 */
	public static XMLElement createRoot(String tagName, String textContent, Map<String, String> attributes) {
		return new XMLElement(tagName, textContent, attributes, null);
	}

	/**
	 * Create a child element for this xml element. The element is automatically added to the end of this element's
	 * children.
	 *
	 * @param tagName the name of the element tag, must not be null
	 * @return the created child element
	 * @throws IllegalArgumentException if the tag name is null or empty
	 */
	public XMLElement createChild(String tagName) {
		XMLElement child = createRoot(tagName);
		this.addChild(child);
		return child;
	}

	/**
	 * Create a child element for this xml element. The element is automatically added to the end of this element's
	 * children.
	 *
	 * @param tagName     the name of the element tag, must not be null
	 * @param textContent the text content, if any
	 * @return the created child element
	 * @throws IllegalArgumentException if the tag name is null or empty
	 */
	public XMLElement createChild(String tagName, String textContent) {
		XMLElement child = createRoot(tagName, textContent);
		this.addChild(child);
		return child;
	}

	/**
	 * Create a child element for this xml element. The element is automatically added to the end of this element's
	 * children.
	 *
	 * @param tagName     the name of the element tag, must not be null
	 * @param textContent the text content, if any
	 * @param attributes  the attributes of this element
	 * @return the created child element
	 * @throws IllegalArgumentException if the tag name is null or empty
	 */
	public XMLElement createChild(String tagName, String textContent, Map<String, String> attributes) {
		XMLElement child = createRoot(tagName, textContent, attributes);
		this.addChild(child);
		return child;
	}

	/**
	 * Add the specified element as a child to this xml element. The new element will be added to the end of this
	 * element's list of children. The new element must not already be among the children of this xml element, be the
	 * element itself, and it must not be a parent of this element. The element will be removed from its current parent,
	 * if it has any.
	 *
	 * @param element the element to add
	 * @throws IllegalArgumentException if the new child element is already a child of this xml element, if the new
	 *                                  child element is the element itself, or if the new child element is a parent of
	 *                                  this xml element
	 */
	public void addChild(XMLElement element) {
		addChild(element, children.size());
	}

	/**
	 * Add the specified element as a child to this xml element at the specified index. This index must be within the
	 * range of this element's children. Also, the new element must not already be among the children of this xml
	 * element, be the element itself, and it must not be a parent of this element. The element will be removed from its
	 * current parent, if it has any.
	 *
	 * @param element the element to add
	 * @param index   the position to add the new element into
	 * @throws IllegalArgumentException  if the new child element is already a child of this xml element, if the new
	 *                                   child element is the element itself, or if the new child element is a parent of
	 *                                   this xml element
	 * @throws IndexOutOfBoundsException if the index is out of range
	 */
	public void addChild(XMLElement element, int index) {
		//Ensure that we are not adding an element to itself
		if (element == this) throw new IllegalArgumentException("Cannot add element to itself!");

		//Ensure that the new element is not among the current children
		if (children.contains(element)) throw new IllegalArgumentException("Cannot add element multiple times!");

		//Ensure that the new element is not a parent of this element
		if (element.contains(this)) throw new IllegalArgumentException("Cannot add an element to its own child!");

		//Remove from current parent, if any
		if (!element.isRoot()) {
			element.getParent().removeChild(element);
		}

		//Add to this element
		children.add(index, element);
		element.setParent(this);
	}

	/**
	 * Add a new child to this xml element after the specified reference element. The reference element must be among
	 * the children of this xml element. Also, the child cannot be added, if it is already among this element's
	 * children, if it is the element itself, or if it is a parent of this element. The element will be removed from its
	 * current parent, if it has any.
	 *
	 * @param newChild       the new child element to add
	 * @param referenceChild the current child element to insert after
	 * @throws IllegalArgumentException if the new child element is already a child of this xml element, if the new
	 *                                  child element is the element itself, or if the new child element is a parent of
	 *                                  this xml element
	 */
	public void addChildAfter(XMLElement newChild, XMLElement referenceChild) {
		int referenceIndex = children.indexOf(referenceChild);
		if (referenceIndex != -1) addChild(newChild, referenceIndex + 1);
	}

	/**
	 * Add a new child to this xml element before the specified reference element. The reference element must be among
	 * the children of this xml element. Also, the child cannot be added, if it is already among this element's
	 * children, if it is the element itself, or if it is a parent of this element. The element will be removed from its
	 * current parent, if it has any.
	 *
	 * @param newChild       the new child element to add
	 * @param referenceChild the current child element to insert before
	 * @throws IllegalArgumentException if the new child element is already a child of this xml element, if the new
	 *                                  child element is the element itself, or if the new child element is a parent of
	 *                                  this xml element
	 */
	public void addChildBefore(XMLElement newChild, XMLElement referenceChild) {
		int referenceIndex = children.indexOf(referenceChild);
		if (referenceIndex != -1) addChild(newChild, referenceIndex);
	}

	/**
	 * Add the specified elements as children to this xml element. The new elements will be added to the end of this
	 * element's list of children. The new elements must not already be among the children of this xml element, be the
	 * element itself, and they must not be a parent of this element. The elements will be removed from their current
	 * parents, if they have any.
	 *
	 * @param elements the elements to add
	 * @throws IllegalArgumentException if one of the new child elements is already a child of this xml element, if one
	 *                                  of the new child elements is the element itself, or if one of the new child
	 *                                  elements is a parent of this xml element
	 */
	public void addChildren(Collection<XMLElement> elements) {
		List<XMLElement> failed = new ArrayList<>();

		//Insert as many elements as possible
		for (XMLElement element : elements) {
			try {
				addChild(element);
			} catch (IllegalArgumentException e) {
				failed.add(element);
			}
		}

		//Notify the user if some elements were not inserted
		if (failed.size() != 0) {
			StringBuilder msg = new StringBuilder("Error inserting the following elements:");
			for (XMLElement e : failed) {
				msg.append("\t").append(e);
			}
			throw new IllegalArgumentException(msg.toString());
		}
	}

	/**
	 * Remove the specified child element from this xml element. If this element does not contain the specified child
	 * element, nothing will happen. Otherwise the child element will be removed and become a root element.
	 *
	 * @param element the child element to remove
	 */
	public void removeChild(XMLElement element) {
		if (children.remove(element)) {
			element.setParent(null);
		}
	}

	/**
	 * Remove all child elements from this xml element.
	 */
	public void clear() {
		for (XMLElement child : children) {
			child.setParent(null);
		}

		children.clear();
	}

	/**
	 * Get the list of the children of this xml element.
	 *
	 * @return the list of children
	 */
	public List<XMLElement> getChildren() {
		return new ArrayList<>(children);
	}

	/**
	 * Get a list of child elements with the specified tag. This will only consider the immediate children of this xml
	 * element.
	 *
	 * @param tagName the name of the tag to search for
	 * @return the list of children with the specified tag
	 */
	public List<XMLElement> getChildrenByTag(String tagName) {
		return children.stream().filter(child -> child.getTagName().equals(tagName)).collect(Collectors.toList());
	}

	/**
	 * Get a list of child elements with the specified tag. This will also consider children of children and so on.
	 *
	 * @param tagName the name of the tag to search for
	 * @return the list of children with the specified tag
	 */
	public List<XMLElement> getChildrenByTagDeep(String tagName) {
		List<XMLElement> result = new ArrayList<>();

		for (XMLElement child : children) {
			//Consider each immediate child in turn
			if (child.getTagName().equals(tagName)) {
				result.add(child);
			}

			//Use recursion to consider children of children
			result.addAll(child.getChildrenByTagDeep(tagName));
		}

		return result;
	}

	/**
	 * Test whether this xml element is a parent of the specified other element.
	 *
	 * @param element the element to test against
	 * @return true if this xml element is a parent of the other element, false otherwise
	 */
	public boolean contains(XMLElement element) {
		//Attempt to early out
		if (element == this) return false;

		for (XMLElement child : children) {
			//Consider each immediate child in turn
			if (child == element) {
				return true;
			}

			//Use recursion to consider children of children, but only return true if we found a match. Otherwise we
			//need to keep looking
			boolean deeper = child.contains(element);
			if (deeper) return true;
		}

		return false;
	}

	/**
	 * Get the child of this xml element with the specified id. Element id should be unique. This will perform a deep
	 * search, thus considering children of children.
	 *
	 * @param id the id to look for
	 * @return the child element with the specified id, or null if no such child was found
	 */
	public XMLElement getChildByID(String id) {
		for (XMLElement child : children) {
			//Consider each immediate child in turn
			if (id.equals(child.getAttribute(ID_ATTRIBUTE))) {
				return child;
			}

			//Use recursion to consider children of children, but only return true if we found a match. Otherwise we
			//need to keep looking
			XMLElement deeper = child.getChildByID(id);
			if (deeper != null) return deeper;
		}

		return null;
	}

	/**
	 * Get the name of the tag of this xml element.
	 *
	 * @return the name of the tag of this xml element
	 */
	public String getTagName() {
		return tagName;
	}

	/**
	 * Get the text content of this xml element.
	 *
	 * @return the text content of this xml element
	 */
	public String getTextContent() {
		return textContent;
	}

	/**
	 * Set the text content of this xml element.
	 *
	 * @param textContent the new text content
	 */
	public void setTextContent(String textContent) {
		this.textContent = (textContent == null ? "" : textContent);
	}

	/**
	 * Test if this xml element has text content.
	 *
	 * @return true if this element has text content, false otherwise
	 */
	public boolean hasTextContent() {
		return !getTextContent().isEmpty();
	}

	/**
	 * Get the value of the attribute with the specified name.
	 *
	 * @param name the name of the attribute
	 * @return the value of the attribute with the specified name, or null if this element has no such attribute
	 */
	public String getAttribute(String name) {
		return attributes.get(name);
	}

	/**
	 * Helper method for getting the value of the attribute with the name specified by {@link #ID_ATTRIBUTE}. If this
	 * element has no id, this will return null.
	 *
	 * @return the id of this element, or null if it has no id
	 */
	public String getID() {
		return getAttribute(ID_ATTRIBUTE);
	}

	/**
	 * Set the id of this element. This will overwrite the current id. If this element has no id, a new attribute will
	 * be created. This method will not make any effort to ensure uniqueness of ids.
	 *
	 * @param id the new id of this element
	 */
	public void setID(String id) {
		setAttribute(ID_ATTRIBUTE, id);
	}

	/**
	 * Add a new class to this element. If this element already has the class, nothing will happen.
	 *
	 * @param className the new class to add
	 */
	public void addClass(String className) {
		String[] current = getClasses();
		StringBuilder newClassString = new StringBuilder();

		//Go through existing classes, ensuring that we are adding a new class. For performance reasons, we construct
		//the new potential class string at the same time rather than looping again
		for (String c : current) {
			//If this element already has the class we return
			if (c.equals(className)) return;

			newClassString.append(c).append(CLASS_SEPARATOR);
		}

		//If we made it here, the class is new, and the string builder is ready for appending the new class
		newClassString.append(className);
		setAttribute(CLASS_ATTRIBUTE, newClassString.toString());
	}

	/**
	 * Remove a class from this element. If the element does not have the class, nothing will happen.
	 *
	 * @param className the class to remove
	 */
	public void removeClass(String className) {
		String[] current = getClasses();
		StringBuilder newClassString = new StringBuilder();

		//Go through existing classes, adding only those to the new class string that do not match the class to remove
		for (String c : current) {
			if (!c.equals(className)) {
				newClassString.append(c).append(CLASS_SEPARATOR);
			}
		}

		//Remove last space if the new class string contains anything
		if (newClassString.length() != 0) newClassString.deleteCharAt(newClassString.length() - 1);

		//Update class string, which may or may not have changed
		setAttribute(CLASS_ATTRIBUTE, newClassString.toString());
	}

	/**
	 * Helper method for getting an array of the classes of this element. This array will be empty, if there are no
	 * classes.
	 *
	 * @return the classes of this element. This array will be empty, if there are no classes
	 */
	public String[] getClasses() {
		String classString = getAttribute(CLASS_ATTRIBUTE);
		if (classString == null) return new String[0];

		//Split classes by spaces
		return classString.split(CLASS_SEPARATOR);
	}

	/**
	 * Set the value of the specified attribute. If the value is null or empty, the attribute will either not be created
	 * or removed if it already exists. If the name is null, this method does nothing.
	 *
	 * @param name  the name of the attribute
	 * @param value the value to set
	 */
	public void setAttribute(String name, String value) {
		if (name == null) return;
		if (value == null || value.isEmpty()) {
			attributes.remove(name);
		} else {
			attributes.put(name, value);
		}
	}

	/**
	 * Test whether this element is a root element. A root element is one without a parent.
	 *
	 * @return {@code true} if this element is the root, {@code false} otherwise
	 */
	public boolean isRoot() {
		return getParent() == null;
	}

	/**
	 * Get the parent of this element. If this element is a root element, this method will return null.
	 *
	 * @return the parent of this element, or null if this element is a root element
	 */
	public XMLElement getParent() {
		return parent;
	}

	/**
	 * Set the parent of this element.
	 *
	 * @param parent the new parent element
	 */
	private void setParent(XMLElement parent) {
		this.parent = parent;
	}

	/**
	 * Test whether this element is a leaf element. A leaf element is one without children.
	 *
	 * @return {@code true} if this is a leaf element, {@code false} otherwise
	 */
	public boolean isLeaf() {
		return children.isEmpty();
	}

	/**
	 * Get a textual representation of the xml in this element and all of its children.
	 *
	 * @return a textual representation of the xml in this element
	 */
	@Override
	public String toString() {
		return toString("");
	}

	/**
	 * Get a textual representation of this node with the specified amount of indentation.
	 *
	 * @param indentation the amount of indentation
	 * @return the textual representation of this node
	 */
	private String toString(String indentation) {
		StringBuilder builder = new StringBuilder();

		builder.append(indentation);
		builder.append("<").append(this.getTagName());

		if (!this.attributes.isEmpty()) {
			for (Map.Entry<String, String> entry : attributes.entrySet()) {
				builder.append(" ").append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
			}
		}

		if (!this.hasTextContent() && this.children.isEmpty()) {
			builder.append("/>\n");
		} else {
			builder.append(">");

			if (this.hasTextContent()) {
				builder.append(getTextContent());
			} else {
				builder.append('\n');
				for (XMLElement child : children) {
					builder.append(child.toString(indentation + '\t'));
				}

				builder.append(indentation);
			}

			builder.append("</").append(this.getTagName()).append(">");
			builder.append('\n');
		}

		return builder.toString();
	}

	/**
	 * Get a deep clone of this XMLElement. The clone will be a new instance of the same class, and the clone will also
	 * be independent of the element from which it is cloned. The cloned element will by convention be a root element,
	 * however, and is thus not strictly equal to the element from which it is cloned. This does not violate the
	 * contract of the {@link Object#clone()} method.
	 *
	 * @return a clone of this XMLElement
	 */
	@Override
	public XMLElement clone() {
		try {
			//By convention, we should retrieve the cloned object by calling super.clone()
			XMLElement e = (XMLElement) super.clone();

			//We then perform a deep clone
			//The tag name is immutable
			//We make a new attributes map, but the content (Strings) is immutable
			e.attributes = new HashMap<>(attributes);
			//Perform a deep copy of the children using recursion
			e.children = new ArrayList<>();
			for (XMLElement child : children) {
				e.addChild(child.clone()); //The child will no longer be a root when it is added
			}
			//To avoid potential problems, we define that the clone is a root element
			e.parent = null;
			//The text content is also immutable

			return e;
		} catch (CloneNotSupportedException e1) {
			//This should never happen, because XMLElement implements Cloneable
		}

		//Return null, although this line should never be executed
		return null;
	}
}
