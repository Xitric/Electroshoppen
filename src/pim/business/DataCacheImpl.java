package pim.business;

import pim.persistence.DataCache;

import java.awt.image.BufferedImage;
import java.util.Set;

/**
 * A business implementation of the {@link DataCache} interface. This implementation will ensure
 * coordination with the business and persistence layers of the PIM, without making the persistence layer dependent on
 * the domain layer.
 *
 * @author Kasper
 */
class DataCacheImpl implements DataCache {

	private final ProductManager productManager;
	private final AttributeManager attributeManager;
	private final CategoryManager categoryManager;
	private final TagManager tagManager;

	/**
	 * Constructs a new data buffer.
	 *
	 * @param productManager   the product manager to delegate calls to
	 * @param attributeManager the attribute manager to delegate calls to
	 * @param categoryManager  the category manager to delegate calls to
	 * @param tagManager       the tag manager to delegate calls to
	 */
	public DataCacheImpl(ProductManager productManager, AttributeManager attributeManager, CategoryManager categoryManager, TagManager tagManager) {
		this.productManager = productManager;
		this.attributeManager = attributeManager;
		this.categoryManager = categoryManager;
		this.tagManager = tagManager;
	}

	@Override
	public Product createProduct(int id, String name, String description, double price) {
		return productManager.constructProduct(id, name, description, price);
	}

	@Override
	public Attribute createAttribute(int id, String name, Object defaultValue) {
		return attributeManager.constructAttribute(id, name, defaultValue);
	}

	@Override
	public Attribute createAttribute(int id, String name, Object defaultValue, Set<Object> legalValues) {
		return attributeManager.constructAttribute(id, name, defaultValue, legalValues);
	}

	@Override
	public Category createCategory(String name) {
		return categoryManager.constructCategory(name);
	}

	@Override
	public Category createCategory(String name, Set<Attribute> attributes) {
		return categoryManager.constructCategory(name, attributes);
	}

	@Override
	public Tag createTag(String name) {
		return tagManager.createTag(name);
	}

	@Override
	public Image createImage(int id, BufferedImage img) {
		return productManager.constructImage(id, img);
	}
}
