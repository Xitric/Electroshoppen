package pim.business;

import erp.business.SupplierIntegrator;
import pim.persistence.PersistenceFacade;
import pim.persistence.PersistenceFactory;

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
	 * Facade for the persistence layer.
	 */
	private final PersistenceFacade persistence;

	/* Entity managers */
	private final ProductManager productManager;
	private final AttributeManager attributeManager;
	private final CategoryManager categoryManager;
	private final TagManager tagManager;

	/**
	 * Constructs a new PIM implementation.
	 */
	public PIMImpl() {
		persistence = PersistenceFactory.createDatabaseMediator();
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

		//Keep track of products to save
		Set<Product> productsToSave = new HashSet<>();

		for (SupplierIntegrator.ProductData data : productData) {
			if (existingProductsMap.containsKey(data.getID())) {
				//The product exists, update it
				Product p = existingProductsMap.get(data.getID());
				p.setName(data.getName());
				p.setPrice(data.getPrice());

				//Schedule for saving
				productsToSave.add(p);
			} else {
				//The product was new
				Product p = productManager.createProduct(data.getID(), data.getName(), "", data.getPrice());

				//Schedule for saving
				productsToSave.add(p);
			}
		}

		//Save products in database
		try {
			productManager.saveProducts(productsToSave);
		} catch (IOException e) {
			e.printStackTrace();
			return false; //Synchronization failed, might only be partial
		}

		//Close connection
		si.close();

		//We made it all the way through, so synchronization was successful
		return true;
	}

	@Override
	public Product getProductInformation(int id) throws IOException {
		return productManager.getProduct(id);
	}

	@Override
	public BufferedImage getImage(String url) throws IOException {
		return productManager.createImage(url).getImage();
	}

	@Override
	public void addImage(String url) {

	}

	@Override
	public void removeImage(String url) throws IOException {
		productManager.removeImage(url);
	}

	@Override
	public List<Product> getProducts(String categoryName) throws IOException {
		return new ArrayList<>(productManager.getProductsByCategory(categoryName));
	}

	@Override
	public List<Product> getProducts() throws IOException {
		return new ArrayList<>(productManager.getProducts());
	}

	@Override
	public void removeAttribute(int attributeID) throws IOException {
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
	public void saveAttribute(Attribute attribute) throws IOException {
		attributeManager.saveAttribute(attribute);
	}

	@Override
	public List<Attribute> getAttributes() throws IOException {
		return new ArrayList<>(attributeManager.getAttributes());
	}

	@Override
	public Attribute getAttribute(int attributeID) throws IOException {
		return attributeManager.getAttribute(attributeID);
	}

	@Override
	public List<Category> getCategoriesWithAttribute(int attributeID) throws IOException {
		return null;
	}

	@Override
	public List<Category> getCategories() throws IOException {
		return new ArrayList<>(categoryManager.getCategories());
	}

	@Override
	public Category getCategory(String categoryName) {
		return null;
	}

	@Override
	public List<Attribute> getAttributesFromCategory(String categoryName) throws IOException {
		return new ArrayList<>(persistence.getCategoryByName(categoryName).getAttributes());
	}

	@Override
	public List<Attribute> getAttributesNotInTheCategory(String categoryName) throws IOException {
		Set<Attribute> attributes = persistence.getAttributes();
		Set<Attribute> aOnCategory = persistence.getCategoryByName(categoryName).getAttributes();
		attributes.removeAll(aOnCategory);
		return new ArrayList<>(attributes);
	}

	@Override
	public void deleteAttributeFromCategory(String categoryName) throws IOException {
		//	    try{
		//
		//
		//        }catch (IOException e){
		//            e.printStackTrace();
		//        }
	}

	@Override
	public void removeCategory(String categoryName) {
		try {

			if (categoryManager.getCategory(categoryName) != null) {
				categoryManager.deleteCategory(categoryName);
			}
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	@Override
	public void addCategory(String categoryName) {
		try{
			if(!categoryManager.getCategories().contains(categoryName)){
				Category c = categoryManager.createCategory(categoryName);
				persistence.saveCategory(c);

			}
		} catch (IOException e){
			e.printStackTrace();
		}
	}



}
