package pim.persistence;

import pim.business.Attribute;
import pim.business.Category;
import pim.business.Product;

import java.util.List;

/**
 * Created by Kasper on 06-04-2017.
 * <p>
 * Mediator used to access the underlying database. The mediator uses the singleton pattern, so calling the method
 * {@link #getInstance()} is necessary to acquire and instance.
 */
public class DatabaseMediator {

	/**
	 * The singleton instance for the database mediator.
	 */
	private static DatabaseMediator instance;

	/**
	 * Private constructor.
	 */
	private DatabaseMediator() {

	}

	/**
	 * Get the singleton instance for the database mediator. This instance is used to access the database of the PIM.
	 *
	 * @return the singleton instance
	 */
	public static DatabaseMediator getInstance() {
		if (instance == null) {
			instance = new DatabaseMediator();
		}

		return instance;
	}

	//hentProdukterPåKategori(navn), hentProduk(id), hentProdukt(navn), hentProdukterMedAttribut(navn), hentProdukterMedTag(navn), hentKategorier(), hentAttributter(kategori)
	public Product getProduct(int id) {
		throw new UnsupportedOperationException("Not yet supported");
	}

	public List<Product> getProductsByCategory(String categoryName) {
		throw new UnsupportedOperationException("Not yet supported");
	}

	public List<Product> getProductsByName(String productName) {
		throw new UnsupportedOperationException("Not yet supported");
	}

	public List<Product> getProductsByAttribute(String attributeName) {
		throw new UnsupportedOperationException("Not yet supported");
	}

	public List<Product> getProductsByTag(String tagName) {
		throw new UnsupportedOperationException("Not yet supported");
	}

	public List<Category> getCategories() {
		throw new UnsupportedOperationException("Not yet supported");
	}

	public List<Attribute> getAttributes() {
		throw new UnsupportedOperationException("Not yet supported");
	}
}
