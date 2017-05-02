package pim.persistence;

import pim.business.*;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Mediator used to access the underlying database.
 *
 * @author Kasper
 * @author mstruntze
 */
class DatabaseMediator implements PersistenceMediator {

	/* Variables for database connection */
	private final static String url = "jdbc:postgresql://46.101.142.251:5432/electroshop";
	private final static String user = "postgres";
	private final static String password = "1234";

	private DataCache cache;

	/**
	 * The database connection.
	 */
	private Connection connection;

	/**
	 * Package private constructor.
	 */
	DatabaseMediator() {
		try {
			connection = DriverManager.getConnection(url, user, password);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
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

	/**
	 * Get the product with the specified id.
	 *
	 * @param id the id of the product
	 * @return the product with the specified id, or null if no such product exists
	 * @throws IOException if something goes wrong
	 */
	@Override
	public Product getProductByID(int id) throws IOException {
		//Attempt to read data from database. Throw exception if something goes wrong
		try (PreparedStatement getProduct = connection.prepareStatement("SELECT * FROM product WHERE id = ?;");
		     PreparedStatement getProductCategories = connection.prepareStatement("SELECT * FROM productcategory WHERE productid = ?;");
		     PreparedStatement getProductValues = connection.prepareStatement("SELECT * FROM attributevalue WHERE productid = ?;");
		     PreparedStatement getProductTags = connection.prepareStatement("SELECT * FROM producttag WHERE productid = ?");
		     PreparedStatement getProductImages = connection.prepareStatement("SELECT * FROM image WHERE productid = ?")) {

			getProduct.setInt(1, id);
			ResultSet productData = getProduct.executeQuery();

			getProductCategories.setInt(1, id);
			ResultSet productCategoryData = getProductCategories.executeQuery();

			getProductValues.setInt(1, id);
			ResultSet productValueData = getProductValues.executeQuery();

			getProductTags.setInt(1, id);
			ResultSet productTagData = getProductTags.executeQuery();

			getProductImages.setInt(1, id);
			ResultSet productImages = getProductImages.executeQuery();

			Set<Product> result = buildProducts(productData, productCategoryData, productValueData, productTagData, productImages);

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
	@Override
	public Set<Product> getProductsByName(String name) throws IOException {
		try (PreparedStatement getProducts = connection.prepareStatement("SELECT * FROM product WHERE name = ?;");
		     PreparedStatement getProductCategories = connection.prepareStatement("SELECT productid, categoryName FROM productcategory, product WHERE productID = id AND name = ?;");
		     PreparedStatement getProductValues = connection.prepareStatement("SELECT attributeid, productid, value FROM attributevalue, product WHERE productid = id AND name = ?;");
		     PreparedStatement getProductTags = connection.prepareStatement("SELECT tagname, productid FROM producttag, product WHERE productid = id AND name = ?;");
		     PreparedStatement getProductImages = connection.prepareStatement("SELECT url, productid FROM image, product WHERE productid = id AND name = ?")) {

			getProducts.setString(1, name);
			ResultSet productData = getProducts.executeQuery();

			getProductCategories.setString(1, name);
			ResultSet productCategoryData = getProductCategories.executeQuery();

			getProductValues.setString(1, name);
			ResultSet productValueData = getProductValues.executeQuery();

			getProductTags.setString(1, name);
			ResultSet productTagsData = getProductTags.executeQuery();

			getProductImages.setString(1, name);
			ResultSet productImages = getProductImages.executeQuery();

			return buildProducts(productData, productCategoryData, productValueData, productTagsData, productImages);
		} catch (SQLException e) {
			throw new IOException("Could not read products with name " + name + "!", e);
		}
	}

	/**
	 * Get a set of all products in the specified category stored in the database.
	 *
	 * @param category the name of the category
	 * @return a set of all products in the specified category stored in the database
	 * @throws IOException if something goes wrong
	 */
	//TODO: Code duplication should be fixed when I get around to optimizing this shit...
	@Override
	public Set<Product> getProductsByCategory(String category) throws IOException {
		try (PreparedStatement getProducts = connection.prepareStatement("SELECT * FROM product WHERE id IN (SELECT productid FROM productcategory WHERE categoryname = ?);");
		     PreparedStatement getProductCategories = connection.prepareStatement("SELECT productid, categoryName FROM productcategory, product WHERE productID = id AND id IN (SELECT productid FROM productcategory WHERE categoryname = ?);");
		     PreparedStatement getProductValues = connection.prepareStatement("SELECT attributeid, productid, value FROM attributevalue, product WHERE productid = id AND id IN (SELECT productid FROM productcategory WHERE categoryname = ?);");
		     PreparedStatement getProductTags = connection.prepareStatement("SELECT tagname, productid FROM producttag, product WHERE productid = id AND id IN (SELECT productid FROM productcategory WHERE categoryname = ?);");
		     PreparedStatement getProductImages = connection.prepareStatement("SELECT url, productid FROM image, product WHERE productid = id AND id IN (SELECT productid FROM productcategory WHERE categoryname = ?);")) {

			getProducts.setString(1, category);
			ResultSet productData = getProducts.executeQuery();

			getProductCategories.setString(1, category);
			ResultSet productCategoryData = getProductCategories.executeQuery();

			getProductValues.setString(1, category);
			ResultSet productValueData = getProductValues.executeQuery();

			getProductTags.setString(1, category);
			ResultSet productTagsData = getProductTags.executeQuery();

			getProductImages.setString(1, category);
			ResultSet productImages = getProductImages.executeQuery();

			return buildProducts(productData, productCategoryData, productValueData, productTagsData, productImages);
		} catch (SQLException e) {
			throw new IOException("Could not read products from category with name " + category + "!", e);
		}
	}

	/**
	 * Gets all products with the associated tag
	 *
	 * @param tag tag to find products by
	 * @return a set of products with the provided tag
	 */
	@Override
	public Set<Product> getProductsByTag(Tag tag) {
		try { //TODO: Optimize!
			Set<Product> products = getProducts();

			for (Product p : products) {
				if (!p.containsTag(tag)) {
					products.remove(p);
				}
			}

			return products;
		} catch (IOException e) {
			// TODO: Implement error handling
		}

		return null;
	}

	/**
	 * Get a set of all products stored in the database.
	 *
	 * @return a set of all products stored in the database
	 * @throws IOException if something goes wrong
	 */
	@Override
	public Set<Product> getProducts() throws IOException {
		try (PreparedStatement getProducts = connection.prepareStatement("SELECT * FROM product;");
		     PreparedStatement getProductCategories = connection.prepareStatement("SELECT * FROM productcategory;");
		     PreparedStatement getProductValues = connection.prepareStatement("SELECT * FROM attributevalue;");
		     PreparedStatement getProductTags = connection.prepareStatement("SELECT * FROM producttag;");
		     PreparedStatement getProductImages = connection.prepareStatement("SELECT * FROM image")) {

			ResultSet productData = getProducts.executeQuery();
			ResultSet productCategoryData = getProductCategories.executeQuery();
			ResultSet productValueData = getProductValues.executeQuery();
			ResultSet productTags = getProductTags.executeQuery();
			ResultSet productImages = getProductImages.executeQuery();
			return buildProducts(productData, productCategoryData, productValueData, productTags, productImages);
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
	 * @param productTags         the data describing product tags
	 * @param productImages       the data describing product images
	 * @return a set of all products that could be built from the data
	 * @throws SQLException if something goes wrong
	 */
	private Set<Product> buildProducts(ResultSet productData, ResultSet productCategoryData, ResultSet productValueData, ResultSet productTags, ResultSet productImages) throws SQLException {
		Map<Integer, Product> products = new HashMap<>();

		//Construct all products
		while (productData.next()) {
			int id = productData.getInt(1);
			String name = productData.getString(2).trim();
			double price = productData.getDouble(3);

			products.put(id, cache.createProduct(id, name, price));
		}

		//Add all product categories
		while (productCategoryData.next()) {
			int productID = productCategoryData.getInt(1);
			String categoryName = productCategoryData.getString(2).trim();

			try {
				Category category = getCategoryByName(categoryName);
				products.get(productID).addCategory(category);
			} catch (IOException e) {
			} //The database should guarantee that this exception never occurs
		}

		//Set all attribute values
		while (productValueData.next()) {
			int attributeID = productValueData.getInt(1);
			int productID = productValueData.getInt(2);
			Object value = bytesToObject(productValueData.getBytes(3));

			try {
				Attribute attribute = getAttributeByID(attributeID);
				products.get(productID).setAttribute(attribute, value);
			} catch (IOException e) {
			} //The database should guarantee that this exception never occurs
		}

		//Add all tags
		while (productTags.next()) {
			String name = productTags.getString(1).trim();
			Tag t = cache.createTag(name);
			Product p = products.get(productTags.getInt(2));
			p.addTag(t);
		}

		//Add all images
		while (productImages.next()) {
			String url = productImages.getString(1).trim();
			Image img = cache.createImage(url);
			int productID = productImages.getInt(2);
			products.get(productID).addImage(img);
		}

		//Return set of products
		return new HashSet<>(products.values());
	}

	/**
	 * Save the specified product in the database. This will overwrite any existing data.
	 *
	 * @param product the product to save
	 */
	@Override
	public void saveProduct(Product product) {
		saveProducts(Collections.singleton(product));
	}

	/**
	 * Save the specified products in the database. This will overwrite any existing data.
	 *
	 * @param products the products to save
	 */
	@Override
	public void saveProducts(Collection<Product> products) {
		try (PreparedStatement storeProductData = connection.prepareStatement("INSERT INTO product VALUES (?, ?, ?) ON CONFLICT (id) DO UPDATE SET name = Excluded.name, price = EXCLUDED.price;");
		     PreparedStatement removeProductCategories = connection.prepareStatement("DELETE FROM productcategory WHERE productid = ?;");
		     PreparedStatement addProductCategory = connection.prepareStatement("INSERT INTO productcategory VALUES(?, ?);");
		     PreparedStatement addAttributeValue = connection.prepareStatement("INSERT INTO attributevalue VALUES(?, ?, ?);");
		     PreparedStatement removeProductTags = connection.prepareStatement("DELETE FROM producttag WHERE productid = ?");
		     PreparedStatement saveProductTags = connection.prepareStatement("INSERT INTO producttag VALUES(?, ?)");
		     PreparedStatement removeProductImages = connection.prepareStatement("DELETE FROM image WHERE productid = ?");
		     PreparedStatement saveProductImages = connection.prepareStatement("INSERT INTO image VALUES(?, ?)")) {

			for (Product product : products) {
				//Store basic product data
				storeProductData.setInt(1, product.getID());
				storeProductData.setString(2, product.getName());
				storeProductData.setDouble(3, product.getPrice());
				storeProductData.executeUpdate();

				//Reset product categories. When removing all categories from a product its attribute values are
				//automatically removed
				removeProductCategories.setInt(1, product.getID());
				removeProductCategories.executeUpdate();

				addProductCategory.setInt(1, product.getID());
				for (Category category : product.getCategories()) {
					addProductCategory.setString(2, category.getName());
					addProductCategory.executeUpdate();
				}

				//Add product attribute values
				addAttributeValue.setInt(2, product.getID());
				for (Attribute.AttributeValue value : product.getAttributeValues()) {
					addAttributeValue.setInt(1, value.getParent().getID());
					addAttributeValue.setObject(3, objectToBytes(value.getValue()));
					addAttributeValue.executeUpdate();
				}

				//Store product tags
				removeProductTags.setInt(1, product.getID());
				removeProductTags.executeUpdate();
				saveProductTags.setInt(2, product.getID());
				for (Tag tag : product.getTags()) {
					saveProductTags.setString(1, tag.getName());
					saveProductTags.executeUpdate();
				}

				//Store images
				removeProductImages.setInt(1, product.getID());
				removeProductImages.executeUpdate();
				saveProductImages.setInt(2, product.getID());
				for (Image image : product.getImages()) {
					saveProductImages.setString(1, image.getUrl());
					saveProductImages.executeUpdate();
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
	@Override
	public void deleteProduct(int id) {
		try (PreparedStatement deleteProductData = connection.prepareStatement("DELETE FROM product WHERE id = ?")) {
			//Delete product entry. The constraints in the database should ensure that the deletion is cascaded
			deleteProductData.setInt(1, id);
			deleteProductData.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get a set of all tags.
	 *
	 * @return the set of all tags
	 * @throws IOException if something goes wrong
	 */
	@Override
	public Set<Tag> getTags() throws IOException {
		try (PreparedStatement tagData = connection.prepareStatement("SELECT * FROM tag")) {
			ResultSet tagResults = tagData.executeQuery();
			return buildTags(tagResults);
		} catch (SQLException e) {
			throw new IOException("Could not read tags!", e);
		}
	}

	/**
	 * Builds a set of tags from a result set.
	 *
	 * @param tagData the result set of data
	 * @return the set of tags created
	 * @throws SQLException if something goes wrong
	 */
	private Set<Tag> buildTags(ResultSet tagData) throws SQLException {
		Set<Tag> tags = new HashSet<>();

		while (tagData.next()) {
			tags.add(cache.createTag(tagData.getString(1).trim()));
		}

		return tags;
	}

	/**
	 * Store a tag in the database.
	 *
	 * @param tag the tag to save
	 */
	@Override
	public void saveTag(Tag tag) {
		try (PreparedStatement tagData = connection.prepareStatement("INSERT INTO tag VALUES (?) ON CONFLICT (name) DO NOTHING;")) {
			tagData.setString(1, tag.getName());
			tagData.executeUpdate();
		} catch (SQLException e) {
			// TODO: implement something
		}
	}

	/**
	 * Delete the tag with the specified name.
	 *
	 * @param name the name of the tag
	 */
	@Override
	public void deleteTag(String name) {
		try (PreparedStatement delete = connection.prepareStatement("DELETE FROM tag WHERE name = ?")) {
			//Deletion should be cascaded
			delete.setString(1, name);
			delete.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get all the images stored for the specified product.
	 *
	 * @param productID the id of the product
	 * @return a set of all the images for the specified product
	 * @throws IOException if something goes wrong
	 */
	@Override
	public Set<Image> getImagesForProduct(String productID) throws IOException {
		try (PreparedStatement getImages = connection.prepareStatement("SELECT url FROM image WHERE productid = ?")) {

			getImages.setString(1, productID);
			ResultSet imageData = getImages.executeQuery();

			return buildImages(imageData);
		} catch (SQLException e) {
			throw new IOException("Could not read images!", e);
		}
	}

	/**
	 * Builds a set of images from a result set.
	 *
	 * @param imageData the data describing the images
	 * @return the set of images
	 * @throws SQLException if something goes wrong
	 */
	private Set<Image> buildImages(ResultSet imageData) throws SQLException {
		Set<Image> images = new HashSet<>();

		while (imageData.next()) {
			String url = imageData.getString(1).trim();
			images.add(cache.createImage(url));
		}

		return images;
	}


	/**
	 * Get the category with the specified name.
	 *
	 * @param name the name of the category
	 * @return the category with the specified name, or null if no such category exists
	 * @throws IOException if something goes wrong
	 */
	@Override
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
	@Override
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
	 * Build a set of categories from the specified data.
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
			String categoryName = categoryAttributeData.getString(1).trim();
			int attributeID = categoryAttributeData.getInt(2);

			Set<Attribute> set = categoryAttributes.getOrDefault(categoryName, new HashSet<>());
			try {
				set.add(getAttributeByID(attributeID));
			} catch (IOException e) {
			} //The database should guarantee that this exception never occurs
			categoryAttributes.put(categoryName, set);
		}

		//Construct all categories and return result
		while (categoryData.next()) {
			String categoryName = categoryData.getString(1).trim();

			//Create new/reuse category
			categories.add(cache.createCategory(categoryName, categoryAttributes.getOrDefault(categoryName, new HashSet<>())));
		}

		return categories;
	}

	/**
	 * Save the specified category in the database. This will overwrite any existing data.
	 *
	 * @param category the category to save
	 */
	@Override
	public void saveCategory(Category category) {
		saveCategories(Collections.singleton(category));
	}

	/**
	 * Save the specified categories in the database. This will overwrite any existing data.
	 *
	 * @param categories the categories to save
	 */
	@Override
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
				Integer[] attributeIDs =
						attributes.stream().map(Attribute::getID).collect(Collectors.toList()).toArray(new Integer[0]);
				Array attributeArray = connection.createArrayOf("INTEGER", attributeIDs);

				deleteRemovedAttributes.setString(1, category.getName());
				deleteRemovedAttributes.setArray(2, attributeArray);
				deleteRemovedAttributes.executeUpdate();

				//The array is automatically freed at some point, so no need to put it in finally
				attributeArray.free();

				//Add new attributes
				addNewAttributes.setString(1, category.getName());
				for (Attribute attribute : attributes) {
					addNewAttributes.setInt(2, attribute.getID());
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
	@Override
	public void deleteCategory(String name) {
		try (PreparedStatement deleteCategoryData = connection.prepareStatement("DELETE FROM category WHERE name = ?")) {
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
	@Override
	public Attribute getAttributeByID(int id) throws IOException {
		//Attempt to read data from database. Throw exception if something goes wrong
		try (PreparedStatement getAttribute = connection.prepareStatement("SELECT * FROM attribute WHERE id = ?");
		     PreparedStatement getLegalValues = connection.prepareStatement("SELECT * FROM legalvalue WHERE attributeid = ?")) {

			getAttribute.setInt(1, id);
			ResultSet attributeData = getAttribute.executeQuery();
			getLegalValues.setInt(1, id);
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
	@Override
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
		Map<Integer, Set<Object>> legalValues = new HashMap<>();
		Set<Attribute> attributes = new HashSet<>();

		//For every legal value, add it to the set of legal values for the correct attribute
		while (legalValueData.next()) {
			int id = legalValueData.getInt(1);
			Object val = bytesToObject(legalValueData.getBytes(2));

			Set<Object> set = legalValues.getOrDefault(id, new HashSet<>());
			set.add(val);
			legalValues.put(id, set);
		}

		//Construct all attributes and return result
		while (attributeData.next()) {
			int id = attributeData.getInt(1);
			String name = attributeData.getString(2).trim();
			Object defaultValue = bytesToObject(attributeData.getBytes(3));

			//Create new/reuse attribute.
			attributes.add(cache.createAttribute(id, name, defaultValue, legalValues.get(id)));
		}

		return attributes;
	}

	@Override
	public int createAttribute(String name, Object defaultValue, Set<Object> legalValues) throws IOException {
		try (PreparedStatement storeAttributeData = connection.prepareStatement("INSERT INTO attribute VALUES (DEFAULT, ?, ?) RETURNING id;");
		     PreparedStatement storeLegalValues = connection.prepareStatement("INSERT INTO legalvalue VALUES (? , ?)")) {

			int id;

			//Store basic attribute data
			storeAttributeData.setString(1, name);
			storeAttributeData.setObject(2, objectToBytes(defaultValue));
			if (storeAttributeData.execute()) {
				//Get id
				ResultSet result = storeAttributeData.getResultSet();
				result.next();
				id = result.getInt(1);
			} else {
				//Nothing returned, so something must have gone wrong
				throw new IOException("Could not create new attribute! No ID returned from database");
			}

			//Store legal values
			storeLegalValues.setInt(1, id);
			for (Object value: legalValues) {
				storeLegalValues.setObject(2, objectToBytes(value));
				storeLegalValues.executeUpdate();
			}

			return id;
		} catch (SQLException e) {
			throw new IOException("Could not create new attribute!", e);
		}
	}

	/**
	 * Save the specified attribute in the database. This will overwrite any existing data.
	 *
	 * @param attribute the attribute to save
	 */
	@Override
	public void saveAttribute(Attribute attribute) {
		saveAttributes(Collections.singleton(attribute));
	}

	/**
	 * Save the specified attributes in the database. This will overwrite any existing data.
	 *
	 * @param attributes the attributes to save
	 */
	@Override
	public void saveAttributes(Collection<Attribute> attributes) {
		try (PreparedStatement storeAttributeData = connection.prepareStatement("INSERT INTO attribute VALUES (?, ?, ?) ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, defaultvalue = EXCLUDED.defaultvalue;");
		     PreparedStatement storeLegalValues = connection.prepareStatement("INSERT INTO legalvalue VALUES (?, ?) ON CONFLICT (attributeid, value) DO NOTHING;")) {

			for (Attribute attribute : attributes) {
				//Store basic attribute data
				storeAttributeData.setInt(1, attribute.getID());
				storeAttributeData.setString(2, attribute.getName());
				storeAttributeData.setObject(3, objectToBytes(attribute.getDefaultValue()));
				storeAttributeData.executeUpdate();

				//If the attribute already exists, the legal values should not be changed since they are immutable.
				//Should the immutability be violated, however, the issue lies elsewhere - not here. Thus the
				//possibility of adding new legal values has not been handled. Also, this would not break anything
				storeLegalValues.setInt(1, attribute.getID());
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
	@Override
	public void deleteAttribute(int id) {
		try (PreparedStatement deleteAttributeData = connection.prepareStatement("DELETE FROM attribute WHERE id = ?")) {
			//Delete attribute entry. The constraints in the database should ensure that the deletion is cascaded
			deleteAttributeData.setInt(1, id);
			deleteAttributeData.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setCache(DataCache cache) {
		this.cache = cache;
	}

	/**
	 * Close the database connection.
	 */
	@Override
	public void dispose() {
		DBUtil.close(connection);
	}
}
