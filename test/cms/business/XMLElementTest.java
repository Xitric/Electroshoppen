package cms.business;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kasper
 */
class XMLElementTest {

	private static XMLElement tree;
	private static XMLElement head;
	private static XMLElement script;
	private static XMLElement body;
	private static XMLElement div1;
	private static XMLElement div2;
	private static XMLElement div3;
	private static XMLElement p;

	@BeforeAll
	static void prepTree() {
		tree = XMLElement.createRoot("html");
		head = XMLElement.createRoot("head");
		tree.addChild(head);

		script = XMLElement.createRoot("script");
		script.setID("sc");
		head.addChild(script);

		body = XMLElement.createRoot("body");
		tree.addChild(body);

		div1 = XMLElement.createRoot("div");
		div1.setID("div1");
		body.addChild(div1);

		div2 = XMLElement.createRoot("div");
		body.addChild(div2);

		p = XMLElement.createRoot("p");
		p.setID("p1");
		div2.addChild(p);

		div3 = XMLElement.createRoot("div");
		body.addChild(div3);
	}

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

		XMLElement element1 = XMLElement.createRoot("p", null, attributes);
		Assertions.assertEquals(element1.getTagName(), "p");
		Assertions.assertTrue(element1.getTextContent().isEmpty());
		Assertions.assertTrue(element1.isRoot());
		for (String att : attributes.keySet()) {
			Assertions.assertEquals(element1.getAttribute(att), attributes.get(att));
		}

		XMLElement element2 = XMLElement.createRoot("p", "Hello, world!", null);
		Assertions.assertEquals(element2.getTagName(), "p");
		Assertions.assertEquals(element2.getTextContent(), "Hello, world!");
		Assertions.assertTrue(element1.isRoot());

