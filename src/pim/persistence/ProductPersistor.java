package pim.persistence;

import org.postgresql.largeobject.LargeObjectManager;
import pim.business.*;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Class used for performing operations on products in the database. Images are also handled by this class, as they are
 * so closely linked to products.
 *
 * @author Kasper
 * @author Mikkel
 */
class ProductPersistor {

	private DatabaseFacade dbf;

	/**
	 * Constructs a new class for performing operations on products in the database.
	 *
	 * @param dbf the database facade
	 */
	public ProductPersistor(DatabaseFacade dbf) {
		this.dbf = dbf;
	}

	public Product getProductByID(int id) throws IOException {
		Connection connection = dbf.getConnection();
		try (PreparedStatement getProduct = connection.prepareStatement("SELECT * FROM product WHERE productid = ?;");
		     PreparedStatement getProductCategories = connection.prepareStatement("SELECT * FROM productcategory WHERE productid = ?;");
		     PreparedStatement getProductValues = connection.prepareStatement("SELECT * FROM attributevalue WHERE productid = ?;");
		     PreparedStatement getProductTags = connection.prepareStatement("SELECT * FROM producttag WHERE productid = ?")) {

			getProduct.setInt(1, id);
			ResultSet productData = getProduct.executeQuery();

			getProductCategories.setInt(1, id);
			ResultSet productCategoryData = getProductCategories.executeQuery();

			getProductValues.setInt(1, id);
			ResultSet productValueData = getProductValues.executeQuery();

			getProductTags.setInt(1, id);
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
			throw new IOException("Unable to read product with id " + id + "!", e);
		}
	}

	public Set<Product> getProductsByName(String name) throws IOException {
		Connection connection = dbf.getConnection();

		try (PreparedStatement getProducts = connection.prepareStatement("SELECT * FROM product WHERE name = ?;");
		     PreparedStatement getProductCategories = connection.prepareStatement("SELECT productid, categoryName FROM productcategory NATURAL JOIN product WHERE name = ?;");
		     PreparedStatement getProductValues = connection.prepareStatement("SELECT attributeid, productid, value FROM attributevalue NATURAL JOIN product WHERE name = ?;");
		     PreparedStatement getProductTags = connection.prepareStatement("SELECT tagname, productid FROM producttag NATURAL JOIN product WHERE name = ?;")) {

			return runStringQueries(name, getProducts, getProductCategories, getProductValues, getProductTags);
		} catch (SQLException e) {
			throw new IOException("Unable to read products with name " + name + "!", e);
		}
	}

	public Set<Product> getProductsByCategory(String name) throws IOException {
		Connection connection = dbf.getConnection();

		try (PreparedStatement getProducts = connection.prepareStatement("SELECT * FROM product WHERE productid IN (SELECT productid FROM productcategory WHERE categoryname = ?);");
		     PreparedStatement getProductCategories = connection.prepareStatement("SELECT productid, categoryName FROM productcategory NATURAL JOIN product WHERE productid IN (SELECT productid FROM productcategory WHERE categoryname = ?);");
		     PreparedStatement getProductValues = connection.prepareStatement("SELECT attributeid, productid, value FROM attributevalue NATURAL JOIN product WHERE productid IN (SELECT productid FROM productcategory WHERE categoryname = ?);");
		     PreparedStatement getProductTags = connection.prepareStatement("SELECT tagname, productid FROM producttag NATURAL JOIN product WHERE productid IN (SELECT productid FROM productcategory WHERE categoryname = ?);")) {

			return runStringQueries(name, getProducts, getProductCategories, getProductValues, getProductTags);
		} catch (SQLException e) {
			throw new IOException("Unable to read products from category with name " + name + "!", e);
		}
	}

