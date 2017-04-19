package pim.business;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * A representation of an image in the PIM.
 *
 * @author Kasper
 */
public class Image {

	private final String url;
	private BufferedImage img;

	/**
	 * Constructs a new image from the specified url.
	 *
	 * @param url the url of the image
	 * @throws IllegalArgumentException if the url points to an invalid image file
	 */
	public Image(String url) {
		//Ensure that the url points to a valid image file (the file must exist and have a supported extension)
		if (!(new File(url).isFile() && Arrays.asList(ImageIO.getReaderFileSuffixes()).contains(url.substring(url.lastIndexOf('.') + 1)))) {
			throw new IllegalArgumentException("The url must refer to a valid image file with one of these types: " + Arrays.toString(ImageIO.getReaderFileSuffixes()));
		}

		this.url = url;
	}

	/**
	 * Get the url of this image.
	 *
	 * @return the url of this image
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Get a buffered image representation of this image. This method uses lazy initialization.
	 *
	 * @return a buffered image representation of this image
	 * @throws IllegalStateException if the image could not be read
	 */
	public BufferedImage getImage() {
		if (img == null) {
			try {
				img = ImageIO.read(new File(url));
			} catch (IOException e) {
				throw new IllegalStateException("Error reading image file at " + url + ". Ensure that the image still exists.", e);
			}
		}

		return img;
	}
}
