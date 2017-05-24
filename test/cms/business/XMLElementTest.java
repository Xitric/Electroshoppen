package cms.business;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kasper
 */
class XMLElementTest {

	//Possible input:
	//Null or empty tag name -> Exception
	//Null or empty text content
	//Null attributes map
	//All proper input
	@Test
	void createRoot() {
		Map<String, String> attributes = new HashMap<>();
		attributes.put("a1", "v1");
		attributes.put("a2", "v2");

		Assertions.assertThrows(IllegalArgumentException.class, () -> XMLElement.createRoot(null, "Hello, world!", attributes));

	}

	//Possible input:
	//Null or empty tag name -> Exception
	//Null or empty text content
	//Null attributes map
	//All proper input
	@Test
	void createChild() {
	}

	//Possible input:
	//The element itself
	//A current child of the element
	//A parent of the element
	//A child with an existing parent
	//A child with no existing parent
	@Test
	void addChild() {
	}

	//Possible input:
	//The element itself
	//A current child of the element
	//A parent of the element
	//A child with an existing parent
	//A child with no existing parent
	//Index 0
	//Index size()
	//Index in between  and size()
	//Index < 0 or > size()
	@Test
	void addChild1() {
	}

	//Possible input:
	//A valid child
	//An invalid child
	@Test
	void removeChild() {
	}

	//Possible input:
	//None
	@Test
	void clear() {
	}

	//Possible input:
	//A tag in the immediate children
	//A tag in the deeper children
	//A nonexisting tag
	@Test
	void getChildrenByTagDeep() {
	}

	//Possible input:
	//An id among the immediate children
	//An id among the deeper children
	//A nonexisting id
	@Test
	void getChildByID() {
	}

	//Possible input:
	//A class already on the element
	//A new class
	@Test
	void addClass() {
	}

	//Possible input:
	//A class on the element
	//A class not on the element
	@Test
	void removeClass() {
	}

	//Possible input:
	//A null name and a valid value
	//A null name and a null or empty value
	//A name of a nonexsiting attribute and a valid value
	//A name of a nonexsiting attribute and a null or empty value
	//A name of an existing attribute and a valid value
	//A name of an existing attribute and a null or empty value
	@Test
	void setAttribute() {
	}

}