	public Set<Product> getProductsByTag(String name) throws IOException {
		Connection connection = dbf.getConnection();

		try (PreparedStatement getProducts = connection.prepareStatement("SELECT * FROM product WHERE productid IN (SELECT productid FROM producttag WHERE tagname = ?);");
		     PreparedStatement getProductCategories = connection.prepareStatement("SELECT productid, categoryName FROM productcategory NATURAL JOIN product WHERE productid IN (SELECT productid FROM producttag WHERE tagname = ?);");
		     PreparedStatement getProductValues = connection.prepareStatement("SELECT attributeid, productid, value FROM attributevalue NATURAL JOIN product WHERE productid IN (SELECT productid FROM producttag WHERE tagname = ?);");
		     PreparedStatement getProductTags = connection.prepareStatement("SELECT tagname, productid FROM producttag NATURAL JOIN product WHERE productid IN (SELECT productid FROM producttag WHERE tagname = ?);")) {

			return runStringQueries(name, getProducts, getProductCategories, getProductValues, getProductTags);
		} catch (SQLException e) {
			throw new IOException("Unable to read products with tag " + name + "!", e);
		}
	}

	/**
	 * Utility method for the three methods {@link #getProductsByName(String)}, {@link #getProductsByCategory(String)}
	 * and {@link #getProductsByTag(String)}.
	 *
	 * @param string               the string to insert into the prepared statements on index 1
	 * @param getProducts          the prepared statement for retrieving product data
	 * @param getProductCategories the prepared statement for retrieving product categories
	 * @param getProductValues     the prepared statement for retrieving attribute values
	 * @param getProductTags       the prepared statement for retrieving tags
	 * @return the resulting set of products
	 * @throws SQLException if something goes wrong
	 */
	private Set<Product> runStringQueries(String string, PreparedStatement getProducts, PreparedStatement getProductCategories, PreparedStatement getProductValues, PreparedStatement getProductTags) throws SQLException, IOException {
		getProducts.setString(1, string);
		ResultSet productData = getProducts.executeQuery();

		getProductCategories.setString(1, string);
		ResultSet productCategoryData = getProductCategories.executeQuery();

		getProductValues.setString(1, string);
		ResultSet productValueData = getProductValues.executeQuery();

		getProductTags.setString(1, string);
		ResultSet productTagsData = getProductTags.executeQuery();

		return buildProducts(productData, productCategoryData, productValueData, productTagsData);
	}

	public Set<Product> getProducts() throws IOException {
		Connection connection = dbf.getConnection();

		try (PreparedStatement getProducts = connection.prepareStatement("SELECT * FROM product;");
		     PreparedStatement getProductCategories = connection.prepareStatement("SELECT * FROM productcategory;");
		     PreparedStatement getProductValues = connection.prepareStatement("SELECT * FROM attributevalue;");
		     PreparedStatement getProductTags = connection.prepareStatement("SELECT * FROM producttag;")) {

			ResultSet productData = getProducts.executeQuery();
			ResultSet productCategoryData = getProductCategories.executeQuery();
			ResultSet productValueData = getProductValues.executeQuery();
			ResultSet productTags = getProductTags.executeQuery();

			Set<Product> products = buildProducts(productData, productCategoryData, productValueData, productTags);

			// Close statements
			DBUtil.close(getProducts);
			DBUtil.close(productCategoryData);
			DBUtil.close(productValueData);
			DBUtil.close(productTags);
			return products;
		} catch (SQLException e) {
			throw new IOException("Unable to read products!", e);
		}
	}

	public void saveProduct(Product product) throws IOException {
		saveProducts(Collections.singleton(product));
	}

