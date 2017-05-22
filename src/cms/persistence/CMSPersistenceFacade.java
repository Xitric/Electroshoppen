package cms.persistence;

import cms.business.DynamicPage;
import cms.business.Template;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Interface describing a class that can be used for accessing the persistence layer.
 *
 * @author Kasper
 */
public interface CMSPersistenceFacade {

	/**
	 * Get the template with the specified id.
	 *
	 * @param id the id of the template
	 * @return the template with the specified id
	 * @throws IOException if the operation fails
	 */
	Template getTemplate(int id) throws IOException;

	/**
	 * Get the templates of the specified type.
	 *
	 * @param type the type of the templates to get
	 * @return the templates of the specified type
	 * @throws IOException if the operation fails
	 */
	Set<Template> getTemplates(String type) throws IOException;

	/**
	 * Get the template for the page with the specified id.
	 *
	 * @param pageid the id of the page
	 * @return the template for the page with the specified id
	 * @throws IOException if the operation fails
	 */
	Template getTemplateForPage(int pageid) throws IOException;

	/**
	 * Get the page with the specified id.
	 *
	 * @param id the id of the page to get
	 * @return the page with the specified id
	 * @throws IOException if the operation fails
	 */
	DynamicPage getPage(int id) throws IOException;

	/**
	 * Save the specified page. If the page has no id, a new one will be generated.
	 *
	 * @param page     the page to save
	 * @param template the template associated with the page
	 * @throws IOException if the operation fails
	 */
	void savePage(DynamicPage page, Template template) throws IOException;

	/**
	 * Delete the page with the specified id.
	 *
	 * @param id the id of the page
	 * @throws IOException if the operation fails
	 */
	void deletePage(int id) throws IOException;

	/**
	 * Used to retrieve all page IDs and Names of the pages in the CMS database.
	 *
	 * @return all page ids and names
	 * @throws IOException if the operation fails
	 */
	Map<Integer, String> getPageInfo() throws IOException;

	/**
	 * Dispose all resources used by this persistence facade. This includes closing all currently open connections.
	 */
	void dispose();
}
