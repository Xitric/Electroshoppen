package pim.business;

import java.util.Arrays;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
public class AttributeTest {

	private static Attribute a;

	@Before
	public void setup() {
		a = new Attribute(-1, "Some attribute", 'c', new HashSet<>(Arrays.asList('a', 'b', 'c', 'd', 'e')));
	}

	@Test
	public void createValue() {
		Attribute.AttributeValue aVal = a.createValue();

		//Test that the attribute value was constructed properly
		Assert.assertEquals(aVal.getValue(), a.getDefaultValue());
		Assert.assertEquals(aVal.getParent(), a);
	}

	@Test(expected=IllegalArgumentException.class)
	public void createValue1() {
		Attribute.AttributeValue aVal = a.createValue('e');

		//Test that the attribute value was constructed properly
		Assert.assertEquals('e', aVal.getValue());
		Assert.assertEquals(aVal.getParent(), a);

		//Test an invalid construction
		a.createValue('f');
	}

	@Test
	public void setID() {
		//Test that we can set the initial id
		a.setID(1);
		Assert.assertTrue(a.getID() == 1);

		//Test that we cannot change it again
		a.setID(2);
		Assert.assertTrue(a.getID() == 1);
	}
}