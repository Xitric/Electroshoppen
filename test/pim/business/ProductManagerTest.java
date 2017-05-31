package pim.business;

import pim.persistence.PIMPersistenceFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link Product}. This class tests the following methods:
 * <ul>
 * <li>{@link ProductManager#getProduct(int)}</li>
 * </ul>
 *
 * @author Kasper
 */
public class ProductManagerTest {

	@Test
	public void getProduct() {
		//Set up
		PIMPersistenceFacade persistence = PIMPersistenceFactory.createDatabaseMediator();
		ProductManager pManager = new ProductManager(persistence);
		AttributeManager aManager = new AttributeManager(persistence);
		CategoryManager cManager = new CategoryManager(persistence);
		TagManager tManager = new TagManager();
		persistence.setCache(new DataCacheImpl(pManager, aManager, cManager, tManager));

		//Try reading the product with id 30 (Omen laptop)
		try {
			Product p = pManager.getProduct(30);

			//Test basic data
			Assert.assertEquals(30, p.getID());
			Assert.assertEquals("HP OMEN 15-AX005NO", p.getName());
			Assert.assertEquals("Cool gaming laptop.\n" +
					"Specs: Intel core I7-6700HQ (2,5 - 3,6 Ghz), Nvidia GeForce GTX 960M with 4GB og dedicated DDR5 memory, 8GB DDR4 memory, 256GB M.2 NVMe SSD, 15,6\" (1920 x 1080), Windows 10.", p.getDescription());
			Assert.assertEquals(8999.95, p.getPrice(), 0.0);

			//Test categories
			Set<String> expectedCategories = new HashSet<>(Collections.singletonList("Laptops"));
			Assert.assertTrue(p.getCategories().size() == expectedCategories.size());
			for (Category c : p.getCategories()) {
				Assert.assertTrue(expectedCategories.contains(c.getName()));
			}

			//Test attributes
			Set<Integer> expectedAttributes = new HashSet<>(Arrays.asList(3, 4, 6, 7, 8, 12));
			Assert.assertTrue(p.getAttributeValues().size() == expectedAttributes.size());
			for (Attribute.AttributeValue a : p.getAttributeValues()) {
				Assert.assertTrue(expectedAttributes.contains(a.getParent().getID()));
			}

			//Test tags
			Set<String> expectedTags = new HashSet<>(Arrays.asList("Value-product", "Epic", "Must-have"));
			Assert.assertTrue(p.getTags().size() == expectedTags.size());
			for (Tag t : p.getTags()) {
				Assert.assertTrue(expectedTags.contains(t.getName()));
			}

			//Test images
			BufferedImage expectedImage = ImageIO.read(getClass().getResourceAsStream("omen-ax005no.jpg"));
			Assert.assertTrue(p.getImages().size() == 1);
			for (Image i : p.getImages()) {
				Assert.assertTrue(compareImages(i.getImage(), expectedImage));
			}
		} catch (IOException e) {
			Assert.fail("Database connection might be lost, try again");
		}

		//Try reading a nonexistent product
		try {
			Assert.assertTrue(pManager.getProduct(-1) == null);
		} catch (IOException e) {
			Assert.fail("Database connection might be lost, try again");
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