package pim.persistence;

import pim.business.Attribute;
import pim.business.Category;
import pim.business.Product;

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

	//TODO: Support tags and images
	/**
	 * Get the product with the specified id.
	 *
	 * @param id the id of the product
	 * @return the product with the specified id, or null if no such product exists
	 * @throws IOException if something goes wrong
	 */
	public Product getProductByID(String id) throws IOException {
		//Attempt to read data from database. Throw exception if something goes wrong
		try (PreparedStatement getProduct = connection.prepareStatement("SELECT * FROM product WHERE id = ?;");
		     PreparedStatement getProductCategories = connection.prepareStatement("SELECT * FROM productcategory WHERE productid = ?;");
		     PreparedStatement getProductValues = connection.prepareStatement("SELECT * FROM attributevalue WHERE productid = ?;")) {

			getProduct.setString(1, id);
			ResultSet productData = getProduct.executeQuery();
			getProductCategories.setString(1, id);
			ResultSet productCategoryData = getProductCategories.executeQuery();
			getProductValues.setString(1, id);
			ResultSet productValueData = getProductValues.executeQuery();
			Set<Product> result = buildProducts(productData, productCategoryData, productValueData);

			//If the set is empty, no product with the specified id was found. Otherwise, the set should contain only
			//one value, that we return
			if (result.size() == 0) {
				return null;
			} else {
				return result.toArray(new Product[0])[0];
			}
		} catch (SQLException e) {
			throw new IOException("Could not read product with id " + id + "!", e);
		}
	}

	/**
	 * Get a set of all products with the specified name stored in the database.
	 *
	 * @param name the name of the products
	 * @return a set of all products with the specified name stored in the database
	 * @throws IOException if something goes wrong
	 */
	public Set<Product> getProductsByName(String name) throws IOException {
		try (PreparedStatement getProducts = connection.prepareStatement("SELECT * FROM product WHERE name = ?;");
		     PreparedStatement getProductCategories = connection.prepareStatement("SELECT productid, categoryName FROM productcategory, product WHERE productID = id AND name = ?;");
		     PreparedStatement getProductValues = connection.prepareStatement("SELECT attributeid, productid, value FROM attributevalue, product WHERE productid = id AND name = ?;")) {

			getProducts.setString(1, name);
			ResultSet productData = getProducts.executeQuery();
			getProductCategories.setString(1, name);
			ResultSet productCategoryData = getProductCategories.executeQuery();
			getProductValues.setString(1, name);
			ResultSet productValueData = getProductValues.executeQuery();
			return buildProducts(productData, productCategoryData, productValueData);
		} catch (SQLException e) {
			throw new IOException("Could not read products with name " + name + "!", e);
		}
	}

	//TODO: Support tags
	public Set<Product> getProductsByTag(String productTag) {
		return null;
	}

	/**
	 * Get a set of all products stored in the database.
	 *
	 * @return a set of all products stored in the database
	 * @throws IOException if something goes wrong
	 */
	public Set<Product> getProducts() throws IOException {
		try (PreparedStatement getProducts = connection.prepareStatement("SELECT * FROM product;");
		     PreparedStatement getProductCategories = connection.prepareStatement("SELECT * FROM productcategory;");
		     PreparedStatement getProductValues = connection.prepareStatement("SELECT * FROM attributevalue;")) {

			ResultSet productData = getProducts.executeQuery();
			ResultSet productCategoryData = getProductCategories.executeQuery();
			ResultSet productValueData = getProductValues.executeQuery();
			return buildProducts(productData, productCategoryData, productValueData);
		} catch (SQLException e) {
			throw new IOException("Could not read products!", e);
		}
	}

	/**
	 * Build a set of products from the specified data.
	 *
	 * @param productData         the data describing product ids, names and prices
	 * @param productCategoryData the data describing product categories
	 * @param productValueData    the data describing attribute values on products
	 * @return a set of all products that could be built from the data
	 * @throws SQLException if something goes wrong
	 */
	private Set<Product> buildProducts(ResultSet productData, ResultSet productCategoryData, ResultSet productValueData) throws SQLException {
		Map<String, Product> products = new HashMap<>();

		//Construct all products
		while (productData.next()) {
			String id = productData.getString(1);
			String name = productData.getString(2);
			double price = productData.getDouble(3);

			products.put(id, new Product(id, name, price));
		}

		//Add all product categories
		while (productCategoryData.next()) {
			String productID = productCategoryData.getString(1);
			String categoryName = productCategoryData.getString(2);

			try {
				Category category = getCategoryByName(categoryName);
				products.get(productID).addCategory(category);
			} catch (IOException e) {
			} //The database should guarantee that this exception never occurs
		}

		//Set all attribute values
		while (productValueData.next()) {
			String attributeID = productValueData.getString(1);
			String productID = productValueData.getString(2);
			Object value = bytesToObject(productValueData.getBytes(3));

			try {
				Attribute attribute = getAttributeByID(attributeID);
				products.get(productID).setAttribute(attribute, value);
			} catch (IOException e) {
			} //The database should guarantee that this exception never occurs
		}

		//Return set of products
		return new HashSet<>(products.values());
	}

	/**
	 * Get the category with the specified name.
	 *
	 * @param name the name of the category
	 * @return the category with the specified name, or null if no such category exists
	 * @throws IOException if something goes wrong
	 */
	public Category getCategoryByName(String name) throws IOException {
		//Attempt to read data from database. Throw exception if something goes wrong
		try (PreparedStatement getCategory = connection.prepareStatement("SELECT * FROM category WHERE name = ?;");
		     PreparedStatement getAttributes = connection.prepareStatement("SELECT * FROM categoryattribute WHERE categoryname = ?;")) {

			getCategory.setString(1, name);
			ResultSet categoryData = getCategory.executeQuery();
			getAttributes.setString(1, name);
			ResultSet categoryAttributeData = getAttributes.executeQuery();
			Set<Category> result = buildCategories(categoryData, categoryAttributeData);

			//If the set is empty, no category with the specified name was found. Otherwise, the set should contain only
			//one value, that we return
			if (result.size() == 0) {
				return null;
			} else {
				return result.toArray(new Category[0])[0];
			}
		} catch (SQLException e) {
			throw new IOException("Could not read category with name " + name + "!", e);
		}
	}

	/**
	 * Get a set of all categories stored in the database.
	 *
	 * @return a set of all categories stored in the database
	 * @throws IOException if something goes wrong
	 */
	public Set<Category> getCategories() throws IOException {
		try (PreparedStatement getCategories = connection.prepareStatement("SELECT * FROM category;");
		     PreparedStatement getAttributes = connection.prepareStatement("SELECT * FROM categoryattribute;")) {

			ResultSet categoryData = getCategories.executeQuery();
			ResultSet categoryAttributeData = getAttributes.executeQuery();
			return buildCategories(categoryData, categoryAttributeData);
		} catch (SQLException e) {
			throw new IOException("Could not read categories!", e);
		}
	}

	/**
	 * Build a set of attributes from the specified data.
	 *
	 * @param categoryData          the data describing category names
	 * @param categoryAttributeData the data describing attributes on categories
	 * @return a set of all categories that could be built from the data
	 * @throws SQLException if something goes wrong
	 */
	private Set<Category> buildCategories(ResultSet categoryData, ResultSet categoryAttributeData) throws SQLException {
		Map<String, Set<Attribute>> categoryAttributes = new HashMap<>();
		Set<Category> categories = new HashSet<>();

		//Read all category attributes
		while (categoryAttributeData.next()) {
			String categoryName = categoryAttributeData.getString(1);
			String attributeID = categoryAttributeData.getString(2);

			Set<Attribute> set = categoryAttributes.getOrDefault(categoryName, new HashSet<>());
			try {
				set.add(getAttributeByID(attributeID));
			} catch (IOException e) {
			} //The database should guarantee that this exception never occurs
			categoryAttributes.put(categoryName, set);
		}

		//Construct all categories and return result
		while (categoryData.next()) {
			String categoryName = categoryData.getString(1);

			//If category has already been read, reuse it. Otherwise register new category
			Category c = dataCache.getCategoryIfPresent(categoryName);
			if (c == null) {
				categories.add(c = new Category(categoryName, categoryAttributes.get(categoryName)));
				dataCache.registerCategoryIfAbsent(c);
			} else {
				categories.add(c);
			}
		}

		return categories;
	}

	/**
	 * Get the attribute with the specified id.
	 *
	 * @param id the id of the attribute
	 * @return the attribute with the specified id, or null if no such attribute exists
	 * @throws IOException if something goes wrong
	 */
	public Attribute getAttributeByID(String id) throws IOException {
		//Attempt to read data from database. Throw exception if something goes wrong
		try (PreparedStatement getAttribute = connection.prepareStatement("SELECT * FROM attribute WHERE id = ?");
		     PreparedStatement getLegalValues = connection.prepareStatement("SELECT * FROM legalvalue WHERE attributeid = ?")) {

			getAttribute.setString(1, id);
			ResultSet attributeData = getAttribute.executeQuery();
			getLegalValues.setString(1, id);
			ResultSet legalValueData = getLegalValues.executeQuery();
			Set<Attribute> result = buildAttributes(attributeData, legalValueData);

			//If the set is empty, no attribute with the specified id was found. Otherwise, the set should contain only
			//one value, that we return
			if (result.size() == 0) {
				return null;
			} else {
				return result.toArray(new Attribute[0])[0];
			}
		} catch (SQLException e) {
			throw new IOException("Could not read attribute with id " + id + "!", e);
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
		try (PreparedStatement getAttributes = connection.prepareStatement("SELECT * FROM attribute;");
		     PreparedStatement getLegalValues = connection.prepareStatement("SELECT * FROM legalvalue;")) {

			ResultSet attributeData = getAttributes.executeQuery();
			ResultSet legalValueData = getLegalValues.executeQuery();
			return buildAttributes(attributeData, legalValueData);
		} catch (SQLException e) {
			throw new IOException("Could not read attributes!", e);
		}
	}

	/**
	 * Build a set of attributes from the specified data.
	 *
	 * @param attributeData  the data describing attribute ids, names and default values
	 * @param legalValueData the data describing legal values of attributes
	 * @return a set of all attributes that could be built from the data
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
		try (PreparedStatement makeAttribute = connection.prepareStatement("INSERT INTO attribute VALUES (?, ?, ?);");
		     PreparedStatement makeLegalValue = connection.prepareStatement("INSERT INTO legalvalue VALUES (?, ?);");
		     PreparedStatement makeCategory = connection.prepareStatement("INSERT INTO category VALUES (?);");
		     PreparedStatement makeCategoryAttribute = connection.prepareStatement("INSERT INTO categoryattribute VALUES (?, ?);")) {

			//Make attributes
//			makeAttribute.setString(1, "0");
//			makeAttribute.setString(2, "Color");
//			makeAttribute.setObject(3, objectToBytes(new Color(0, 0, 0)));
//			makeAttribute.executeUpdate();
//
//			makeAttribute.setString(1, "1");
//			makeAttribute.setString(2, "Manufacturing Country");
//			makeAttribute.setObject(3, objectToBytes("DK"));
//			makeAttribute.executeUpdate();
//
//			makeAttribute.setString(1, "2");
//			makeAttribute.setString(2, "Length");
//			makeAttribute.setObject(3, objectToBytes(0));
//			makeAttribute.executeUpdate();
//
//			//Make legal values
//			makeLegalValue.setString(1, "0");
//			makeLegalValue.setObject(2, objectToBytes(new Color(255, 0, 0)));
//			makeLegalValue.executeUpdate();
//			makeLegalValue.setObject(2, objectToBytes(new Color(0, 255, 0)));
//			makeLegalValue.executeUpdate();
//			makeLegalValue.setObject(2, objectToBytes(new Color(0, 0, 255)));
//			makeLegalValue.executeUpdate();
//			makeLegalValue.setObject(2, objectToBytes(new Color(255, 255, 255)));
//			makeLegalValue.executeUpdate();
//			makeLegalValue.setObject(2, objectToBytes(new Color(0, 0, 0)));
//			makeLegalValue.executeUpdate();
//
//			makeLegalValue.setString(1, "1");
//			makeLegalValue.setObject(2, objectToBytes("DK"));
//			makeLegalValue.executeUpdate();
//			makeLegalValue.setObject(2, objectToBytes("GB"));
//			makeLegalValue.executeUpdate();
//			makeLegalValue.setObject(2, objectToBytes("DE"));
//			makeLegalValue.executeUpdate();
//			makeLegalValue.setObject(2, objectToBytes("FR"));
//			makeLegalValue.executeUpdate();
//
//			//Make categories
//			makeCategory.setString(1, "Mice");
//			makeCategory.executeUpdate();
//			makeCategory.setString(1, "Rulers");
//			makeCategory.executeUpdate();
//
//			//Make category attributes
//			makeCategoryAttribute.setString(1, "Mice");
//			makeCategoryAttribute.setString(2, "0");
//			makeCategoryAttribute.executeUpdate();
//			makeCategoryAttribute.setString(2, "1");
//			makeCategoryAttribute.executeUpdate();
//
//			makeCategoryAttribute.setString(1, "Rulers");
//			makeCategoryAttribute.setString(2, "1");
//			makeCategoryAttribute.executeUpdate();
//			makeCategoryAttribute.setString(2, "2");
//			makeCategoryAttribute.executeUpdate();

			//Read categories and print
			try {
				Set<Category> categories = getCategories();

				for (Category c : categories) {
					System.out.println(c.getName());

					for (Attribute a : c.getAttributes()) {
						System.out.println("\t" + a.getName().trim() + " [default: " + a.createValue().getValue() + "]");

						if (a.getLegalValues() == null) {
							System.out.println("\t\tAll values legal");
						} else {
							for (Object o : a.getLegalValues()) {
								System.out.println("\t\t" + o);
							}
						}
					}

					System.out.println();
				}
			} catch (IOException e) {
				e.printStackTrace();
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
