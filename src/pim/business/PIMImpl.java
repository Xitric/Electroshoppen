package pim.business;

import erp.business.SupplierIntegrator;
import pim.persistence.DatabaseMediator;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;

/**
 * Implementation of the PIM interface.
 *
 * @author Kasper
 */
public class PIMImpl implements PIM {

	@Override
	public boolean synchronize() {
		//Get set of product data from supplier integrator. If connection fails, try again. After 5 failed attempts,
		//synchronization is stopped
		Set<SupplierIntegrator.ProductData> productData = null;

		for (int i = 0; i < 5; i++) {
			try {
				productData = SupplierIntegrator.getInstance().getAllProductData();
			} catch (TimeoutException e) {
				e.printStackTrace();
			}
		}

		if (productData == null) return false; //Synchronization failed

		//Connection was established and the product data has been retrieved. For each piece of data, either update the
		//existing product or create a new product
		Set<Product> existingProducts;
		try {
			//We get a set of all products in the PIM. This costs more memory, but it also speeds up the process
			//substantially
			existingProducts = DatabaseMediator.getInstance().getProducts();
		} catch (IOException e) {
			e.printStackTrace();
			return false; //Synchronization failed
		}

		//We further add the data to a map, to speed up the lookup process further
		Map<String, Product> existingProductsMap = new HashMap<>();
		for (Product p: existingProducts) {
			existingProductsMap.put(p.getID(), p);
		}

		for (SupplierIntegrator.ProductData data: productData) {
			if (existingProductsMap.containsKey(data.getName())) {
				//The product exists, update it
				Product p = existingProductsMap.get(data.getID());
				p.setName(data.getName());
				p.setPrice(data.getPrice());

				//Save changes to database
				DatabaseMediator.getInstance().saveProduct(p);
			} else {
				//The product was new
				Product p = new Product(data.getID(), data.getName(), data.getPrice());

				//Save new product in database
				DatabaseMediator.getInstance().saveProduct(p);
			}
		}

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
	public void removeAttribute(String attributeName) {

	}

	@Override
	public Attribute getAttribute(String attributeName) {
		return null;
	}

	@Override
	public List<Category> getCategoriesWithAttribute(String attributeName) {
		return null;
	}

	public static void main(String[] args) {
		PIM pim = new PIMImpl();
		long start = System.currentTimeMillis();
		pim.synchronize();
		long end = System.currentTimeMillis();

		System.out.println("Synchronized in " + (end-start) + "ms (it takes almost 1 second for me!)");
	}
}
