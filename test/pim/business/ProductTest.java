package pim.business;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

/**
 * Test class for {@link Product}. This class tests the following methods:
 * <ul>
 * <li>{@link Product#addCategory(Category)}</li>
 * <li>{@link Product#removeCategory(Category)}</li>
 * <li>{@link Product#setAttribute(Attribute, Object)}</li>
 * </ul>
 *
 * @author Kasper
 */
class ProductTest {

	@Test
	void addCategory() {
		Product p = new Product(1, "Test Product", "A test product.", 14.99);

		//Set up categories
		Attribute firstAttribute = new Attribute(1, "First attribute", "Default");
		Attribute secondAttribute = new Attribute(2, "Second attribute", 'c');
		Attribute thirdAttribute = new Attribute(3, "Third attribute", false);
		Category someCategory1 = new Category("First category", new HashSet<>(Arrays.asList(firstAttribute, secondAttribute)));
		Category someCategory2 = new Category("Second category", new HashSet<>());
		Category someCategory3 = new Category("Third category", new HashSet<>(Arrays.asList(secondAttribute, thirdAttribute)));

		//Add first category and test that the new category is in fact added along with its attributes
		p.addCategory(someCategory1);
		Assertions.assertTrue(p.getCategories().size() == 1 && p.getCategories().contains(someCategory1));
		Assertions.assertTrue(p.getAttributeValues().size() == 2);
		for (Attribute.AttributeValue value : p.getAttributeValues()) {
			Assertions.assertTrue(Arrays.asList(firstAttribute, secondAttribute).contains(value.getParent()));
			Assertions.assertTrue(value.getParent().getDefaultValue() == value.getValue());
		}

		//Add a new category with no attributes, and test that only the category set has changed
		p.addCategory(someCategory2);
		Assertions.assertTrue(p.getCategories().size() == 2 && p.getCategories().containsAll(Arrays.asList(someCategory1, someCategory2)));
		Assertions.assertTrue(p.getAttributeValues().size() == 2);
		for (Attribute.AttributeValue value : p.getAttributeValues()) {
			Assertions.assertTrue(Arrays.asList(firstAttribute, secondAttribute).contains(value.getParent()));
			Assertions.assertTrue(value.getParent().getDefaultValue() == value.getValue());
		}

		//Add a new category with a common attribute to ensure correct combination
		p.addCategory(someCategory3);
		Assertions.assertTrue(p.getCategories().size() == 3 && p.getCategories().containsAll(Arrays.asList(someCategory1, someCategory2, someCategory3)));
		Assertions.assertTrue(p.getAttributeValues().size() == 3);
		for (Attribute.AttributeValue value : p.getAttributeValues()) {
			Assertions.assertTrue(Arrays.asList(firstAttribute, secondAttribute, thirdAttribute).contains(value.getParent()));
			Assertions.assertTrue(value.getParent().getDefaultValue() == value.getValue());
		}
	}

	@Test
	void removeCategory() {
		Product p = new Product(1, "Test Product", "A test product.", 14.99);

		//Set up categories
		Attribute firstAttribute = new Attribute(1, "First attribute", "Default");
		Attribute secondAttribute = new Attribute(2, "Second attribute", 'c');
		Attribute thirdAttribute = new Attribute(3, "Third attribute", false);
		Category someCategory1 = new Category("First category", new HashSet<>(Arrays.asList(firstAttribute, secondAttribute)));
		Category someCategory2 = new Category("Second category", new HashSet<>(Arrays.asList(secondAttribute, thirdAttribute)));
		Category someCategory3 = new Category("Third category", new HashSet<>(Collections.singletonList(secondAttribute)));
		p.addCategory(someCategory1);
		p.addCategory(someCategory2);

		//Remove invalid category with shared attribute
		p.removeCategory(someCategory3);
		Assertions.assertTrue(p.getCategories().size() == 2 && p.getCategories().containsAll(Arrays.asList(someCategory1, someCategory2)));
		Assertions.assertTrue(p.getAttributeValues().size() == 3);
		for (Attribute.AttributeValue value : p.getAttributeValues()) {
			Assertions.assertTrue(Arrays.asList(firstAttribute, secondAttribute, thirdAttribute).contains(value.getParent()));
			Assertions.assertTrue(value.getParent().getDefaultValue() == value.getValue());
		}

		//Remove valid category
		p.removeCategory(someCategory2);
		Assertions.assertTrue(p.getCategories().size() == 1 && p.getCategories().containsAll(Collections.singletonList(someCategory1)));
		Assertions.assertTrue(p.getAttributeValues().size() == 2);
		for (Attribute.AttributeValue value : p.getAttributeValues()) {
			Assertions.assertTrue(Arrays.asList(firstAttribute, secondAttribute).contains(value.getParent()));
			Assertions.assertTrue(value.getParent().getDefaultValue() == value.getValue());
		}
	}

	@Test
	void setAttribute() {
		Product p = new Product(1, "Test Product", "A test product.", 14.99);

		//Set up attributes
		Attribute firstAttribute = new Attribute(1, "First attribute", "Default");
		Attribute secondAttribute = new Attribute(2, "Second attribute", 'c', new HashSet<>(Arrays.asList('a', 'b', 'c', 'd')));
		Attribute thirdAttribute = new Attribute(3, "Third attribute", false);
		Category someCategory1 = new Category("First category", new HashSet<>(Arrays.asList(firstAttribute, secondAttribute)));
		p.addCategory(someCategory1);

		//Add invalid attribute thirdAttribute
		p.setAttribute(thirdAttribute, new Object());
		Assertions.assertTrue(p.getAttributeValues().size() == 2);
		for (Attribute.AttributeValue value : p.getAttributeValues()) {
			Assertions.assertTrue(Arrays.asList(firstAttribute, secondAttribute).contains(value.getParent()));
			Assertions.assertTrue(value.getParent().getDefaultValue() == value.getValue());
		}

		//Add invalid attribute value 'e'
		Assertions.assertThrows(IllegalArgumentException.class, () -> p.setAttribute(secondAttribute, 'e'));
		Assertions.assertTrue(p.getAttributeValues().size() == 2);
		for (Attribute.AttributeValue value : p.getAttributeValues()) {
			Assertions.assertTrue(Arrays.asList(firstAttribute, secondAttribute).contains(value.getParent()));
			Assertions.assertTrue(value.getParent().getDefaultValue() == value.getValue());
		}

		//Add legal attribute value 'a'
		p.setAttribute(secondAttribute, 'a');
		Assertions.assertTrue(p.getAttributeValues().size() == 2);
		for (Attribute.AttributeValue value : p.getAttributeValues()) {
			Assertions.assertTrue(Arrays.asList(firstAttribute, secondAttribute).contains(value.getParent()));
			if (value.getParent() == firstAttribute) {
				Assertions.assertTrue(value.getParent().getDefaultValue() == value.getValue());
			} else {
				Assertions.assertTrue(value.getValue().equals('a'));
			}
		}
	}
}