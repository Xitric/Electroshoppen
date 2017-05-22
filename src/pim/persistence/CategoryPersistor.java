package pim.persistence;

import pim.business.Attribute;
import pim.business.Category;
import shared.DBUtil;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class used for performing operations on categories in the database.
 *
 * @author Kasper
 */
class CategoryPersistor {

	private PIMDatabaseFacade dbf;

	/**
	 * Constructs a new class for performing operations on categories in the database.
	 *
	 * @param dbf the database facade
	 */
	public CategoryPersistor(PIMDatabaseFacade dbf) {
		this.dbf = dbf;
	}

	public Category getCategoryByName(String name) throws IOException {
		Connection connection = dbf.getConnection();

		try (PreparedStatement getCategory = connection.prepareStatement("SELECT * FROM category WHERE categoryname = ?;");
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
			throw new IOException("Unable to read category with name " + name + "!", e);
		}
	}

	public Set<Category> getCategories() throws IOException {
		Connection connection = dbf.getConnection();

		try (PreparedStatement getCategories = connection.prepareStatement("SELECT * FROM category;");
		     PreparedStatement getAttributes = connection.prepareStatement("SELECT * FROM categoryattribute;")) {

			ResultSet categoryData = getCategories.executeQuery();
			ResultSet categoryAttributeData = getAttributes.executeQuery();

			return buildCategories(categoryData, categoryAttributeData);
		} catch (SQLException e) {
			throw new IOException("Unable to read categories!", e);
		}
	}

	public void saveCategory(Category category) throws IOException {
		saveCategories(Collections.singleton(category));
	}

	public void saveCategories(Collection<Category> categories) throws IOException {
		Connection connection = dbf.getConnection();

		try (PreparedStatement storeCategoryData = connection.prepareStatement("INSERT INTO category VALUES (?) ON CONFLICT (categoryname) DO NOTHING;");
		     PreparedStatement deleteRemovedAttributes = connection.prepareStatement("DELETE FROM categoryattribute WHERE categoryname = ?  AND NOT (attributeid = ANY(?));");
		     PreparedStatement addNewAttributes = connection.prepareStatement("INSERT INTO categoryattribute VALUES (?, ?) ON CONFLICT (categoryname, attributeid) DO NOTHING")) {

			//Turn of auto commit to ensure each category is saved fully
			connection.setAutoCommit(false);

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

				//Commit per category. This might result in only some of the categories being saved, but that should be
				//better than saving none of them
				connection.commit();
			}
		} catch (SQLException e) {
			throw new IOException("Unable to save all categories! Some categories might not be saved!", e);
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

	public void deleteCategory(String name) throws IOException {
		Connection connection = dbf.getConnection();

		try (PreparedStatement deleteCategoryData = connection.prepareStatement("DELETE FROM category WHERE categoryname = ?")) {

			//Delete category entry. The constraints in the database should ensure that the deletion is cascaded
			deleteCategoryData.setString(1, name);
			deleteCategoryData.executeUpdate();
		} catch (SQLException e) {
			throw new IOException("Unable to delete category with name " + name + "!", e);
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

			//noinspection EmptyCatchBlock
			try {
				set.add(dbf.getAttributeByID(attributeID));
			} catch (IOException e) {
			} //The database should guarantee that this exception never occurs

			categoryAttributes.put(categoryName, set);
		}

		//Construct all categories and return result
		while (categoryData.next()) {
			String categoryName = categoryData.getString(1).trim();

			//Create new/reuse category
			categories.add(dbf.getCache().createCategory(categoryName, categoryAttributes.getOrDefault(categoryName, new HashSet<>())));
		}

		return categories;
	}
}
