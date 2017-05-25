package pim.business;

import shared.Image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

/**
 * Manages loading products from the persistence layer and storing them in memory for faster retrieval. This manager
 * ensures that only one instance of any product is made.
 *
 * @author Kasper
 */
class ProductManager implements ProductChangeListener {

	private final Map<Integer, Product> products;
	private final PIMPersistenceFacade persistence;
	private HashMap<Integer, Image> images;

	/**
	 * Constructs a new product manager.
	 *
	 * @param persistence the persistence facade
	 */
	public ProductManager(PIMPersistenceFacade persistence) {
		products = new HashMap<>();
		images = new HashMap<>();
		this.persistence = persistence;
	}

	/**
	 * Constructs a product if one with the given id does not exist already. Otherwise a reference to the existing
	 * product will be returned.
	 *
	 * @param id          the id of the product
	 * @param name        the name of the product
	 * @param description the description of the product
	 * @param price       the price of the product
	 * @return the created product
	 */
	public Product constructProduct(int id, String name, String description, double price) {
		Product p;

		if (products.get(id) == null) {
			p = new Product(id, name, description, price);
			p.addChangeListener(this);
			products.put(id, p);
		} else {
			p = products.get(id);
			p.setName(name);
			p.setDescription(description);
			p.setPrice(price);
		}

		return p;
	}

	public Set<Product> getPopularProducts(int amount) throws IOException {
		Set<ProductReview> reviews = persistence.getProductReviews();
		HashMap<Integer, ArrayList<Double>> reviewMap = new HashMap<>();
		Set<Integer> popularProductIds = new HashSet<>();
		Set<Product> popularProducts = new HashSet<>();

		for (ProductReview next : reviews) {
			int productId = next.getProductid();
			Date date = next.getTime();
			int rating = next.getRating();

			Calendar c = Calendar.getInstance();
			c.add(Calendar.MONTH, -2);
			Date currentDate = c.getTime();

			if (!reviewMap.containsKey(productId)) {
				reviewMap.put(productId, new ArrayList<Double>());
			}

			if (date.compareTo(currentDate) > 0) {
				reviewMap.get(productId).add((double) next.getRating());
			} else {
				reviewMap.get(productId).add(next.getRating() * 0.5);
			}

		}

		for (int i = 0; i < amount; i++) {
			double biggest = 0;
			int productId = 0;
			for (Map.Entry<Integer, ArrayList<Double>> entry : reviewMap.entrySet()) {
				double ratingSum = 0;
				for (int j = 0; j < entry.getValue().size(); j++) {
					ratingSum += entry.getValue().get(j);
				}
				ratingSum = ratingSum / entry.getValue().size();
				if (ratingSum > biggest) {
					productId = entry.getKey();
					biggest = ratingSum;
				}
			}

			reviewMap.remove(productId);
			popularProductIds.add(productId);

		}

		for (Integer popularProductId : popularProductIds) {
			popularProducts.add(persistence.getProductByID(popularProductId));
		}
		return popularProducts;
	}

	/**
	 * Get a set of all products.
	 *
	 * @return a set of all products
	 * @throws IOException if something goes wrong
	 */
	public Set<Product> getProducts() throws IOException {
		return persistence.getProducts();
	}

	/**
	 * Get the product with the specified id.
	 *
	 * @param productID the id of the product
	 * @return the product with the specified id, or null if no such product could be retrieved
	 * @throws IOException if something goes wrong
	 */

	public Product getProduct(int productID) throws IOException {
		return persistence.getProductByID(productID);
	}

	/**
	 * Get the products in the specified category.
	 *
	 * @param categoryName the name of the category
	 * @return the products in the specified category
	 * @throws IOException if something goes wrong
	 */
	public Set<Product> getProductsByCategory(String categoryName) throws IOException {
		return persistence.getProductsByCategory(categoryName);
	}

	/**
	 * Remove the specified category from the currently loaded products.
	 *
	 * @param category the category to remove
	 */
	public void removeCategoryFromProducts(Category category) {
		for (Product p : products.values()) {
			p.removeCategory(category);
		}
	}

	/**
	 * Save the information about the specified product in the database.
	 *
	 * @param product the product to save
	 * @throws IOException if something goes wrong
	 */
	public void saveProduct(Product product) throws IOException {
		persistence.saveProduct(product);
		products.put(product.getID(), product);
	}

	/**
	 * Save the information about all the specified products in the database.
	 *
	 * @param productCollection the products to save
	 * @throws IOException if something goes wrong
	 */
	public void saveProducts(Collection<Product> productCollection) throws IOException {
		persistence.saveProducts(productCollection);

		for (Product product : productCollection) {
			products.put(product.getID(), product);
		}
	}

	/**
	 * Constructs an image or returns the existing one with the same id if it already exists.
	 *
	 * @param id  the id of the image
	 * @param img the image data
	 * @return the created image object
	 */
	public Image constructImage(int id, BufferedImage img) {
		return images.computeIfAbsent(id, (i) -> new Image(i, img));
	}

	/**
	 * Create a new image from the specified url in the PIM. This image will automatically be saved.
	 *
	 * @param url the location of the image
	 * @return the new image
	 * @throws IOException if something goes wrong
	 */
	public Image createImage(String url) throws IOException {
		Image img = new Image(url);
		persistence.saveImage(img);
		images.put(img.getID(), img); //Image should have a valid id after it has been saved
		return img;
	}

	/**
	 * Called when an image is removed from a product. This will test if no more references to the image exist, and in
	 * this case remove the image from memory.
	 *
	 * @param image the image that was removed
	 */
	@Override
	public void imageRemoved(Image image) {
		for (Product p : products.values()) {
			if (p.getImages().contains(image)) {
				return;
			}
		}

		//No products contained the image, so free from memory (automatically removed from db)
		images.remove(image.getID());
	}
}
