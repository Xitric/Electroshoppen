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
 * @author Mikkel
 */
public class Image {

	private BufferedImage img;
	private String url;
	private int id;

	/**
	 * Constructs a new image from the specified url.
	 *
	 * @param url the url of the image
	 * @throws IllegalArgumentException if the url points to an invalid image file
	 */
	Image(String url) {
		//Ensure that the url points to a valid image file (the file must exist and have a supported extension)
		if (!(new File(url).isFile() && Arrays.asList(ImageIO.getReaderFileSuffixes()).contains(url.substring(url.lastIndexOf('.') + 1)))) {
			throw new IllegalArgumentException("The url must refer to a valid image file with one of these types: " + Arrays.toString(ImageIO.getReaderFileSuffixes()));
		}

		this.url = url;
	}

	/**
	 * Constructs a new wrapper of a buffered image with a missing id. The id should be generated when the image is
	 * saved for the first time.
	 *
	 * @param img the image to wrap
	 * @throws IllegalArgumentException if the image is null
	 */
	Image(BufferedImage img) {
		this(-1, img);
	}

	/**
	 * Constructs a new wrapper of a buffered image.
	 *
	 * @param id  the id of this image
	 * @param img the image to wrap
	 * @throws IllegalArgumentException if the image is null
	 */
	Image(int id, BufferedImage img) {
		this.id = id;

		if (img == null) {
			throw new IllegalArgumentException("Image cannot be null!");
		}
		this.img = img;
	}

	/**
	 * Test whether the id of this image is valid.
	 *
	 * @return true if the id is valid, false otherwise
	 */
	public boolean hasValidID() {
		return id >= 0;
	}

	/**
	 * Get the id of this image.
	 *
	 * @return the id of this image
	 */
	public int getID() {
		return id;
	}

	/**
	 * Set the id of this image. This operation will be ignored if the id is already set. The purpose of this method
	 * is to allow the persistence layer to assign an id to an image created in the domain layer.
	 *
	 * @param id the id of the image
	 */
	public void setID(int id) {
		if (this.id < 0) {
			this.id = id;
		}
	}

	/**
	 * Get a buffered image representation of this image. If this image was constructed using a url, this method will
	 * use lazy loading.
	 *
	 * @return a buffered image representation of this image
	 */
	public BufferedImage getImage() {
		if (img == null) {
			//If the image data is null, then the url must specify a valid image (ensured in the constructors)
			try {
				img = ImageIO.read(new File(url));
			} catch (IOException e) {
				throw new IllegalStateException("Error reading image file at " + url + ". Ensure that the image still exists.", e);
			}
		}

		return img;
	}
}
