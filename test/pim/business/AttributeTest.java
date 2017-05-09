package pim.business;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Test class for {@link Attribute}. This class tests the following methods:
 * <ul>
 * <li>{@link Attribute#createValue()}</li>
 * <li>{@link Attribute#createValue(Object)}</li>
 * <li>{@link Attribute#setID(int)}</li>
 * </ul>s
 *
 * @author Kasper
 */
class AttributeTest {

	private static Attribute a;

	@BeforeAll
	static void setup() {
		a = new Attribute(-1, "Some attribute", 'c', new HashSet<>(Arrays.asList('a', 'b', 'c', 'd', 'e')));
	}

	@Test
	void createValue() {
		Attribute.AttributeValue aVal = a.createValue();

		//Test that the attribute value was constructed properly
		Assertions.assertEquals(aVal.getValue(), a.getDefaultValue());
		Assertions.assertEquals(aVal.getParent(), a);
	}

	@Test
	void createValue1() {
		Attribute.AttributeValue aVal = a.createValue('e');

		//Test that the attribute value was constructed properly
		Assertions.assertEquals(aVal.getValue(), 'e');
		Assertions.assertEquals(aVal.getParent(), a);

		//Test an invalid construction
		Assertions.assertThrows(IllegalArgumentException.class, () -> a.createValue('f'));
	}

	@Test
	void setID() {
		//Test that we can set the initial id
		a.setID(1);
		Assertions.assertTrue(a.getID() == 1);

		//Test that we cannot change it again
		a.setID(2);
		Assertions.assertTrue(a.getID() == 1);
	}
}