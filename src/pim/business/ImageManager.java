package pim.business;

import pim.persistence.PersistenceMediator;

import java.util.HashMap;

/**
 * Manages image creation and ensures no duplicates (same url) are made. This is mainly to reduce the memory footprint
 * of the application.
 *
 * @author Kasper
 */
public class ImageManager {

	private HashMap<String, Image> images;
	private final PersistenceMediator persistence;

	/**
	 * Constructs a new image manager.
	 *
	 * @param persistence the persistence mediator
	 */
	public ImageManager(PersistenceMediator persistence) {
		images = new HashMap<>();
		this.persistence = persistence;
	}

	/**
	 * Creates an image or returns the existing one with the same url if it already exists.
	 *
	 * @param url the url of the image
	 * @return the created image object
	 */
	public Image createImage(String url) {
		return images.computeIfAbsent(url, Image::new);
	}

	/**
	 * Removes an image from the list of images.
	 *
	 * @param url the url of the image to remove
	 */
	public void removeImage(String url) {
		images.remove(url);
	}
}
