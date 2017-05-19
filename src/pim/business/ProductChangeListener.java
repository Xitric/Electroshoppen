package pim.business;

import shared.Image;

/**
 * Interface describing an object that can listen to changes on a product in terms of removing images.
 *
 * @author Kasper
 */
public interface ProductChangeListener {

	/**
	 * Called when an image has been removed from a product.
	 *
	 * @param image the image that was removed
	 */
	void imageRemoved(Image image);
}
