package pim.business;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pim.persistence.PIMPersistenceFactory;
import shared.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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
		PIMPersistenceFacade persistence = PIMPersistenceFactory.createDatabaseMediator();
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
			Assertions.assertEquals(p.getDescription(), "Cool gaming laptop.\n" +
					"Specs: Intel core I7-6700HQ (2,5 - 3,6 Ghz), Nvidia GeForce GTX 960M with 4GB og dedicated DDR5 memory, 8GB DDR4 memory, 256GB M.2 NVMe SSD, 15,6\" (1920 x 1080), Windows 10.");
			Assertions.assertEquals(p.getPrice(), 8999.95);

			//Test categories
			Set<String> expectedCategories = new HashSet<>(Collections.singletonList("Laptops"));
			Assertions.assertTrue(p.getCategories().size() == expectedCategories.size());
			for (Category c : p.getCategories()) {
				Assertions.assertTrue(expectedCategories.contains(c.getName()));
			}

			//Test attributes
			Set<Integer> expectedAttributes = new HashSet<>(Arrays.asList(3, 4, 6, 7, 8, 12));
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
			BufferedImage expectedImage = ImageIO.read(getClass().getResourceAsStream("omen-ax005no.jpg"));
			Assertions.assertTrue(p.getImages().size() == 1);
			for (Image i : p.getImages()) {
				Assertions.assertTrue(compareImages(i.getImage(), expectedImage));
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

	boolean compareImages(BufferedImage i1, BufferedImage i2) {
		//Early out, if possible
		if (i1.getWidth() != i2.getWidth() || i1.getHeight() != i2.getHeight()) {
			return false;
		}

		//Images are the same size, test every pixel
		for (int x = 0; x < i1.getWidth(); x++) {
			for (int y = 0; y < i1.getHeight(); y++) {
				if (i1.getRGB(x, y) != i2.getRGB(x, y)) {
					return false;
				}
			}
		}

		return true;
	}
}