package cms.business;

import org.w3c.dom.Element;

/**
 * An object that can be used to refer to a specific location in the content of a {@link DynamicPage}. This object will
 * keep track of both the id of the selected element, the range of selected text (if any) in that element, and whether
 * this marker points to before or after itself.
 *
 * @author Kasper
 */
public class DocumentMarker {

	/**
	 * The id of the selected element.
	 */
	private final String id;

	/**
	 * The highlighted text in the selected element, or null if no text is selected.
	 */
	private final String range;

	/**
	 * The index of the start of the text highlight or -1 if no text is highlighted.
	 */
	private final int startIndex;

	/**
	 * True if this marker points to before itself, false if it points to after itself. Used for relative positioning.
	 */
	private final boolean before;

	/**
	 * Constructs a new document marker. If the specified range is not contained in the element, it will be disregarded.
	 *
	 * @param element the selected element
	 * @param range   the range of text selected in the element, if any.
	 * @param before  true if this marker points to before itself, false if it points to after itself
	 */
	public DocumentMarker(Element element, String range, boolean before) {
		this.id = element.getAttribute(DynamicPage.ID_ATTRIB);
		this.before = before;

		if (!range.isEmpty() && element.getTextContent().contains(range)) {
			this.range = range;
			startIndex = element.getTextContent().indexOf(range);
		} else {
			this.range = null;
			startIndex = -1;
		}
	}

	/**
	 * Get the id of the element selected by this document marker.
	 *
	 * @return the id of the element selected by this document marker
	 */
	public String getSelectedElementID() {
		return id;
	}

	/**
	 * Test if this document marker describes a selection of a part of the selected element. If false, the marker
	 * describes a selection of the entire element.
	 *
	 * @return true if this is a part selection, false if it is a selection of the entire element
	 */
	public boolean hasRangeSelection() {
		return range != null;
	}

	/**
	 * Get the range selection from this document marker. If this marker describes a selection of the entire element,
	 * this method will return null.
	 *
	 * @return the range selection, or null if this is a selection of the entire element
	 */
	public String getRangeSelection() {
		return range;
	}

	/**
	 * Get the index of the beginning of the part selection. This will return the index of the first character in the
	 * part selection.
	 *
	 * @return the index of the beginning of the part selection, or -1 if this is a selection of the entire element
	 */
	public int getStartSelection() {
		return startIndex;
	}

	/**
	 * Get the index of the end of the part selection. This will return the index of the first character NOT in the part
	 * selection.
	 *
	 * @return the index of the end of the part selection, or -1 if this is a selection of the entire element
	 */
	public int getEndSelection() {
		if (!hasRangeSelection()) return -1;
		return getStartSelection() + range.length();
	}

	/**
	 * Check whether this marker points to before itself. If false, this marker points to after itself. This value can
	 * be used for relative positioning of elements.
	 *
	 * @return true if this marker points to before itself, false if it points to after itself
	 */
	public boolean pointsToBefore() {
		return before;
	}
}
