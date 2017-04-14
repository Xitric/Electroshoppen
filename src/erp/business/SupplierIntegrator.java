package erp.business;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeoutException;

/**
 * Dummy class to model the behavior of the supplier integrator system. It is assumed that this system is used to
 * integrate with all ERP systems in a unified way. The goal of this class is not to model the internal behavior of the
 * supplier integrator or the ERP systems, but rather to provide the overall behavior of the public interface.
 *
 * @author Kasper
 */
public class SupplierIntegrator {

	private static SupplierIntegrator instance = null;

	/**
	 * "Establish" a connection to the supplier integrator and get a singleton instance for retrieving information. This
	 * will fail and throw an exception in 1% of all cases to simulate the chance of not being able to establish a
	 * connection.
	 *
	 * @return the supplier integrator singleton instance
	 * @throws TimeoutException if a connection could not be "established"
	 */
	public static SupplierIntegrator getInstance() throws TimeoutException {
		//Connection error in 1% of all cases
		if (Math.random() > .99) throw new TimeoutException("Connection could not be established, try again later");

		//Otherwise, lazily initialize supplier integrator and return instance
		if (instance == null) {
			instance = new SupplierIntegrator();
		}

		return instance;
	}

	/**
	 * Read all product information from the ERP systems handled by this supplier integrator.
	 *
	 * @return a set of all product data
	 */
	public Set<ProductData> getAllProductData() {
		Set<ProductData> data = new HashSet<>();

		//Read ERP.txt file
		try (Scanner reader = new Scanner(SupplierIntegrator.class.getResourceAsStream("ERP.txt"))) {
			while (reader.hasNextLine()) {
				//Read each line. Skip it, if it begins with the comment symbol '#'
				String line = reader.nextLine();
				if (line.startsWith("#")) continue;

				//Read the information from the line using a new scanner with ':' as its delimiter
				Scanner lineReader = new Scanner(line);
				lineReader.useDelimiter(":");
				String id = lineReader.next();
				String name = lineReader.next();
				double price = lineReader.nextDouble();

				//Add product data
				data.add(new ProductData(id, name, price));
			}
		}

		return data;
	}

	/**
	 * "Close" the connection to the supplier integrator system. This method does nothing, but in a real life scenario
	 * it would.
	 */
	public void close() {

	}

	/**
	 * A class representing objects that can store data for products read from the ERP systems.
	 */
	public static class ProductData {

		private final String id;
		private final String name;
		private final double price;

		/**
		 * Constructs a new object for product data read from the ERP systems.
		 *
		 * @param id    the id of the product
		 * @param name  the name of the product
		 * @param price the price of the product
		 */
		private ProductData(String id, String name, double price) {
			this.id = id;
			this.name = name;
			this.price = price;
		}

		/**
		 * Get the id of the product represented by this data.
		 *
		 * @return the id of the product represented by this data
		 */
		public String getID() {
			return id;
		}

		/**
		 * Get the name of the product represented by this data.
		 *
		 * @return the name of the product represented by this data
		 */
		public String getName() {
			return name;
		}

		/**
		 * Get the price of the product represented by this data.
		 *
		 * @return the price of the product represented by this data
		 */
		public double getPrice() {
			return price;
		}
	}
}
