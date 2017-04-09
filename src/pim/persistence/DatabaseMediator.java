package pim.persistence;

import pim.business.Attribute;
import pim.business.Category;
import pim.business.Product;

import java.io.*;
import java.sql.*;
import java.util.*;

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
	 * Cache for data read from the database.
	 */
	private DataCache dataCache;

	/**
	 * Private constructor.
	 */
	private DatabaseMediator() {
		try {
			connection = DriverManager.getConnection(url, user, password);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		dataCache = new DataCache();
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
	 * Get the product with the specified id from the database.
	 *
	 * @param id the id of the product
	 * @return the product with the specified id
	 */
	public Product getProduct(String id) {
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

	/**
	 * Get the attribute with the specified id.
	 *
	 * @param id the id of the attribute.
	 * @return the attribute with the specified id
	 * @throws IOException if something goes wrong
	 */
	public Attribute getAttributeByID(String id) throws IOException {
		Attribute a = dataCache.getAttributeIfPresent(id);

		//Read attribute if not present
		if (a == null) {
			try (PreparedStatement getLegalValues = connection.prepareStatement("select * from legalvalue where attributeid = ?");
			     PreparedStatement getAttribute = connection.prepareStatement("select * from attribute where id = ?")) {

				//For every legal value, add it to the set of legal values
				Set<Object> values = new HashSet<>();
				ResultSet legalValues = getLegalValues.executeQuery();

				while (legalValues.next()) {
					Object val = bytesToObject(legalValues.getBytes(2));
					values.add(val);
				}

				//Read attribute data and construct. There should only be one tuple
				ResultSet attributeData = getAttribute.executeQuery();

				while (attributeData.next()) {
					String name = attributeData.getString(2);
					Object defaultValue = bytesToObject(attributeData.getBytes(3));
					a = new Attribute<>(id, name, defaultValue, values);
					dataCache.registerAttributeIfAbsent(a);
				}
			} catch (SQLException e) {
				throw new IOException("Could not read attribute with id " + id + "!", e);
			}
		}

		return a;
	}

	/**
	 * Get a set of all attributes stored in the database.
	 *
	 * @return a set of all attributes stored in the database
	 * @throws IOException if something goes wrong
	 */
	public Set<Attribute> getAttributes() throws IOException {
		Map<String, Set<Object>> values = new HashMap<>();
		Set<Attribute> attributes = new HashSet<>();

		//Attempt to read data from database. Throw exception if something goes wrong
		try (PreparedStatement getLegalValues = connection.prepareStatement("select * from legalvalue;");
		     PreparedStatement getAttributes = connection.prepareStatement("select * from attribute;")) {

			//For every legal value, add it to the set of legal values for the correct attribute
			ResultSet legalValues = getLegalValues.executeQuery();

			while (legalValues.next()) {
				String id = legalValues.getString(1);
				Object val = bytesToObject(legalValues.getBytes(2));

				Set<Object> set = values.getOrDefault(id, new HashSet<>());
				set.add(val);
				values.put(id, set);
			}

			//Construct all attributes and return result
			ResultSet attributeData = getAttributes.executeQuery();

			while (attributeData.next()) {
				String id = attributeData.getString(1);
				String name = attributeData.getString(2);
				Object defaultValue = bytesToObject(attributeData.getBytes(3));

				//If attribute has already been read, reuse it. Otherwise register new attribute
				Attribute a = dataCache.getAttributeIfPresent(id);
				if (a == null) {
					attributes.add(a = new Attribute<>(id, name, defaultValue, values.get(id)));
					dataCache.registerAttributeIfAbsent(a);
				} else {
					attributes.add(a);
				}
			}
		} catch (SQLException e) {
			throw new IOException("Could not read attributes!", e);
		}

		return attributes;
	}

	/**
	 * Close the database connection.
	 */
	public void dispose() {
		DBUtil.close(connection);
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
		try (PreparedStatement toDB = connection.prepareStatement("insert into legalvalue values (?, ?);")) {
			//Read attributes
			Set<Attribute> attributes = null;
			try {
				attributes = getAttributes();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}

			for (Attribute a : attributes) {
				System.out.println("Attribute (" + a.getID().trim() + ", " + a.getName().trim() + ")");
				Set values = a.getLegalValues();
				if (values == null) {
					System.out.println("\tAllows all values");
				} else {
					for (Object o : values) {
						System.out.println("\t[" + o.getClass().getName() + "] " + o);
					}
				}

				System.out.println();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		DatabaseMediator db = DatabaseMediator.getInstance();
		db.doObjectTest();
	}
}
