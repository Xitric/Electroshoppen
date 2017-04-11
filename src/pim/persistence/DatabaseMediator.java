package pim.persistence;

import pim.business.Attribute;
import pim.business.Category;

import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
	 * Get a set of all categories stored in the database.
	 *
	 * @return a set of all categories stored in the database
	 * @throws IOException if something goes wrong
	 */
	public Set<Category> getCategories() throws IOException {
		try (PreparedStatement getCategories = connection.prepareStatement("select * from category;")) {

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Get the attribute with the specified id.
	 *
	 * @param id the id of the attribute.
	 * @return the attribute with the specified id, or null if no such attribute exists
	 * @throws IOException if something goes wrong
	 */
	public Attribute getAttributeByID(String id) throws IOException {
		//Attempt to read data from database. Throw exception if something goes wrong
		try (PreparedStatement getLegalValues = connection.prepareStatement("select * from legalvalue where attributeid = ?");
		     PreparedStatement getAttribute = connection.prepareStatement("select * from attribute where id = ?")) {

			getLegalValues.setString(1, id);
			ResultSet legalValues = getLegalValues.executeQuery();
			getAttribute.setString(1, id);
			ResultSet attributeData = getAttribute.executeQuery();
			Set<Attribute> result =  buildAttributes(attributeData, legalValues);

			//If the set is empty, no attribute with the specified id was found. Otherwise, the set should contain only
			//one value, that we return
			if (result.size() == 0) {
				return null;
			} else {
				return result.toArray(new Attribute[0])[0];
			}
		} catch (SQLException e) {
			throw new IOException("Could not read attributes!", e);
		}
	}

	/**
	 * Get a set of all attributes stored in the database.
	 *
	 * @return a set of all attributes stored in the database
	 * @throws IOException if something goes wrong
	 */
	public Set<Attribute> getAttributes() throws IOException {
		//Attempt to read data from database. Throw exception if something goes wrong
		try (PreparedStatement getLegalValues = connection.prepareStatement("select * from legalvalue;");
		     PreparedStatement getAttributes = connection.prepareStatement("select * from attribute;")) {

			ResultSet legalValues = getLegalValues.executeQuery();
			ResultSet attributeData = getAttributes.executeQuery();
			return buildAttributes(attributeData, legalValues);
		} catch (SQLException e) {
			throw new IOException("Could not read attributes!", e);
		}
	}

	/**
	 * Build a set of attributes from the specified data.
	 *
	 * @param attributeData the data describing attribute ids, names and default values
	 * @param legalValueData the data describing legal values of attributes
	 * @return a set of all attributes that could be build from the data
	 * @throws SQLException if something goes wrong
	 */
	private Set<Attribute> buildAttributes(ResultSet attributeData, ResultSet legalValueData) throws SQLException {
		Map<String, Set<Object>> legalValues = new HashMap<>();
		Set<Attribute> attributes = new HashSet<>();

		//For every legal value, add it to the set of legal values for the correct attribute
		while (legalValueData.next()) {
			String id = legalValueData.getString(1);
			Object val = bytesToObject(legalValueData.getBytes(2));

			Set<Object> set = legalValues.getOrDefault(id, new HashSet<>());
			set.add(val);
			legalValues.put(id, set);
		}

		//Construct all attributes and return result
		while (attributeData.next()) {
			String id = attributeData.getString(1);
			String name = attributeData.getString(2);
			Object defaultValue = bytesToObject(attributeData.getBytes(3));

			//If attribute has already been read, reuse it. Otherwise register new attribute
			Attribute a = dataCache.getAttributeIfPresent(id);
			if (a == null) {
				attributes.add(a = new Attribute(id, name, defaultValue, legalValues.get(id)));
				dataCache.registerAttributeIfAbsent(a);
			} else {
				attributes.add(a);
			}
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
	private static byte[] objectToBytes(Object o) {
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
	private static Object bytesToObject(byte[] bytes) {
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
