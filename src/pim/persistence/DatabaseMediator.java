package pim.persistence;

import pim.business.*;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Mediator used to access the underlying database. The mediator uses the singleton pattern, so calling the method
 * {@link #getInstance()} is necessary to acquire an instance.
 *
 * @author Kasper
 * @author mstruntze
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
		     PreparedStatement getProductValues = connection.prepareStatement("SELECT * FROM attributevalue WHERE productid = ?;");
		     PreparedStatement getProductTags = connection.prepareStatement("SELECT * FROM producttag WHERE productid = ?")) {

			getProduct.setString(1, id);
			ResultSet productData = getProduct.executeQuery();

			getProductCategories.setString(1, id);
			ResultSet productCategoryData = getProductCategories.executeQuery();

			getProductValues.setString(1, id);
			ResultSet productValueData = getProductValues.executeQuery();

			getProductTags.setString(1, id);
			ResultSet productTagData = getProductTags.executeQuery();

			Set<Product> result = buildProducts(productData, productCategoryData, productValueData, productTagData);

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
		     PreparedStatement getProductValues = connection.prepareStatement("SELECT attributeid, productid, value FROM attributevalue, product WHERE productid = id AND name = ?;");
			 PreparedStatement getProductTags = connection.prepareStatement("SELECT tagname, product.name FROM producttag, product WHERE producttag.productid = product.id AND product.name = ?;")) {

			getProducts.setString(1, name);
			ResultSet productData = getProducts.executeQuery();

			getProductCategories.setString(1, name);
			ResultSet productCategoryData = getProductCategories.executeQuery();

			getProductValues.setString(1, name);
			ResultSet productValueData = getProductValues.executeQuery();

			getProductTags.setString(1, name);
			ResultSet productTagsData = getProductTags.executeQuery();

			return buildProducts(productData, productCategoryData, productValueData, productTagsData);
		} catch (SQLException e) {
			throw new IOException("Could not read products with name " + name + "!", e);
		}
	}

	/**
	 * Get a set of all tags
	 *
	 * @return the set of all tags
	 */
	public Set<Tag> getTags() {
		try(PreparedStatement tagData = connection.prepareStatement("SELECT * FROM tag")) {
			ResultSet tagResults = tagData.executeQuery();
			return buildTags(tagResults);
		} catch(SQLException e) {
			// TODO: error handling
		}

		return null;
	}

	/**
	 * Builds a set of tags from a result set
	 *
	 * @param tagData The result set of data
	 * @return The set of tags created
	 */
	private Set<Tag> buildTags(ResultSet tagData) {
		Set<Tag> tags = new HashSet<>();

		TagManager tm = TagManager.getInstance();

		try {
			while (tagData.next()) {
				tags.add(tm.createTag(tagData.getString(1)));
			}
		} catch(SQLException e) {
			// TODO: error handling
		}

		return tags;
	}

	/**
	 * Gets all products with the associated tag
	 *
	 * @param tag Tag to find products by
	 * @return A set of products with the provided tag
	 */
	public Set<Product> getProductsByTag(Tag tag) {
		try {
			Set<Product> products = getProducts();
			Iterator<Product> it = products.iterator();

			while(it.hasNext()) {
				Product p = it.next();
				if(!p.containsTag(tag)) {
					products.remove(p);
				}
			}

			return products;
		} catch(IOException e) {
			// TODO: Implement error handling
		}

		return null;
	}

	/**
	 * Stores a tag in the database
	 *
	 * @param tag The tag to save
	 */
	public void saveTag(Tag tag) {
		try (PreparedStatement tagData = connection.prepareStatement("INSERT INTO tag VALUES (?) ON CONFLICT (name) DO NOTHING;")) {
			tagData.setString(1, tag.getName());
			tagData.executeUpdate();
		} catch(SQLException e) {
			// TODO: implement something
		}
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
		     PreparedStatement getProductValues = connection.prepareStatement("SELECT * FROM attributevalue;");
		     PreparedStatement getProductTags = connection.prepareStatement("SELECT * FROM producttag;")) {

			ResultSet productData = getProducts.executeQuery();
			ResultSet productCategoryData = getProductCategories.executeQuery();
			ResultSet productValueData = getProductValues.executeQuery();
			ResultSet productTags = getProductTags.executeQuery();
			return buildProducts(productData, productCategoryData, productValueData, productTags);
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
	private Set<Product> buildProducts(ResultSet productData, ResultSet productCategoryData, ResultSet productValueData, ResultSet productTags) throws SQLException {
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

		//Set all tag
		while(productTags.next()){
			String name = productTags.getString(1);
			Tag t = TagManager.getInstance().createTag(name);
			Product p = products.get(productTags.getString(2));
			p.addTag(t);
		}

		//Return set of products
		return new HashSet<>(products.values());
	}

	/**
	 * Save the specified product in the database. This will overwrite any existing data.
	 *
	 * @param product the product to save
	 */
	public void saveProduct(Product product) {
		saveProducts(Collections.singleton(product));
	}

	/**
	 * Save the specified products in the database. This will overwrite any existing data.
	 *
	 * @param products the products to save
	 */
	public void saveProducts(Collection<Product> products) {
		try (PreparedStatement storeProductData = connection.prepareStatement("INSERT INTO product VALUES (?, ?, ?) ON CONFLICT (id) DO UPDATE SET name = Excluded.name, price = EXCLUDED.price;");
		     PreparedStatement removeProductCategories = connection.prepareStatement("DELETE FROM productcategory WHERE productid = ?;");
		     PreparedStatement addProductCategory = connection.prepareStatement("INSERT INTO productcategory VALUES(?, ?);");
		     PreparedStatement addAttributeValue = connection.prepareStatement("INSERT INTO attributevalue VALUES(?, ?, ?);")) {

			for (Product product : products) {
				//Store basic product data
				storeProductData.setString(1, product.getID());
				storeProductData.setString(2, product.getName());
				storeProductData.setDouble(3, product.getPrice());
				storeProductData.executeUpdate();

				//Reset product categories. When removing all categories from a product its attribute values are
				//automatically removed
				removeProductCategories.setString(1, product.getID());
				removeProductCategories.executeUpdate();

				addProductCategory.setString(1, product.getID());
				for (Category category : product.getCategories()) {
					addProductCategory.setString(2, category.getName());
					addProductCategory.executeUpdate();
				}

				//Add product attribute values
				addAttributeValue.setString(2, product.getID());
				for (Attribute.AttributeValue value : product.getAttributeValues()) {
					addAttributeValue.setString(1, value.getParent().getID());
					addAttributeValue.setObject(3, objectToBytes(value.getValue()));
					addAttributeValue.executeUpdate();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Delete all data for the product with the specified id.
	 *
	 * @param id the id of the product
	 */
	public void deleteProduct(String id) {
		try (PreparedStatement deleteProductData = connection.prepareStatement("delete from product where id = ?")) {
			//Delete product entry. The constraints in the database should ensure that the deletion is cascaded
			deleteProductData.setString(1, id);
			deleteProductData.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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

		CategoryManager categoryManager = CategoryManager.getInstance();

		//Construct all categories and return result
		while (categoryData.next()) {
			String categoryName = categoryData.getString(1);

			//Create new/reuse category
			categories.add(categoryManager.createCategory(categoryName, categoryAttributes.getOrDefault(categoryName, new HashSet<>())));
		}

		return categories;
	}

	/**
	 * Save the specified category in the database. This will overwrite any existing data.
	 *
	 * @param category the category to save
	 */
	public void saveCategory(Category category) {
		saveCategories(Collections.singleton(category));
	}

	/**
	 * Save the specified categories in the database. This will overwrite any existing data.
	 *
	 * @param categories the categories to save
	 */
	public void saveCategories(Collection<Category> categories) {
		try (PreparedStatement storeCategoryData = connection.prepareStatement("INSERT INTO category VALUES (?) ON CONFLICT (name) DO NOTHING;");
		     PreparedStatement deleteRemovedAttributes = connection.prepareStatement("DELETE FROM categoryattribute WHERE categoryname = ?  AND NOT (attributeid = ANY(?));");
		     PreparedStatement addNewAttributes = connection.prepareStatement("INSERT INTO categoryattribute VALUES (?, ?) ON CONFLICT (categoryname, attributeid) DO NOTHING")) {

			for (Category category : categories) {
				//Store basic category data
				storeCategoryData.setString(1, category.getName());
				storeCategoryData.executeUpdate();

				//Delete removed attributes
				//Construct array of attribute ids for this category
				Set<Attribute> attributes = category.getAttributes();
				String[] attributeIDs =
						attributes.stream().map(Attribute::getID).collect(Collectors.toList()).toArray(new String[0]);
				Array attributeArray = connection.createArrayOf("CHAR", attributeIDs);

				deleteRemovedAttributes.setString(1, category.getName());
				deleteRemovedAttributes.setArray(2, attributeArray);
				deleteRemovedAttributes.executeUpdate();

				//The array is automatically freed at some point, so no need to put it in finally
				attributeArray.free();

				//Add new attributes
				addNewAttributes.setString(1, category.getName());
				for (Attribute attribute : attributes) {
					addNewAttributes.setString(2, attribute.getID());
					addNewAttributes.executeUpdate();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Delete all data for the category with the specified name.
	 *
	 * @param name the name of the category
	 */
	public void deleteCategory(String name) {
		try (PreparedStatement deleteCategoryData = connection.prepareStatement("delete from category where name = ?")) {
			//Delete category entry. The constraints in the database should ensure that the deletion is cascaded
			deleteCategoryData.setString(1, name);
			deleteCategoryData.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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

		AttributeManager attributeManager = AttributeManager.getInstance();

		//Construct all attributes and return result
		while (attributeData.next()) {
			String id = attributeData.getString(1);
			String name = attributeData.getString(2);
			Object defaultValue = bytesToObject(attributeData.getBytes(3));

			//Create new/reuse attribute.
			attributes.add(attributeManager.createAttribute(id, name, defaultValue, legalValues.get(id)));
		}

		return attributes;
	}

	/**
	 * Save the specified attribute in the database. This will overwrite any existing data.
	 *
	 * @param attribute the attribute to save
	 */
	public void saveAttribute(Attribute attribute) {
		saveAttributes(Collections.singleton(attribute));
	}

	/**
	 * Save the specified attributes in the database. This will overwrite any existing data.
	 *
	 * @param attributes the attributes to save
	 */
	public void saveAttributes(Collection<Attribute> attributes) {
		try (PreparedStatement storeAttributeData = connection.prepareStatement("INSERT INTO attribute VALUES (?, ?, ?) ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, defaultvalue = EXCLUDED.defaultvalue;");
		     PreparedStatement storeLegalValues = connection.prepareStatement("INSERT INTO legalvalue VALUES (?, ?) ON CONFLICT (attributeid, value) DO NOTHING;")) {

			for (Attribute attribute : attributes) {
				//Store basic attribute data
				storeAttributeData.setString(1, attribute.getID());
				storeAttributeData.setString(2, attribute.getName());
				storeAttributeData.setObject(3, objectToBytes(attribute.getDefaultValue()));
				storeAttributeData.executeUpdate();

				//If the attribute already exists, the legal values should not be changed since they are immutable.
				//Should the immutability be violated, however, the issue lies elsewhere - not here. Thus the
				//possibility of adding new legal values has not been handled. Also, this would not break anything
				storeLegalValues.setString(1, attribute.getID());
				for (Object value : attribute.getLegalValues()) {
					storeLegalValues.setObject(2, objectToBytes(value));
					storeLegalValues.executeUpdate();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Delete all data for the attribute with the specified id.
	 *
	 * @param id the id of the attribute
	 */
	public void deleteAttribute(String id) {
		try (PreparedStatement deleteAttributeData = connection.prepareStatement("delete from attribute where id = ?")) {
			//Delete attribute entry. The constraints in the database should ensure that the deletion is cascaded
			deleteAttributeData.setString(1, id);
			deleteAttributeData.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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

	}

	public static void main(String[] args) {
		DatabaseMediator db = DatabaseMediator.getInstance();
		db.doObjectTest();
	}
}
