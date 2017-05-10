package pim.business;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link Product}. This class tests the following methods:
 * <ul>
 * <li>{@link Category#addAttribute(Attribute)}</li>
 * <li>{@link Category#removeAttribute(Attribute)}</li>
 * </ul>
 *
 * @author Kasper
 */
class CategoryTest {

	@Test
	void addAttribute() {
		//Set up
		Product p = new Product(1, "Some product", "Some product description", 14.99);
		Category someCategory = new Category("Some category");
		p.addCategory(someCategory);
		Attribute someAttribute = new Attribute(1, "First attribute", 'c');

		//Add an attribute
		someCategory.addAttribute(someAttribute);
		Assertions.assertTrue(someCategory.getAttributes().size() == 1 && someCategory.getAttributes().contains(someAttribute));
		Assertions.assertTrue(p.getAttributeValues().size() == 1 && p.getAttributeValues().get(0).getValue().equals(someAttribute.getDefaultValue()));
	}

	@Test
	void removeAttribute() {
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
		Assertions.assertTrue(someCategory.getAttributes().size() == 1 && someCategory.getAttributes().contains(someAttribute2));
		Assertions.assertTrue(p.getAttributeValues().size() == 1 && p.getAttributeValues().get(0).getValue().equals(someAttribute2.getDefaultValue()));
	}
}