		XMLElement element3 = XMLElement.createRoot("p", "Hello, world!", attributes);
		Assertions.assertEquals(element3.getTagName(), "p");
		Assertions.assertEquals(element3.getTextContent(), "Hello, world!");
		Assertions.assertTrue(element1.isRoot());
		for (String att : attributes.keySet()) {
			Assertions.assertEquals(element3.getAttribute(att), attributes.get(att));
		}
	}

	//Possible input:
	//Null or empty tag name -> Exception
	//Null or empty text content
	//Null attributes map
	//All proper input
	@Test
	void createChild() {
		Map<String, String> attributes = new HashMap<>();
		attributes.put("a1", "v1");
		attributes.put("a2", "v2");
		XMLElement root = XMLElement.createRoot("root");

		Assertions.assertThrows(IllegalArgumentException.class, () -> root.createChild(null, "Hello, world!", attributes));

		XMLElement element1 = root.createChild("p", null, attributes);
		Assertions.assertEquals(element1.getTagName(), "p");
		Assertions.assertTrue(element1.getTextContent().isEmpty());
		for (String att : attributes.keySet()) {
			Assertions.assertEquals(element1.getAttribute(att), attributes.get(att));
		}
		Assertions.assertEquals(element1.getParent(), root);
		Assertions.assertTrue(root.contains(element1));

		XMLElement element2 = root.createChild("p", "Hello, world!", null);
		Assertions.assertEquals(element2.getTagName(), "p");
		Assertions.assertEquals(element2.getTextContent(), "Hello, world!");
		Assertions.assertEquals(element2.getParent(), root);
		Assertions.assertTrue(root.contains(element2));

		XMLElement element3 = root.createChild("p", "Hello, world!", attributes);
		Assertions.assertEquals(element3.getTagName(), "p");
		Assertions.assertEquals(element3.getTextContent(), "Hello, world!");
		for (String att : attributes.keySet()) {
			Assertions.assertEquals(element3.getAttribute(att), attributes.get(att));
		}
		Assertions.assertEquals(element3.getParent(), root);
		Assertions.assertTrue(root.contains(element3));

		Assertions.assertTrue(root.getChildren().size() == 3);
	}

	//Possible input:
	//The element itself
	//A current child of the element
	//A parent of the element
	//A child with an existing parent
	//A child with no existing parent
	@Test
	void addChild() {
		XMLElement root = XMLElement.createRoot("p");
		XMLElement child1 = XMLElement.createRoot("p");
		XMLElement rootNew = XMLElement.createRoot("p");

		//The element itself
		Assertions.assertThrows(IllegalArgumentException.class, () -> root.addChild(root));

		//A child with no existing parent
		root.addChild(child1);
		Assertions.assertTrue(root.getChildren().size() == 1);
		Assertions.assertTrue(root.getChildren().get(0) == child1);
		Assertions.assertTrue(child1.getParent() == root);

		//A current child of the element
		Assertions.assertThrows(IllegalArgumentException.class, () -> root.addChild(child1));

		//A parent of the element
		Assertions.assertThrows(IllegalArgumentException.class, () -> child1.addChild(root));

		//A child with an existing parent
		rootNew.addChild(child1);
		Assertions.assertTrue(rootNew.getChildren().size() == 1);
		Assertions.assertTrue(rootNew.getChildren().get(0) == child1);
		Assertions.assertTrue(child1.getParent() == rootNew);
		Assertions.assertTrue(root.getChildren().size() == 0);
	}

	//Possible input:
	//The different element options are tested above, and so are omitted here
	//Index 0
	//Index size()
	//Index in between 0 and size()
	//Index < 0 or > size()
	@Test
	void addChild1() {
		XMLElement root = XMLElement.createRoot("p");
		XMLElement child1 = XMLElement.createRoot("p");
		XMLElement child2 = XMLElement.createRoot("p");
		XMLElement child3 = XMLElement.createRoot("p");
		XMLElement child4 = XMLElement.createRoot("p");
		root.addChild(XMLElement.createRoot("q"));
		root.addChild(XMLElement.createRoot("q"));
		root.addChild(XMLElement.createRoot("q"));
		root.addChild(XMLElement.createRoot("q"));

		//Index 0
		root.addChild(child1, 0);
		Assertions.assertTrue(root.getChildren().size() == 5);
		Assertions.assertTrue(root.getChildren().get(0) == child1);

		//Index size()
		int index = root.getChildren().size();
		root.addChild(child2, index);
		Assertions.assertTrue(root.getChildren().size() == 6);
		Assertions.assertTrue(root.getChildren().get(index) == child2);

		//Index in between 0 and size()
		root.addChild(child3, 2);
		Assertions.assertTrue(root.getChildren().size() == 7);
		Assertions.assertTrue(root.getChildren().get(2) == child3);

		//Index < 0 or > size()
		Assertions.assertThrows(IndexOutOfBoundsException.class, () -> root.addChild(child4, -1));
		Assertions.assertThrows(IndexOutOfBoundsException.class, () -> root.addChild(child4, 10));
	}

	//Possible input:
	//A valid child
	//An invalid child
	@Test
	void removeChild() {
		XMLElement root = XMLElement.createRoot("p");
		XMLElement child1 = XMLElement.createRoot("p");
		XMLElement child2 = XMLElement.createRoot("p");
		XMLElement child3 = XMLElement.createRoot("p");
		XMLElement other = XMLElement.createRoot("p");
		root.addChild(child1);
		root.addChild(child2);
		root.addChild(child3);

		//A valid child
		root.removeChild(child2);
		Assertions.assertTrue(root.getChildren().size() == 2);
		Assertions.assertEquals(root.getChildren().get(0), child1);
		Assertions.assertEquals(root.getChildren().get(1), child3);
		Assertions.assertTrue(child2.isRoot());

		//An invalid child
		root.removeChild(other);
		Assertions.assertTrue(root.getChildren().size() == 2);
		Assertions.assertEquals(root.getChildren().get(0), child1);
		Assertions.assertEquals(root.getChildren().get(1), child3);
	}

	//Possible input:
	//None
	@Test
	void clear() {
		XMLElement root = XMLElement.createRoot("p");
		XMLElement child = XMLElement.createRoot("q");
		root.addChild(child);
		root.addChild(XMLElement.createRoot("q"));
		root.addChild(XMLElement.createRoot("q"));
		root.addChild(XMLElement.createRoot("q"));
		root.addChild(XMLElement.createRoot("q"));

		Assertions.assertTrue(root.getChildren().size() == 5);
		root.clear();
		Assertions.assertTrue(root.getChildren().size() == 0);
		Assertions.assertTrue(child.isRoot());
	}

	//Possible input:
	//A tag in the immediate children
	//A tag in the deeper children
	//A nonexisting tag
	@Test
	void getChildrenByTagDeep() {
		//A tag in the immediate children
		List<XMLElement> children = tree.getChildrenByTagDeep("head");
		Assertions.assertTrue(children.size() == 1);
		Assertions.assertEquals(children.get(0), head);

		//A tag in the deeper children
		List<XMLElement> deepChildren = tree.getChildrenByTagDeep("div");
		Assertions.assertTrue(deepChildren.size() == 3);
		Assertions.assertTrue(deepChildren.contains(div1));
		Assertions.assertTrue(deepChildren.contains(div2));
		Assertions.assertTrue(deepChildren.contains(div3));

		//A nonexisting tag
		List<XMLElement> noChildren = tree.getChildrenByTagDeep("foo");
		Assertions.assertTrue(noChildren.size() == 0);
	}

	//Possible input:
	//An id among the immediate children
	//An id among the deeper children
	//A nonexisting id
	@Test
	void getChildByID() {
		//An id among the immediate children
		XMLElement immediateChild = tree.getChildByID("sc");
		Assertions.assertEquals(immediateChild, script);

		//An id among the deeper children
		XMLElement deepChild1 = tree.getChildByID("p1");
		Assertions.assertEquals(deepChild1, p);

		XMLElement deepChild2 = tree.getChildByID("div1");
		Assertions.assertEquals(deepChild2, div1);

		//A nonexisting id
		XMLElement noChild = tree.getChildByID("bar");
		Assertions.assertNull(noChild);
	}

	//Possible input:
	//A class already on the element
	//A new class
	@Test
	void addClass() {
		XMLElement element = XMLElement.createRoot("p");

		//A new class
		element.addClass("foo");
		Assertions.assertTrue(element.getClasses().length == 1);
		Assertions.assertEquals(element.getClasses()[0], "foo");

		element.addClass("bar");
		Assertions.assertTrue(element.getClasses().length == 2);
		Assertions.assertEquals(element.getClasses()[0], "foo");
		Assertions.assertEquals(element.getClasses()[1], "bar");

		//A class already on the element
		element.addClass("bar");
		Assertions.assertTrue(element.getClasses().length == 2);
		Assertions.assertEquals(element.getClasses()[0], "foo");
		Assertions.assertEquals(element.getClasses()[1], "bar");
	}

	//Possible input:
	//A class on the element
	//A class not on the element
	@Test
	void removeClass() {
		XMLElement element = XMLElement.createRoot("p");
		element.addClass("foo");
		element.addClass("bar");

		//A class not on the element
		element.removeClass("apples");
		Assertions.assertTrue(element.getClasses().length == 2);
		Assertions.assertEquals(element.getClasses()[0], "foo");
		Assertions.assertEquals(element.getClasses()[1], "bar");

		//A class on the element
		element.removeClass("foo");
		Assertions.assertTrue(element.getClasses().length == 1);
		Assertions.assertEquals(element.getClasses()[0], "bar");
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
		Map<String, String> attributes = new HashMap<>();
		attributes.put("a1", "v1");
		attributes.put("a2", "v2");
		XMLElement element = XMLElement.createRoot("p", "text", attributes);

		//A null name and a valid value
		element.setAttribute(null, "bar");
		Assertions.assertNull(element.getAttribute(null));

		//A null name and a null or empty value
		element.setAttribute(null, "");
		Assertions.assertNull(element.getAttribute(null));

		//A name of a nonexsiting attribute and a valid value
		element.setAttribute("a3", "bar");
		Assertions.assertEquals(element.getAttribute("a3"), "bar");

		//A name of a nonexsiting attribute and a null or empty value
		element.setAttribute("a4", "");
		Assertions.assertNull(element.getAttribute("a4"));

		//A name of an existing attribute and a valid value
		element.setAttribute("a1", "foo");
		Assertions.assertEquals(element.getAttribute("a1"), "foo");

		//A name of an existing attribute and a null or empty value
		element.setAttribute("a2", "");
		Assertions.assertNull(element.getAttribute("a2"));
	}
}