package cms.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

/**
 * @author Kasper
 */
public class XMLElementTest {

	private static XMLElement tree;
	private static XMLElement head;
	private static XMLElement script;
	private static XMLElement body;
	private static XMLElement div1;
	private static XMLElement div2;
	private static XMLElement div3;
	private static XMLElement p;

	@Before
	public void prepTree() {
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
	@Test(expected=IllegalArgumentException.class)
	public void createRoot() {
		Map<String, String> attributes = new HashMap<>();
		attributes.put("a1", "v1");
		attributes.put("a2", "v2");
		
		XMLElement.createRoot(null, "Hello, world!", attributes);

		XMLElement element1 = XMLElement.createRoot("p", null, attributes);
		Assert.assertEquals("p", element1.getTagName());
		Assert.assertTrue(element1.getTextContent().isEmpty());
		Assert.assertTrue(element1.isRoot());
		for (String att : attributes.keySet()) {
			Assert.assertEquals(element1.getAttribute(att), attributes.get(att));
		}

		XMLElement element2 = XMLElement.createRoot("p", "Hello, world!", null);
		Assert.assertEquals("p", element2.getTagName());
		Assert.assertEquals("Hello, world!", element2.getTextContent());
		Assert.assertTrue(element1.isRoot());

		XMLElement element3 = XMLElement.createRoot("p", "Hello, world!", attributes);
		Assert.assertEquals("p", element3.getTagName());
		Assert.assertEquals("Hello, world!", element3.getTextContent());
		Assert.assertTrue(element1.isRoot());
		for (String att : attributes.keySet()) {
			Assert.assertEquals(element3.getAttribute(att), attributes.get(att));
		}
	}

	//Possible input:
	//Null or empty tag name -> Exception
	//Null or empty text content
	//Null attributes map
	//All proper input
	@Test(expected=IllegalArgumentException.class)
	public void createChild() {
		Map<String, String> attributes = new HashMap<>();
		attributes.put("a1", "v1");
		attributes.put("a2", "v2");
		XMLElement root = XMLElement.createRoot("root");

		root.createChild(null, "Hello, world!", attributes);

		XMLElement element1 = root.createChild("p", null, attributes);
		Assert.assertEquals("p", element1.getTagName());
		Assert.assertTrue(element1.getTextContent().isEmpty());
		for (String att : attributes.keySet()) {
			Assert.assertEquals(element1.getAttribute(att), attributes.get(att));
		}
		Assert.assertEquals(element1.getParent(), root);
		Assert.assertTrue(root.contains(element1));

		XMLElement element2 = root.createChild("p", "Hello, world!", null);
		Assert.assertEquals("p", element2.getTagName());
		Assert.assertEquals("Hello, world!", element2.getTextContent());
		Assert.assertEquals(element2.getParent(), root);
		Assert.assertTrue(root.contains(element2));

		XMLElement element3 = root.createChild("p", "Hello, world!", attributes);
		Assert.assertEquals("p", element3.getTagName());
		Assert.assertEquals("Hello, world!", element3.getTextContent());
		for (String att : attributes.keySet()) {
			Assert.assertEquals(element3.getAttribute(att), attributes.get(att));
		}
		Assert.assertEquals(element3.getParent(), root);
		Assert.assertTrue(root.contains(element3));

		Assert.assertTrue(root.getChildren().size() == 3);
	}

	//Possible input:
	//The element itself
	//A current child of the element
	//A parent of the element
	//A child with an existing parent
	//A child with no existing parent
	@Test(expected=IllegalArgumentException.class)
	public void addChild() {
		XMLElement root = XMLElement.createRoot("p");
		XMLElement child1 = XMLElement.createRoot("p");
		XMLElement rootNew = XMLElement.createRoot("p");

		//The element itself
		root.addChild(root);

		//A child with no existing parent
		root.addChild(child1);
		Assert.assertTrue(root.getChildren().size() == 1);
		Assert.assertTrue(root.getChildren().get(0) == child1);
		Assert.assertTrue(child1.getParent() == root);

		//A current child of the element
		root.addChild(child1);

		//A parent of the element
		child1.addChild(root);

		//A child with an existing parent
		rootNew.addChild(child1);
		Assert.assertTrue(rootNew.getChildren().size() == 1);
		Assert.assertTrue(rootNew.getChildren().get(0) == child1);
		Assert.assertTrue(child1.getParent() == rootNew);
		Assert.assertTrue(root.getChildren().isEmpty());
	}

	//Possible input:
	//The different element options are tested above, and so are omitted here
	//Index 0
	//Index size()
	//Index in between 0 and size()
	//Index < 0 or > size()
	@Test(expected=IndexOutOfBoundsException.class)
	public void addChild1() {
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
		Assert.assertTrue(root.getChildren().size() == 5);
		Assert.assertTrue(root.getChildren().get(0) == child1);

		//Index size()
		int index = root.getChildren().size();
		root.addChild(child2, index);
		Assert.assertTrue(root.getChildren().size() == 6);
		Assert.assertTrue(root.getChildren().get(index) == child2);

		//Index in between 0 and size()
		root.addChild(child3, 2);
		Assert.assertTrue(root.getChildren().size() == 7);
		Assert.assertTrue(root.getChildren().get(2) == child3);

		//Index < 0 or > size()
		root.addChild(child4, -1);
		root.addChild(child4, 10);
	}

	//Possible input:
	//A valid child
	//An invalid child
	@Test
	public void removeChild() {
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
		Assert.assertTrue(root.getChildren().size() == 2);
		Assert.assertEquals(root.getChildren().get(0), child1);
		Assert.assertEquals(root.getChildren().get(1), child3);
		Assert.assertTrue(child2.isRoot());

		//An invalid child
		root.removeChild(other);
		Assert.assertTrue(root.getChildren().size() == 2);
		Assert.assertEquals(root.getChildren().get(0), child1);
		Assert.assertEquals(root.getChildren().get(1), child3);
	}

	//Possible input:
	//None
	@Test
	public void clear() {
		XMLElement root = XMLElement.createRoot("p");
		XMLElement child = XMLElement.createRoot("q");
		root.addChild(child);
		root.addChild(XMLElement.createRoot("q"));
		root.addChild(XMLElement.createRoot("q"));
		root.addChild(XMLElement.createRoot("q"));
		root.addChild(XMLElement.createRoot("q"));

		Assert.assertTrue(root.getChildren().size() == 5);
		root.clear();
		Assert.assertTrue(root.getChildren().isEmpty());
		Assert.assertTrue(child.isRoot());
	}

	//Possible input:
	//A tag in the immediate children
	//A tag in the deeper children
	//A nonexisting tag
	@Test
	public void getChildrenByTagDeep() {
		//A tag in the immediate children
		List<XMLElement> children = tree.getChildrenByTagDeep("head");
		Assert.assertTrue(children.size() == 1);
		Assert.assertEquals(children.get(0), head);

		//A tag in the deeper children
		List<XMLElement> deepChildren = tree.getChildrenByTagDeep("div");
		Assert.assertTrue(deepChildren.size() == 3);
		Assert.assertTrue(deepChildren.contains(div1));
		Assert.assertTrue(deepChildren.contains(div2));
		Assert.assertTrue(deepChildren.contains(div3));

		//A nonexisting tag
		List<XMLElement> noChildren = tree.getChildrenByTagDeep("foo");
		Assert.assertTrue(noChildren.isEmpty());
	}

	//Possible input:
	//An id among the immediate children
	//An id among the deeper children
	//A nonexisting id
	@Test
	public void getChildByID() {
		//An id among the immediate children
		XMLElement immediateChild = tree.getChildByID("sc");
		Assert.assertEquals(immediateChild, script);

		//An id among the deeper children
		XMLElement deepChild1 = tree.getChildByID("p1");
		Assert.assertEquals(deepChild1, p);

		XMLElement deepChild2 = tree.getChildByID("div1");
		Assert.assertEquals(deepChild2, div1);

		//A nonexisting id
		XMLElement noChild = tree.getChildByID("bar");
		Assert.assertNull(noChild);
	}

	//Possible input:
	//A class already on the element
	//A new class
	@Test
	public void addClass() {
		XMLElement element = XMLElement.createRoot("p");

		//A new class
		element.addClass("foo");
		Assert.assertTrue(element.getClasses().length == 1);
		Assert.assertEquals("foo", element.getClasses()[0]);

		element.addClass("bar");
		Assert.assertTrue(element.getClasses().length == 2);
		Assert.assertEquals("foo", element.getClasses()[0]);
		Assert.assertEquals("bar", element.getClasses()[1]);

		//A class already on the element
		element.addClass("bar");
		Assert.assertTrue(element.getClasses().length == 2);
		Assert.assertEquals("foo", element.getClasses()[0]);
		Assert.assertEquals("bar", element.getClasses()[1]);
	}

	//Possible input:
	//A class on the element
	//A class not on the element
	@Test
	public void removeClass() {
		XMLElement element = XMLElement.createRoot("p");
		element.addClass("foo");
		element.addClass("bar");

		//A class not on the element
		element.removeClass("apples");
		Assert.assertTrue(element.getClasses().length == 2);
		Assert.assertEquals("foo", element.getClasses()[0]);
		Assert.assertEquals("bar", element.getClasses()[1]);

		//A class on the element
		element.removeClass("foo");
		Assert.assertTrue(element.getClasses().length == 1);
		Assert.assertEquals("bar", element.getClasses()[0]);
	}

	//Possible input:
	//A null name and a valid value
	//A null name and a null or empty value
	//A name of a nonexsiting attribute and a valid value
	//A name of a nonexsiting attribute and a null or empty value
	//A name of an existing attribute and a valid value
	//A name of an existing attribute and a null or empty value
	@Test
	public void setAttribute() {
		Map<String, String> attributes = new HashMap<>();
		attributes.put("a1", "v1");
		attributes.put("a2", "v2");
		XMLElement element = XMLElement.createRoot("p", "text", attributes);

		//A null name and a valid value
		element.setAttribute(null, "bar");
		Assert.assertNull(element.getAttribute(null));

		//A null name and a null or empty value
		element.setAttribute(null, "");
		Assert.assertNull(element.getAttribute(null));

		//A name of a nonexsiting attribute and a valid value
		element.setAttribute("a3", "bar");
		Assert.assertEquals("bar", element.getAttribute("a3"));

		//A name of a nonexsiting attribute and a null or empty value
		element.setAttribute("a4", "");
		Assert.assertNull(element.getAttribute("a4"));

		//A name of an existing attribute and a valid value
		element.setAttribute("a1", "foo");
		Assert.assertEquals("foo", element.getAttribute("a1"));

		//A name of an existing attribute and a null or empty value
		element.setAttribute("a2", "");
		Assert.assertNull(element.getAttribute("a2"));
	}
}