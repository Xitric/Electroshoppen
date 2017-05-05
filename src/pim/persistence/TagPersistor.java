package pim.persistence;

import pim.business.Tag;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Class used for performing operations on tags in the database.
 *
 * @author Kasper
 */
class TagPersistor {

	private DatabaseFacade dbf;

	/**
	 * Constructs a new class for performing operations on tags in the database.
	 *
	 * @param dbf the database facade
	 */
	public TagPersistor(DatabaseFacade dbf) {
		this.dbf = dbf;
	}

	public Tag getTag(String name) throws IOException {
		Connection connection = dbf.getConnection();

		try (PreparedStatement tagData = connection.prepareStatement("SELECT * FROM tag WHERE tagname = ?")) {
			tagData.setString(1, name);
			ResultSet tagResults = tagData.executeQuery();
			Set<Tag> result = buildTags(tagResults);

			//If the set is empty, no tag with the specified name was found. Otherwise, the set should contain only
			//one value, that we return
			if (result.size() == 0) {
				return null;
			} else {
				return result.toArray(new Tag[0])[0];
			}
		} catch (SQLException e) {
			throw new IOException("Unable to read tag with name " + name + "!", e);
		}
	}

	public Set<Tag> getTags() throws IOException {
		Connection connection = dbf.getConnection();

		try (PreparedStatement tagData = connection.prepareStatement("SELECT * FROM tag")) {
			ResultSet tagResults = tagData.executeQuery();
			return buildTags(tagResults);
		} catch (SQLException e) {
			throw new IOException("Unable to read tags!", e);
		}
	}

	public void saveTag(Tag tag) throws IOException {
		saveTags(Collections.singleton(tag));
	}

	public void saveTags(Collection<Tag> tags) throws IOException {
		Connection connection = dbf.getConnection();

		try (PreparedStatement tagData = connection.prepareStatement("INSERT INTO tag VALUES (?) ON CONFLICT (tagname) DO NOTHING;")) {
			for (Tag tag : tags) {
				tagData.setString(1, tag.getName());
				tagData.executeUpdate();
			}
		} catch (SQLException e) {
			throw new IOException("Unable to save all tags! Some tags might not be saved!", e);
		}
	}

	public void deleteTag(String name) throws IOException {
		Connection connection = dbf.getConnection();

		try (PreparedStatement delete = connection.prepareStatement("DELETE FROM tag WHERE tagname = ?")) {
			//Deletion should be cascaded
			delete.setString(1, name);
			delete.executeUpdate();
		} catch (SQLException e) {
			throw new IOException("Unable to delete tag " + name + "!", e);
		}
	}

	/**
	 * Build a set of tags from a result set.
	 *
	 * @param tagData the result set of data
	 * @return the set of tags created
	 * @throws SQLException if something goes wrong
	 */
	private Set<Tag> buildTags(ResultSet tagData) throws SQLException {
		Set<Tag> tags = new HashSet<>();

		while (tagData.next()) {
			tags.add(dbf.getCache().createTag(tagData.getString(1).trim()));
		}

		return tags;
	}
}
