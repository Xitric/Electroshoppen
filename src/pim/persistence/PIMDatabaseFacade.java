package pim.persistence;

import pim.business.*;
import shared.DBUtil;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;

/**
 * Implementation of the PIMPersistenceFacade interface for use with JDBC.
 *
 * @author Kasper
 * @author Niels
 */
class PIMDatabaseFacade implements PIMPersistenceFacade {

	/* Variables for database connection */
	private final static String url = "jdbc:postgresql://46.101.142.251:5432/electroshop";
//	private final static String url = "jdbc:postgresql://localhost/PIM";
	private final static String user = "postgres";
	private final static String password = "1234";

	/**
	 * The database connection.
	 */
	private Connection connection;

	/* Variables for data manipulation */
	private ProductPersistor productPersistor;
	private CategoryPersistor categoryPersistor;
	private AttributePersistor attributePersistor;
	private TagPersistor tagPersistor;
	private DataCache cache;

	/**
	 * Constructs a new persistence facade for use with JDBC.
	 */
	public PIMDatabaseFacade() {
		productPersistor = new ProductPersistor(this);
		categoryPersistor = new CategoryPersistor(this);
		attributePersistor = new AttributePersistor(this);
		tagPersistor = new TagPersistor(this);
	}

	/**
	 * Get the connection to the database. If the connection has been closed because of inactivity, it will be
	 * automatically reopened.
	 *
	 * @return the connection to the database
	 * @throws IOException if a connection could not be established
	 */
	public Connection getConnection() throws IOException {
		try {
			if (connection == null || connection.isClosed()) {
				return connection = DriverManager.getConnection(url, user, password);
			} else {
				return connection;
			}
		} catch (SQLException e) {
			throw new IOException(String.format("Could not establish a database connection on\n\t%s\n\tUser: %s\n\tPassword: %s", url, user, password), e);
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
	public static byte[] objectToBytes(Object o) {
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
	public static Object bytesToObject(byte[] bytes) {
		try (ObjectInputStream oIn = new ObjectInputStream(new ByteArrayInputStream(bytes))) {

			//Use an object input stream to read the object from the byte array
			return oIn.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		//Something went wrong, return null
		return null;
	}

	@Override
	public Product getProductByID(int id) throws IOException {
		return productPersistor.getProductByID(id);
	}

	@Override
	public Set<Product> getProductsByName(String name) throws IOException {
		return productPersistor.getProductsByName(name);
	}

	@Override
	public Set<Product> getProductsByCategory(String name) throws IOException {
		return productPersistor.getProductsByCategory(name);
	}

	@Override
	public Set<Product> getProductsByTag(String name) throws IOException {
		return productPersistor.getProductsByTag(name);
	}

	@Override
	public Set<ProductReview> getProductReviews() throws IOException {
		return productPersistor.getProductReviews();
	}

	@Override
	public Set<Product> getProducts() throws IOException {
		return productPersistor.getProducts();
	}

	@Override
	public void saveProduct(Product product) throws IOException {
		productPersistor.saveProduct(product);
	}

	@Override
	public void saveProducts(Collection<Product> products) throws IOException {
		productPersistor.saveProducts(products);
	}

	@Override
	public void deleteProduct(int id) throws IOException {
		productPersistor.deleteProduct(id);
	}

	@Override
	public Category getCategoryByName(String name) throws IOException {
		return categoryPersistor.getCategoryByName(name);
	}

	@Override
	public Set<Category> getCategories() throws IOException {
		return categoryPersistor.getCategories();
	}

	@Override
	public void saveCategory(Category category) throws IOException {
		categoryPersistor.saveCategory(category);
	}

	@Override
	public void saveCategories(Collection<Category> categories) throws IOException {
		categoryPersistor.saveCategories(categories);
	}

	@Override
	public void deleteCategory(String name) throws IOException {
		categoryPersistor.deleteCategory(name);
	}

	@Override
	public Attribute getAttributeByID(int id) throws IOException {
		return attributePersistor.getAttributeByID(id);
	}

	@Override
	public Set<Attribute> getAttributes() throws IOException {
		return attributePersistor.getAttributes();
	}

	@Override
	public void saveAttribute(Attribute attribute) throws IOException {
		attributePersistor.saveAttribute(attribute);
	}

	@Override
	public void saveAttributes(Collection<Attribute> attributes) throws IOException {
		attributePersistor.saveAttributes(attributes);
	}

	@Override
	public void deleteAttribute(int id) throws IOException {
		attributePersistor.deleteAttribute(id);
	}

	@Override
	public Tag getTag(String name) throws IOException {
		return tagPersistor.getTag(name);
	}

	@Override
	public Set<Tag> getTags() throws IOException {
		return tagPersistor.getTags();
	}

	@Override
	public void saveTag(Tag tag) throws IOException {
		tagPersistor.saveTag(tag);
	}

	@Override
	public void saveTags(Collection<Tag> tags) throws IOException {
		tagPersistor.saveTags(tags);
	}

	@Override
	public void deleteTag(String name) throws IOException {
		tagPersistor.deleteTag(name);
	}

	@Override
	public Set<Image> getImagesForProduct(int id) throws IOException {
		return productPersistor.getImagesForProduct(id);
	}

	@Override
	public Set<Image> getImages() throws IOException {
		return productPersistor.getImages();
	}

	@Override
	public void saveImage(Image image) throws IOException {
		productPersistor.saveImage(image);
	}

	@Override
	public void saveImages(Collection<Image> images) throws IOException {
		productPersistor.saveImages(images);
	}

	@Override
	public DataCache getCache() {
		return cache;
	}

	@Override
	public void setCache(DataCache cache) {
		this.cache = cache;
	}

	@Override
	public void dispose() {
		DBUtil.close(connection);
	}
}
