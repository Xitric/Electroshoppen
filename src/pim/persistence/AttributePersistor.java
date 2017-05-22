package pim.persistence;

import pim.business.Attribute;
import shared.DBUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Class used for performing operations on attributes in the database.
 *
 * @author Kasper
 */
class AttributePersistor {

	private PIMDatabaseFacade dbf;

	/**
	 * Constructs a new class for performing operations on attributes in the database.
	 *
	 * @param dbf the database facade
	 */
	public AttributePersistor(PIMDatabaseFacade dbf) {
		this.dbf = dbf;
	}

	public Attribute getAttributeByID(int id) throws IOException {
		Connection connection = dbf.getConnection();

		try (PreparedStatement getAttribute = connection.prepareStatement("SELECT * FROM attribute WHERE attributeid = ?");
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
			throw new IOException("Unable to read attribute with id " + id + "!", e);
		}
	}

	public Set<Attribute> getAttributes() throws IOException {
		Connection connection = dbf.getConnection();

		//Attempt to read data from database. Throw exception if something goes wrong
		try (PreparedStatement getAttributes = connection.prepareStatement("SELECT * FROM attribute;");
		     PreparedStatement getLegalValues = connection.prepareStatement("SELECT * FROM legalvalue;")) {

			ResultSet attributeData = getAttributes.executeQuery();
			ResultSet legalValueData = getLegalValues.executeQuery();

			return buildAttributes(attributeData, legalValueData);
		} catch (SQLException e) {
			throw new IOException("Unable to read attributes!", e);
		}
	}

	public void saveAttribute(Attribute attribute) throws IOException {
		saveAttributes(Collections.singleton(attribute));
	}

	public void saveAttributes(Collection<Attribute> attributes) throws IOException {
		Connection connection = dbf.getConnection();

		try (PreparedStatement storeAttributeData = connection.prepareStatement("INSERT INTO attribute VALUES (?, ?, ?) ON CONFLICT (attributeid) DO UPDATE SET name = EXCLUDED.name, defaultvalue = EXCLUDED.defaultvalue;");
		     PreparedStatement storeAttributeDataNew = connection.prepareStatement("INSERT INTO attribute VALUES (DEFAULT, ?, ?) RETURNING attributeid;");
		     PreparedStatement storeLegalValues = connection.prepareStatement("INSERT INTO legalvalue VALUES (?, ?) ON CONFLICT (attributeid, value) DO NOTHING;")) {

			//Turn of auto commit to ensure each attribute is saved fully
			connection.setAutoCommit(false);

			for (Attribute attribute : attributes) {
				//Store basic attribute data
				//If attribute has an invalid id, generate a new one
				if (attribute.hasValidID()) {
					storeAttributeData.setInt(1, attribute.getID());
					storeAttributeData.setString(2, attribute.getName());
					storeAttributeData.setObject(3, PIMDatabaseFacade.objectToBytes(attribute.getDefaultValue()));
					storeAttributeData.executeUpdate();
				} else {
					storeAttributeDataNew.setString(1, attribute.getName());
					storeAttributeDataNew.setObject(2, PIMDatabaseFacade.objectToBytes(attribute.getDefaultValue()));
					if (storeAttributeDataNew.execute()) {
						//Get generated id
						ResultSet result = storeAttributeDataNew.getResultSet();
						result.next();
						int id = result.getInt(1);
						attribute.setID(id); //Subsequent calls to attribute.getID() are now safe for use
					} else {
						//Nothing returned, so something must have gone wrong
						connection.rollback();
						throw new IOException("Unable to save attribute! No ID returned from database");
					}
				}

				//If the attribute already exists, the legal values should not be changed since they are immutable.
				//Should the immutability be violated, however, the issue lies elsewhere - not here. Thus the
				//possibility of adding new legal values has not been handled. Also, this would not break anything
				storeLegalValues.setInt(1, attribute.getID());
				if (attribute.getLegalValues() != null) {
					for (Object value : attribute.getLegalValues()) {
						storeLegalValues.setObject(2, PIMDatabaseFacade.objectToBytes(value));
						storeLegalValues.executeUpdate();
					}
				}

				//Commit per attribute. This might result in only some of the attributes being saved, but that should be
				//better than saving none of them
				connection.commit();
			}
		} catch (SQLException e) {
			throw new IOException("Unable to save all attributes! Some attributes might not be saved!", e);
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

	public void deleteAttribute(int id) throws IOException {
		Connection connection = dbf.getConnection();

		try (PreparedStatement deleteAttributeData = connection.prepareStatement("DELETE FROM attribute WHERE attributeid = ?")) {
			//Delete attribute entry. The constraints in the database should ensure that the deletion is cascaded
			deleteAttributeData.setInt(1, id);
			deleteAttributeData.executeUpdate();
		} catch (SQLException e) {
			throw new IOException("Unable to delete attribute with id " + id + "!", e);
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
			Object val = PIMDatabaseFacade.bytesToObject(legalValueData.getBytes(2));

			Set<Object> set = legalValues.getOrDefault(id, new HashSet<>());
			set.add(val);
			legalValues.put(id, set);
		}

		//Construct all attributes and return result
		while (attributeData.next()) {
			int id = attributeData.getInt(1);
			String name = attributeData.getString(2).trim();
			Object defaultValue = PIMDatabaseFacade.bytesToObject(attributeData.getBytes(3));

			//Create new/reuse attribute.
			attributes.add(dbf.getCache().createAttribute(id, name, defaultValue, legalValues.get(id)));
		}

		return attributes;
	}
}
