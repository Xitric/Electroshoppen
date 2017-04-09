package pim.persistence;

import pim.business.Attribute;
import pim.business.Category;
import pim.business.Product;

import java.awt.*;
import java.io.*;
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

	/**
	 * Serialize the specified object and get the result in a byte array for storing in the database.
	 *
	 * @param o the object to serialize
	 * @return the serialized object as a byte array, or null if the object could not be serialized
	 * @see <a href="http://www.easywayserver.com/java/save-serializable-object-in-java/">EasyWayServer - How to save
	 * java object in database</a>
	 */
	private byte[] objectToBytes(Object o) {
		try (ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		     ObjectOutputStream oOut = new ObjectOutputStream(bOut)) {

			//Use the object output stream to serialize an object and store the result in a byte array output stream
			oOut.writeObject(o);
			oOut.flush();

			//Get the byte array from the byte array output stream
			return bOut.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Something went wrong, return null
		return null;
	}

	/**
	 * Deserialize the object stored in the specified byte array.
	 *
	 * @param bytes the byte array containing the serialized object
	 * @return the deserialized object, or null if the object could not be deserialized
	 * @see <a href="http://www.easywayserver.com/java/save-serializable-object-in-java/">EasyWayServer - How to save
	 * java object in database</a>
	 */
	private Object bytesToObject(byte[] bytes) {
		try (ObjectInputStream oIn = new ObjectInputStream(new ByteArrayInputStream(bytes))) {

			//Use an object input stream to read the object from the byte array
			return oIn.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		//Something went wrong, return null
		return null;
	}

	public void doObjectTest() {
		PreparedStatement toDB = null;
		PreparedStatement fromDB = null;
		ResultSet rs = null;

		try {
			toDB = connection.prepareStatement("insert into legalvalue values ('1', ?);");
			fromDB = connection.prepareStatement("select value from legalvalue;");

			toDB.setObject(1, objectToBytes(new Color(139, 180, 221)));
			toDB.executeUpdate();

			rs = fromDB.executeQuery();
			rs.next();
			Color c = (Color) bytesToObject(rs.getBytes(1));
			System.out.println(c);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (toDB != null) {
				try {
					toDB.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (fromDB != null) {
				try {
					fromDB.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
		DatabaseMediator db = DatabaseMediator.getInstance();
		db.doObjectTest();
	}
}
