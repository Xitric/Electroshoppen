package cms.business;

import java.awt.image.BufferedImage;

/**
 * @author Emil
 */
public class Guide implements DynamicPage {
	@Override
	public void insertHTML(DocumentMarker marker, String html) {

	}

	@Override
	public void removeHTML(DocumentMarker marker) {

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
}
