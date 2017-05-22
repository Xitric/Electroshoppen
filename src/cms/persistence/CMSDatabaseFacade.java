package cms.persistence;

import cms.business.DynamicPage;
import cms.business.DynamicPageImpl;
import cms.business.Template;
import javafx.util.Pair;
import shared.DBUtil;

import javax.xml.transform.Result;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

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
		Connection connection = getConnection();

		try (PreparedStatement storePageData = connection.prepareStatement("INSERT INTO page VALUES (?) ON CONFLICT (pageid) DO NOTHING;");
		     PreparedStatement storePageDataNew = connection.prepareStatement("INSERT INTO page VALUES (DEFAULT) RETURNING pageid;");
		     PreparedStatement storePageTemplate = connection.prepareStatement("INSERT INTO pagelayout VALUES (?, ?) ON CONFLICT (pageid) DO UPDATE SET templateid = EXCLUDED.templateid");
		     PreparedStatement storePageContent = connection.prepareStatement("INSERT INTO content VALUES (?, ?, ?) ON CONFLICT (elementid, pageid) DO UPDATE SET html = EXCLUDED.html;")) {

			//Turn of auto commit to ensure the page is saved fully
			connection.setAutoCommit(false);

			//Store page id. If the page has an invalid id, generate a new one
			if (page.hasValidID()) {
				storePageData.setInt(1, page.getID());
				storePageData.executeUpdate();
			} else {
				if (storePageDataNew.execute()) {
					//Get generated id
					ResultSet result = storePageDataNew.getResultSet();
					result.next();
					int id = result.getInt(1);
					page.setID(id); //Subsequent calls to page.getID() are now safe for use
				} else {
					//Nothing returned, so something must have gone wrong
					connection.rollback();
					throw new IOException("Unable to save page! No ID returned from database");
				}
			}

			//Store page template
			storePageTemplate.setInt(1, page.getID());
			storePageTemplate.setInt(2, template.getID());
			storePageTemplate.executeUpdate();

			//Store page content
			storePageContent.setInt(2, page.getID());
			for (String contentID : template.getElementIDs()) {
				storePageContent.setString(1, contentID);
				storePageContent.setString(3, page.getContentForID(contentID).toString());
				storePageContent.executeUpdate();
			}

			//Commit changes
			connection.commit();

		} catch (SQLException e) {
			throw new IOException("Unable to save page!", e);
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

	@Override
	public void deletePage(int id) throws IOException {
		Connection connection = getConnection();

		try (PreparedStatement deletePageData = connection.prepareStatement("DELETE FROM page WHERE pageid = ?")) {
			//Delete page entry. The constraints in the database should ensure that the deletion is cascaded
			deletePageData.setInt(1, id);
			deletePageData.executeUpdate();
		} catch (SQLException e) {
			throw new IOException("Unable to delete page with id " + id + "!", e);
		}
	}

	public Map<Integer, String> getPageInfo() throws IOException{
		Connection connection = getConnection();
		HashMap<Integer, String> pageInformationMap = new HashMap<>();
		try(PreparedStatement pageInformation = connection.prepareStatement("SELECT * FROM page")){
			ResultSet rs = pageInformation.executeQuery();
			while(rs.next()){
				pageInformationMap.put(rs.getInt("pageid"), rs.getString("pagename"));
			}
		}catch(SQLException e){
			throw new IOException("Unable to retrive page information");
		}
		return pageInformationMap;

	}

	@Override
	public Set<Integer> getPageIDs() throws IOException {
		Connection connection = getConnection();
		Set<Integer> pageIDs = new HashSet<>();

		try(PreparedStatement PageIDs = connection.prepareStatement("SELECT pageid FROM page")) {

			ResultSet rs = PageIDs.executeQuery();

			while (rs.next()) {
				int id = rs.getInt(1);
				pageIDs.add(id);
			}

		} catch(SQLException e) {
			throw new IOException("Unable to retrieve page IDs from database", e);
		}
		return pageIDs;
	}

	@Override
	public Set<String> getPageNames() throws IOException {
		Connection connection = getConnection();
		Set<String> pageNamesSet = new HashSet<>();

		try(PreparedStatement pageNames = connection.prepareStatement("SELECT pagename FROM page")) {

			ResultSet rs = pageNames.executeQuery();
			String name = null;

			while (rs.next()) {
				name = rs.getString(1);
			}

		} catch(SQLException e) {
			throw new IOException("Unable to retrieve page names from database", e);
		}
		return pageNamesSet;
	}

	@Override
	public void dispose() {
		DBUtil.close(conn);
	}
}
