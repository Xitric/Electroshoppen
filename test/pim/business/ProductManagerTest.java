package pim.business;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pim.persistence.PersistenceFacade;
import pim.persistence.PersistenceFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Test class for {@link Product}. This class tests the following methods:
 * <ul>
 * <li>{@link ProductManager#getProduct(int)}</li>
 * </ul>
 *
 * @author Kasper
 */
class ProductManagerTest {

	@Test
	void getProduct() {
		//Set up
		PersistenceFacade persistence = PersistenceFactory.createDatabaseMediator();
		ProductManager pManager = new ProductManager(persistence);
		AttributeManager aManager = new AttributeManager(persistence);
		CategoryManager cManager = new CategoryManager(persistence);
		TagManager tManager = new TagManager(persistence);
		persistence.setCache(new DataCacheImpl(pManager, aManager, cManager, tManager));

		//Try reading the product with id 30 (Omen laptop)
		try {
			Product p = pManager.getProduct(30);

			//Test basic data
			Assertions.assertEquals(p.getID(), 30);
			Assertions.assertEquals(p.getName(), "HP OMEN 15-AX005NO");
			Assertions.assertEquals(p.getDescription(), "Some generic laptop with mediocre specs.");
			Assertions.assertEquals(p.getPrice(), 8999.95);

			//Test categories
			Set<String> expectedCategories = new HashSet<>(Collections.singletonList("Laptops"));
			Assertions.assertTrue(p.getCategories().size() == expectedCategories.size());
			for (Category c : p.getCategories()) {
				Assertions.assertTrue(expectedCategories.contains(c.getName()));
			}

			//Test attributes
			Set<Integer> expectedAttributes = new HashSet<>(Arrays.asList(3, 4, 6, 7, 8, 16));
			Assertions.assertTrue(p.getAttributeValues().size() == expectedAttributes.size());
			for (Attribute.AttributeValue a : p.getAttributeValues()) {
				Assertions.assertTrue(expectedAttributes.contains(a.getParent().getID()));
			}

			//Test tags
			Set<String> expectedTags = new HashSet<>(Arrays.asList("Value-product", "Epic", "Must-have"));
			Assertions.assertTrue(p.getTags().size() == expectedTags.size());
			for (Tag t : p.getTags()) {
				Assertions.assertTrue(expectedTags.contains(t.getName()));
			}

			//Test images
			Set<String> expectedImages = new HashSet<>(Collections.singletonList("res/omen-ax005no.jpg"));
			Assertions.assertTrue(p.getImages().size() == expectedImages.size());
			for (Image i : p.getImages()) {
				Assertions.assertTrue(expectedImages.contains(i.getUrl()));
			}
		} catch (IOException e) {
			Assertions.fail("Database connection might be lost, try again", e);
		}

		//Try reading a nonexistent product
		try {
			Assertions.assertTrue(pManager.getProduct(-1) == null);
		} catch (IOException e) {
			Assertions.fail("Database connection might be lost, try again", e);
		}
	}
}