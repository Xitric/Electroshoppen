package pim.persistence;

import pim.business.Attribute;
import pim.business.Category;
import pim.business.Product;

import java.sql.*;
import java.util.List;

/**
 * Mediator used to access the underlying database. The mediator uses the singleton pattern, so calling the method
 * {@link #getInstance()} is necessary to acquire an instance.
 *
 * @author Kasper
 */
public class DatabaseMediator {

	/**
	 * The singleton instance for the database mediator.
	 */
	private static DatabaseMediator instance;

	/* Variables for database connection */
	private final static String url = "jdbc:postgresql://46.101.142.251:5432/electroshop";
	private final static String user = "postgres";
	private final static String password = "1234";

	/**
	 * The database connection.
	 */
	private Connection connection;

	/**
	 * Private constructor.
	 */
	private DatabaseMediator() {
		try {
			connection = DriverManager.getConnection(url, user, password);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Get the singleton instance for the database mediator. This instance is used to access the database of the PIM.
	 *
	 * @return the singleton instance
	 */
	public static DatabaseMediator getInstance() {
		if (instance == null) {
			instance = new DatabaseMediator();
		}

		return instance;
	}

	/**
	 * Safely run the specified query on the database.
	 *
	 * @param query the query to run
	 * @return the result of the query wrapped in a {@link TableData} object
	 */
	private TableData runQuery(String query) {
		TableData table = null;

		//Attempt to run the specified query
		try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(query)) {
			table = new TableData(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return table;
	}

	/**
	 * Get the product with the specified id from the database.
	 *
	 * @param id the id of the product
	 * @return the product with the specified id
	 */
	public Product getProduct(int id) {
		throw new UnsupportedOperationException("Not yet supported");
	}

	public List<Product> getProductsByCategory(String categoryName) {
		throw new UnsupportedOperationException("Not yet supported");
	}

	public List<Product> getProductsByName(String productName) {
		throw new UnsupportedOperationException("Not yet supported");
	}

	public List<Product> getProductsByAttribute(String attributeName) {
		throw new UnsupportedOperationException("Not yet supported");
	}

	public List<Product> getProductsByTag(String tagName) {
		throw new UnsupportedOperationException("Not yet supported");
	}

	public List<Category> getCategories() {
		throw new UnsupportedOperationException("Not yet supported");
	}

	public List<Attribute> getAttributes() {
		throw new UnsupportedOperationException("Not yet supported");
	}

	public static void main(String[] args) {
		DatabaseMediator.getInstance();
	}
}
