package pim.business;

import erp.business.SupplierIntegrator;
import pim.persistence.DatabaseFacade;
import pim.persistence.PersistenceMediator;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;

/**
 * Implementation of the PIM interface.
 *
 * @author Kasper
 */
public class PIMImpl implements PIM {

	/**
	 * Mediator for persistence layer.
	 */
	private final PersistenceMediator persistence;

	/* Entiry managers */
	private final CategoryManager categoryManager;
	private final AttributeManager attributeManager;
	private final TagManager tagManager;
	private final ImageManager imageManager;

	/**
	 * Constructs a new PIM implementation.
	 */
	public PIMImpl() {
		categoryManager = new CategoryManager();
		attributeManager = new AttributeManager();
		tagManager = new TagManager();
		imageManager = new ImageManager();
		persistence = DatabaseFacade.createDatabaseMediator(categoryManager, attributeManager, tagManager, imageManager);
	}

	@Override
	public boolean synchronize() {
		//Get set of product data from supplier integrator. If connection fails, try again. After 5 failed attempts,
		//synchronization is stopped
		SupplierIntegrator si = null;

		for (int i = 0; i < 5; i++) {
			try {
				si = SupplierIntegrator.getInstance();
				break; //Only breaks if an exception does not occur
			} catch (TimeoutException e) {
				e.printStackTrace();
			}
		}

		if (si == null) return false; //Synchronization failed

		Set<SupplierIntegrator.ProductData> productData = si.getAllProductData();

		//Connection was established and the product data has been retrieved. For each piece of data, either update the
		//existing product or create a new product
		Set<Product> existingProducts;
		try {
			//We get a set of all products in the PIM. This costs more memory, but it also speeds up the process
			//substantially
			existingProducts = persistence.getProducts();
		} catch (IOException e) {
			e.printStackTrace();
			return false; //Synchronization failed
		}

		//We further add the data to a map, to speed up the lookup process further
		Map<String, Product> existingProductsMap = new HashMap<>();
		for (Product p : existingProducts) {
			existingProductsMap.put(p.getID(), p);
		}

		for (SupplierIntegrator.ProductData data : productData) {
			if (existingProductsMap.containsKey(data.getName())) {
				//The product exists, update it
				Product p = existingProductsMap.get(data.getID());
				p.setName(data.getName());
				p.setPrice(data.getPrice());

				//Save changes to database
				persistence.saveProduct(p);
			} else {
				//The product was new
				Product p = new Product(data.getID(), data.getName(), data.getPrice());

				//Save new product in database
				persistence.saveProduct(p);
			}
		}

		//Close connection
		si.close();

		//We made it all the way through, so synchronization was successful
		return true;
	}

	@Override
	public Product getProductInformation(int id) {
		return null;
	}

	@Override
	public BufferedImage getMediaInformation(int id) {
		return null;
	}

	@Override
	public List<Product> getProducts(String categoryName) {
		return null;
	}

	@Override
	public void removeAttribute(String attributeID) {
		persistence.deleteAttribute(attributeID);
	}

	@Override
	public List<Attribute> getAttributes() {
		try {
			return new ArrayList<>(persistence.getAttributes());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new ArrayList<Attribute>();
	}

	@Override
	public Attribute getAttribute(String attributeName) {
		return null;
	}

	@Override
	public List<Category> getCategoriesWithAttribute(String attributeName) {
		return null;
	}
}
