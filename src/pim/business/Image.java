package pim.business;

import java.awt.image.BufferedImage;

/**
 * A representation of an image in the PIM.
 *
 * @author Kasper
 */
public class Image {

	private BufferedImage img;


	public Image(BufferedImage img) {
		this.img = img;
	}

	/**
	 * Get the url of this image.
	 *
	 * @return the url of this image
	 */

	/**
	 * Get a buffered image representation of this image. This method uses lazy initialization.
	 *
	 * @return a buffered image representation of this image
	 * @throws IllegalStateException if the image could not be read
	 */
	public BufferedImage getImage() {
		if (img == null) {
			// TODO
			//throw new IllegalStateException("Error reading image file at " + url + ". Ensure that the image still exists.", e);
		}

		return img;
	}
}
