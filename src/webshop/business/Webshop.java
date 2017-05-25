package webshop.business;

import pim.business.Product;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Interface describing the functionality that must be provided by all webshop implementations.
 *
 * @author Emil
 * @author Kasper
 */
public interface Webshop {

	/**
	 * Get the landing page for the web shop.
	 *
	 * @return the html of the landing page
	 * @throws IOException if the operation fails
	 */
	String getLandingPage() throws IOException;

	/**
	 * Get the page with the specified id.
	 *
	 * @param id the id of the page
	 * @return the html of the page with the specified id
	 * @throws IOException if the operation fails
	 */
	String getPage(int id) throws IOException;

	/**
	 * Get the page for the product with the specified id.
	 *
	 * @param id the id of the product
	 * @return the html of the page for the product with the specified id
	 * @throws IOException if the operation fails
	 */
	String getProductPage(int id) throws IOException;

	/**
	 * Getting IDs and Names of all guide pages
	 *
	 * @return Returns a Map where ID is the key and Name of a page is the Value
	 * @throws IOException if the operation failed
	 */
	Map<Integer, String> getGuidePages() throws IOException;

	/**
	 * Getting IDs and Names of all article pages
	 *
	 * @return Returns a Map where ID is the key and Name of a page is the Value
	 * @throws IOException if the operation failed
	 */
	Map<Integer, String> getArticlePages() throws IOException;

	/**
	 * Get a list of all products that should be sold in this web shop.
	 *
	 * @return a list of all products currently being sold
	 * @throws IOException if the operation fails
	 */
	List<Product> getAllProducts() throws IOException;
}
