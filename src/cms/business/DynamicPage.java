package cms.business;

import java.awt.image.BufferedImage;

/**
 * Interface describing a
 *
 * @author Kasper
 */
public interface DynamicPage {

	void insertHTML(DocumentMarker marker, String html);

	void removeHTML(DocumentMarker marker);

	void insertImage(DocumentMarker marker, BufferedImage image);

	void removeImage(DocumentMarker marker);

	String getTextSelection(DocumentMarker marker);

	Link getLinkSelection(DocumentMarker marker);

	void setTextLink(DocumentMarker marker, Link link);

	void removeLink(DocumentMarker marker);
}