	public void saveProducts(Collection<Product> products) throws IOException {
		Connection connection = dbf.getConnection();

		try (PreparedStatement storeProductData = connection.prepareStatement("INSERT INTO product VALUES (?, ?, ?, ?) ON CONFLICT (productid) DO UPDATE SET name = Excluded.name, price = EXCLUDED.price, description = Excluded.description;");
		     PreparedStatement storeProductDataNew = connection.prepareStatement("INSERT INTO product VALUES (DEFAULT, ?, ?, ?) RETURNING productid;");
		     PreparedStatement removeProductCategories = connection.prepareStatement("DELETE FROM productcategory WHERE productid = ?;");
		     PreparedStatement addProductCategory = connection.prepareStatement("INSERT INTO productcategory VALUES(?, ?);");
		     PreparedStatement addAttributeValue = connection.prepareStatement("INSERT INTO attributevalue VALUES(?, ?, ?) ON CONFLICT (attributeid, productid) DO UPDATE SET value = Excluded.value;");
		     PreparedStatement removeProductTags = connection.prepareStatement("DELETE FROM producttag WHERE productid = ?");
		     PreparedStatement saveProductTags = connection.prepareStatement("INSERT INTO producttag VALUES(?, ?)");
		     PreparedStatement removeProductImages = connection.prepareStatement("DELETE FROM productimage WHERE productid = ?");
		     PreparedStatement saveProductImages = connection.prepareStatement("INSERT INTO productimage VALUES(?, ?)")) {

			//Turn of auto commit to ensure each product is saved fully
			connection.setAutoCommit(false);

			for (Product product : products) {
				//Store basic product data
				//If product has an invalid id, generate a new one
				if (product.hasValidID()) {
					storeProductData.setInt(1, product.getID());
					storeProductData.setString(2, product.getName());
					storeProductData.setDouble(3, product.getPrice());
					storeProductData.setString(4, product.getDescription());
					storeProductData.executeUpdate();
				} else {
					storeProductDataNew.setString(1, product.getName());
					storeProductDataNew.setDouble(2, product.getPrice());
					storeProductData.setString(3, product.getDescription());
					if (storeProductData.execute()) {
						//Get generated id
						ResultSet result = storeProductDataNew.getResultSet();
						result.next();
						int id = result.getInt(1);
						product.setID(id); //Subsequent calls to product.getID() are now safe for use
					} else {
						//Nothing returned, so something must have gone wrong
						connection.rollback();
						throw new IOException("Unable to save product! No ID returned from database");
					}
				}

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
					addAttributeValue.setObject(3, DatabaseFacade.objectToBytes(value.getValue()));
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
					saveProductImages.setInt(1, image.getID());
					saveProductImages.executeUpdate();
				}

				//Commit per product. This might result in only some of the products being saved, but that should be
				//better than saving none of them
				connection.commit();
			}
		} catch (SQLException e) {
			throw new IOException("Unable to save all products! Some products might not be saved!", e);
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();

				//We should close the connection to ensure that it causes no more harm
				DBUtil.close(connection);
			}
		}
	}

	public void deleteProduct(int id) throws IOException {
		Connection connection = dbf.getConnection();

		try (PreparedStatement deleteProductData = connection.prepareStatement("DELETE FROM product WHERE productid = ?")) {

			//Delete product entry. The constraints in the database should ensure that the deletion is cascaded
			deleteProductData.setInt(1, id);
			deleteProductData.executeUpdate();
		} catch (SQLException e) {
			throw new IOException("Unable to delete product with id " + id + "!", e);
		}
	}

	public Set<Image> getImagesForProduct(int id) throws IOException {
		Connection connection = dbf.getConnection();

		try (PreparedStatement getImages = connection.prepareStatement("SELECT imageid, imagedata FROM image NATURAL JOIN productimage WHERE productid = ?;")) {

			getImages.setInt(1, id);
			ResultSet imageData = getImages.executeQuery();

			return buildImages(imageData);
		} catch (SQLException e) {
			throw new IOException("Unable to read images for product with id " + id + "!", e);
		}
	}

	public Set<Image> getImages() throws IOException {
		Connection connection = dbf.getConnection();

		try (PreparedStatement getImages = connection.prepareStatement("SELECT * FROM image")) {

			ResultSet imageData = getImages.executeQuery();

			return buildImages(imageData);
		} catch (SQLException e) {
			throw new IOException("Unable to read images!", e);
		}
	}

	public void saveImage(Image image) throws IOException {
		saveImages(Collections.singleton(image));
	}

	public void saveImages(Collection<Image> images) throws IOException {
		Connection connection = dbf.getConnection();

		try (PreparedStatement storeImageData = connection.prepareStatement("INSERT INTO image VALUES (?, ?) ON CONFLICT (imageid) DO UPDATE SET imagedata = EXCLUDED.imagedata;");
		     PreparedStatement storeImageDataNew = connection.prepareStatement("INSERT INTO image VALUES (DEFAULT, ?) RETURNING imageid;")) {

			for (Image image: images) {
				//Store image data
				//If the image has an invalid id, generate a new one
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(image.getImage(), "png", baos);

				if (image.hasValidID()) {
					storeImageData.setInt(1, image.getID());
					storeImageData.setObject(2, baos.toByteArray());
					storeImageData.executeUpdate();
				} else {
					storeImageDataNew.setObject(1, baos.toByteArray());
					if (storeImageDataNew.execute()) {
						//Get generated id
						ResultSet result = storeImageDataNew.getResultSet();
						result.next();
						int id = result.getInt(1);
						image.setID(id); //Subsequent calls to image.getID() are now safe for use
					} else {
						//Nothing returned, so something must have gone wrong
						connection.rollback();
						throw new IOException("Unable to save image! No ID returned from database");
					}
				}
			}
		} catch (SQLException e) {
			throw new IOException("Unable to save all images! Some images might not be saved!", e);
		}
	}

	/**
	 * Build a set of products from the specified data.
	 *
	 * @param productData         the data describing product ids, names and prices
	 * @param productCategoryData the data describing product categories
	 * @param productValueData    the data describing attribute values on products
	 * @param productTags         the data describing product tags
	 * @return a set of all products that could be built from the data
	 * @throws SQLException if something goes wrong
	 */
	private Set<Product> buildProducts(ResultSet productData, ResultSet productCategoryData, ResultSet productValueData, ResultSet productTags) throws SQLException, IOException {
		Map<Integer, Product> products = new HashMap<>();

		//Construct all products
		while (productData.next()) {
			int id = productData.getInt(1);
			String name = productData.getString(2).trim();
			double price = productData.getDouble(3);
			String description = productData.getString(4);

			products.put(id, dbf.getCache().createProduct(id, name, description, price));
		}
		productData.close();

		//Add all product categories
		while (productCategoryData.next()) {
			int productID = productCategoryData.getInt(1);
			String categoryName = productCategoryData.getString(2).trim();

			//noinspection EmptyCatchBlock
			try {
				Category category = dbf.getCategoryByName(categoryName);
				products.get(productID).addCategory(category);
			} catch (IOException e) {
			} //The database should guarantee that this exception never occurs
		}
		productCategoryData.close();

		//Set all attribute values
		while (productValueData.next()) {
			int attributeID = productValueData.getInt(1);
			int productID = productValueData.getInt(2);
			Object value = DatabaseFacade.bytesToObject(productValueData.getBytes(3));

			//noinspection EmptyCatchBlock
			try {
				Attribute attribute = dbf.getAttributeByID(attributeID);
				products.get(productID).setAttribute(attribute, value);
			} catch (IOException e) {
			} //The database should guarantee that this exception never occurs
		}
		productValueData.close();

		//Add all tags
		while (productTags.next()) {
			String name = productTags.getString(1).trim();
			Tag t = dbf.getCache().createTag(name);
			int productID = productTags.getInt(2);
			products.get(productID).addTag(t);
		}

		// access to large object manager (used for images)
		LargeObjectManager lobj = dbf.getConnection().unwrap(org.postgresql.PGConnection.class).getLargeObjectAPI();

		//Add all images
		for (Map.Entry<Integer, Product> productEntry : products.entrySet()) {
			productEntry.getValue().setImages(getImagesForProduct(productEntry.getKey()));
		}

		//Return set of products
		return new HashSet<>(products.values());
	}

	/**
	 * Build a set of images from a result set.
	 *
	 * @param imageData the data describing the images
	 * @return the set of images
	 * @throws SQLException if something goes wrong
	 */
	private Set<Image> buildImages(ResultSet imageData) throws SQLException, IOException {
		Set<Image> images = new HashSet<>();

		while (imageData.next()) {
			int imageID = imageData.getInt(1);
			ByteArrayInputStream bais = new ByteArrayInputStream(imageData.getBytes(2));
			Image image = dbf.getCache().createImage(imageID, ImageIO.read(bais));
			images.add(image);
		}

		DBUtil.close(imageData);

		return images;
	}
}
