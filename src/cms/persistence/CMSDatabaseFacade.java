package cms.persistence;

import cms.business.DynamicPage;
import cms.business.DynamicPageImpl;
import cms.business.Template;
import cms.business.XMLElement;
import shared.DBUtil;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.postgresql.jdbc.EscapedFunctions.INSERT;

/**
 * Implementation of the CMSPersistenceFacade interface for use with JDBC.
 *
 * @author Kasper
 */
class CMSDatabaseFacade implements CMSPersistenceFacade {

	/* Variables for database connection */
	private final static String url = "jdbc:postgresql://46.101.142.251:5432/cms";
	//	private final static String url = "jdbc:postgresql://localhost/NAME_HERE"; Use this if a local database is desired
	private final static String user = "postgres";
	private final static String password = "1234";

	/**
	 * The database connection.
	 */
	private Connection conn;

	/**
	 * Constructs a new persistence facade for use with JDBC.
	 */
	public CMSDatabaseFacade() {

	}

	//TODO: Should we use a shared method?

	/**
	 * Get the connection to the database. If the connection has been closed because of inactivity, it will be
	 * automatically reopened.
	 *
	 * @return the connection to the database
	 * @throws IOException if a connection could not be established
	 */
	@SuppressWarnings("Duplicates")
	public Connection getConnection() throws IOException {
		try {
			if (conn == null || conn.isClosed()) {
				return conn = DriverManager.getConnection(url, user, password);
			} else {
				return conn;
			}
		} catch (SQLException e) {
			throw new IOException(String.format("Could not establish a database connection on\n\t%s\n\tUser: %s\n\tPassword: %s", url, user, password), e);
		}
	}

	@Override
	public Template getTemplate(int id) throws IOException {
		Connection connection = getConnection();
		try (PreparedStatement getTemplate = connection.prepareStatement("SELECT * FROM template WHERE templateid = ?;")) {

			getTemplate.setInt(1, id);
			ResultSet templateData = getTemplate.executeQuery();

			Set<Template> result = buildTemplates(templateData);

			//If the set is empty, no template with the specified id was found. Otherwise, the set should contain only
			//one value, that we return
			if (result.size() == 0) {
				return null;
			} else {
				return result.toArray(new Template[0])[0];
			}
		} catch (SQLException e) {
			throw new IOException("Unable to read template with id " + id + "!", e);
		}
	}

	@Override
	public Set<Template> getTemplates(String type) throws IOException {
		Connection connection = getConnection();
		try (PreparedStatement getTemplate = connection.prepareStatement("SELECT * FROM template WHERE type = ?;")) {

			getTemplate.setString(1, type);
			ResultSet templateData = getTemplate.executeQuery();

			return buildTemplates(templateData);
		} catch (SQLException e) {
			throw new IOException("Unable to read templates with the type " + type + "!", e);
		}
	}

	@Override
	public Template getTemplateForPage(int pageid) throws IOException {
		Connection connection = getConnection();
		try (PreparedStatement getTemplate = connection.prepareStatement("SELECT templateid, type, layout FROM template NATURAL JOIN pagelayout WHERE pageid = ?;")) {

			getTemplate.setInt(1, pageid);
			ResultSet templateData = getTemplate.executeQuery();

			Set<Template> result = buildTemplates(templateData);

			//If the set is empty, no template for the page with the specified id was found. Otherwise, the set should
			//contain only one value, that we return
			if (result.size() == 0) {
				return null;
			} else {
				return result.toArray(new Template[0])[0];
			}
		} catch (SQLException e) {
			throw new IOException("Unable to read template for the page with id " + pageid + "!", e);
		}
	}

	/**
	 * Build a set of templates from the specified data.
	 *
	 * @param templateData the data describing template ids, types, and layouts
	 * @return a set of all templates that could be built from the data
	 * @throws SQLException if something goes wrong
	 * @throws IOException  if something goes wrong
	 */
	public Set<Template> buildTemplates(ResultSet templateData) throws SQLException, IOException {
		Set<Template> templates = new HashSet<>();

		//Construct all templates
		while (templateData.next()) {
			int id = templateData.getInt(1);
			String type = templateData.getString(2).trim();
			String layout = templateData.getString(3);

			templates.add(new Template(id, type, layout));
		}

		return templates;
	}

	@Override
	public DynamicPage getPage(int id) throws IOException {
		Connection connection = getConnection();
		try (PreparedStatement getPage = connection.prepareStatement("SELECT * FROM page WHERE pageid = ?;");
		     PreparedStatement getPageContent = connection.prepareStatement("SELECT * FROM content WHERE pageid = ?")) {

			getPage.setInt(1, id);
			ResultSet pageData = getPage.executeQuery();

			getPageContent.setInt(1, id);
			ResultSet pageContent = getPageContent.executeQuery();

			//We expect that the result set contains at most one element, but we need to be sure that there exists a
			//page with the specified id. Simply checking for content on the specified id is not enough, as a page can
			//exist with no content.
			int pageid = -1;
			while (pageData.next()) {
				pageid = pageData.getInt(1);
			}

			//If no page was read, return null
			if (pageid == -1) return null;

			//Otherwise get the page content
			Map<String, String> content = new HashMap<>();
			while (pageContent.next()) {
				String elementID = pageContent.getString(1).trim();
				String html = pageContent.getString(3);
				content.put(elementID, html);
			}

			return new DynamicPageImpl(pageid, content);

		} catch (SQLException e) {
			throw new IOException("Unable to read template with id " + id + "!", e);
		}
	}

	@Override
	public void savePage(DynamicPage page, Template template) throws IOException {
		//TODO Add ON CONFLICT conditions to the SQL statements?

		int pageID = page.getID();
		int templateID = template.getID();
		XMLElement content = page.getContentForID(String.valueOf(pageID));
		String elementID = content.getID();
		Connection connection = getConnection();

		try (PreparedStatement saveContent = connection.prepareStatement("INSERT INTO content(elementid, pageid, html) VALUES (?, ?, ?)");
				PreparedStatement savePageID = connection.prepareStatement("INSERT INTO page(pageid) VALUES (?)");
				PreparedStatement saveTemplate = connection.prepareStatement("INSERT INTO template (templateid) VALUES (?)");
				PreparedStatement savePageLayout = connection.prepareStatement("INSERT INTO pagelayout (pageid, templateid) VALUES (?, ?)")){

			if (page.hasValidID() && template.hasValidID()) {
				saveContent.setString(1, elementID);
				saveContent.setInt(2, pageID);
				saveContent.setString(3, String.valueOf(content));
				savePageID.setInt(1, pageID);
				saveTemplate.setInt(1, templateID);
				savePageLayout.setInt(1, pageID);
				savePageLayout.setInt(2, templateID);
			}
			else {
				throw new IOException("page or template has invalid IDs!");
			}

		} catch (SQLException e) {
			throw new IOException("Could not save page!", e);
		}
	}

	@Override
	public void dispose() {
		DBUtil.close(conn);
	}
}
