package pim.business;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link Product}. This class tests the following methods:
 * <ul>
 * <li>{@link Category#addAttribute(Attribute)}</li>
 * <li>{@link Category#removeAttribute(Attribute)}</li>
 * </ul>
 *
 * @author Kasper
 */
public class CategoryTest {

	@Test
	public void addAttribute() {
		//Set up
		Product p = new Product(1, "Some product", "Some product description", 14.99);
		Category someCategory = new Category("Some category");
		p.addCategory(someCategory);
		Attribute someAttribute = new Attribute(1, "First attribute", 'c');

		//Add an attribute
		someCategory.addAttribute(someAttribute);
		Assert.assertTrue(someCategory.getAttributes().size() == 1 && someCategory.getAttributes().contains(someAttribute));
		Assert.assertTrue(p.getAttributeValues().size() == 1 && p.getAttributeValues().get(0).getValue().equals(someAttribute.getDefaultValue()));

		//Add existing attribute
		someCategory.addAttribute(someAttribute);
		Assert.assertTrue(someCategory.getAttributes().size() == 1 && someCategory.getAttributes().contains(someAttribute));
		Assert.assertTrue(p.getAttributeValues().size() == 1 && p.getAttributeValues().get(0).getValue().equals(someAttribute.getDefaultValue()));
	}

	@Test
	public void removeAttribute() {
		//Set up
		Product p = new Product(1, "Some product", "Some product description", 14.99);
		Category someCategory = new Category("Some category");
		p.addCategory(someCategory);
		Attribute someAttribute1 = new Attribute(1, "First attribute", 'c');
		someCategory.addAttribute(someAttribute1);
		Attribute someAttribute2 = new Attribute(2, "Second attribute", false);
		someCategory.addAttribute(someAttribute2);

		//Remove an attribute
		someCategory.removeAttribute(someAttribute1);
		Assert.assertTrue(someCategory.getAttributes().size() == 1 && someCategory.getAttributes().contains(someAttribute2));
		Assert.assertTrue(p.getAttributeValues().size() == 1 && p.getAttributeValues().get(0).getValue().equals(someAttribute2.getDefaultValue()));

		//Remove nonexistent attribute
		someCategory.removeAttribute(someAttribute1);
		Assert.assertTrue(someCategory.getAttributes().size() == 1 && someCategory.getAttributes().contains(someAttribute2));
		Assert.assertTrue(p.getAttributeValues().size() == 1 && p.getAttributeValues().get(0).getValue().equals(someAttribute2.getDefaultValue()));
	}
}