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

	/* Entity managers */
	private final ProductManager productManager;
	private final AttributeManager attributeManager;
	private final CategoryManager categoryManager;
	private final TagManager tagManager;

	/**
	 * Constructs a new PIM implementation.
	 */
	public PIMImpl() {
		persistence = DatabaseFacade.createDatabaseMediator();
		productManager = new ProductManager(persistence);
		attributeManager = new AttributeManager(persistence);
		categoryManager = new CategoryManager(persistence);
		tagManager = new TagManager(persistence);
		persistence.setCache(new DataCacheImpl(productManager, attributeManager, categoryManager, tagManager));
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
			existingProducts = productManager.getProducts();
		} catch (IOException e) {
			e.printStackTrace();
			return false; //Synchronization failed
		}

		//We further add the data to a map, to speed up the lookup process further
		Map<Integer, Product> existingProductsMap = new HashMap<>();
		for (Product p : existingProducts) {
			existingProductsMap.put(p.getID(), p);
		}

		for (SupplierIntegrator.ProductData data : productData) {
			if (existingProductsMap.containsKey(data.getID())) {
				//The product exists, update it
				Product p = existingProductsMap.get(data.getID());
				p.setName(data.getName());
				p.setPrice(data.getPrice());

				//Save changes to database
				productManager.saveProduct(p);
			} else {
				//The product was new
				Product p = productManager.createProduct(data.getID(), data.getName(), data.getPrice());

				//Save new product in database
				productManager.saveProduct(p);
			}
		}

		//Close connection
		si.close();

		//We made it all the way through, so synchronization was successful
		return true;
	}

	@Override
	public Product getProductInformation(int id) {
		try {
			return productManager.getProduct(id);
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Something went wrong, return null
		return null;
	}

	@Override
	public BufferedImage getMediaInformation(String url) {
		return productManager.createImage(url).getImage();
	}

	@Override
	public List<Product> getProducts(String categoryName) {
		try {
			return new ArrayList<>(productManager.getProductsByCategory(categoryName));
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Something went wrong, return null
		return null;
	}

	@Override
	public int addAttribute(String name, Object defaultValue, Set<Object> legalValues) {
		try {
			return attributeManager.registerAttribute(name, defaultValue, legalValues);
		} catch (IOException e) {
			e.printStackTrace();
		}

		//TODO: Send exception all the way to the GUI to notify the user
		//Something went wrong, return -1
		return -1;
	}

	@Override
	public void removeAttribute(int attributeID) {
		Attribute attribute = attributeManager.getLoadedAttribute(attributeID);

		//Remove attribute from all categories (and thus also products) in memory. If the attribute is null, then no
		//categories or products in memory are referring to it, and this step can be safely skipped
		if (attribute != null) {
			Set<Category> categories = categoryManager.getLoadedCategoriesWithAttribute(attribute);
			for (Category category : categories) {
				category.removeAttribute(attribute);
			}
		}

		//Delete attribute in database. This automatically resolves broken references to it
		attributeManager.deleteAttribute(attributeID);
	}

	@Override
	public List<Attribute> getAttributes() {
		try {
			return new ArrayList<>(attributeManager.getAttributes());
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Something went wrong, return null
		return null;
	}

	@Override
	public Attribute getAttribute(int attributeID) {
		try {
			return attributeManager.getAttribute(attributeID);
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Something went wrong, return null
		return null;
	}

	@Override
	public List<Category> getCategoriesWithAttribute(int attributeID) {
		return null;
	}

	@Override
	public List<Category> getCategories() {
		try {
			return new ArrayList<>(categoryManager.getCategories());
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Something went wrong, return null
		return null;
	}

	@Override
	public List<Attribute> getAttributesFromCategory(String categoryName) {
		try {
			return new ArrayList<>(persistence.getCategoryByName(categoryName).getAttributes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Attribute> getAttributesNotInTheCategory(String categoryName) {
		try {
			Set<Attribute> attributes = persistence.getAttributes();
			Set<Attribute> aOnCategory = persistence.getCategoryByName(categoryName).getAttributes();
			attributes.removeAll(aOnCategory);
			return new ArrayList<>(attributes);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void deleteAttributeFromCategory(String categoryName) {
		//	    try{
		//
		//
		//        }catch (IOException e){
		//            e.printStackTrace();
		//        }
	}

}
