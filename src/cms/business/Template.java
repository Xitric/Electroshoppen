package cms.business;

import com.sun.org.apache.xpath.internal.SourceTree;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Bruger on 18-05-2017.
 */
public class Template {
	private XMLElement template;

	public Template() {
		template = getProductTemplate();
	}

	public Set<String> getIds() {
		return getXMLId(template);
	}

	private Set<String> getXMLId(XMLElement element) {
		if (element.getID() != null) {
			return Collections.singleton(element.getID());
		} else {
			Set<String> idSet = new HashSet<>();
			for (XMLElement child : element.getChildren()) {
				idSet.addAll(getXMLId(child));
			}
			return idSet;
		}
	}

	public XMLElement getArticleTemplate() {
		XMLElement article = new XMLParser().parse("<html><head><title>Example</title>\n" +
				"<style>\n" +
				".wrapper {\n" +
				"  display: flex;\n" +
				"}\n" +
				".wrapper > div {\n" +
				"  font-size: 4vh;\n" +
				"  color: black;\n" +
				"  background: white;\n" +
				"  margin: .3em;\n" +
				"  padding: .3em;\n" +
				"  outline:2px #125688 solid;\n" +
				"  flex: 1;\n" +
				"}\n" +
				"</style>\n" +
				"</head><body class=\"nonselectable\">" +
				"<div class=\"wrapper nonselectable\">\n" +
				"  <div id = \"1\">Title</div>\n" +
				"</div>\n" +
				"<div class=\"wrapper nonselectable\">\n" +
				"  <div id = \"2\">1 Column</div>\n" +
				"</div>\n" +
				"<div class=\"wrapper nonselectable\">\n" +
				"  <div id = \"3\">2 Columns</div>\n" +
				"  <div id = \"4\">2 Columns</div>\n" +
				"</div>\n" +
				"<div class=\"wrapper nonselectable\">\n" +
				"  <div id = \"5\">3 Columns</div>\n" +
				"  <div id = \"6\">3 Columns</div>\n" +
				"  <div id = \"7\">3 Columns</div>\n" +
				"</div>\n" +
				"<div class=\"wrapper nonselectable\">\n" +
				"  <div id = \"8\">Footer</div>\n" +
				"</div></body></html>");
		return article;
	}

	public XMLElement getProductTemplate() {
		XMLElement product = new XMLParser().parse("<html>\n" +
				"<head>\n" +
				"  <style>\n" +
				"    .wrapper {\n" +
				"      display: flex;\n" +
				"    }\n" +
				"    .wrapper > div {\n" +
				"      font-size: 4vh;\n" +
				"      color: black;\n" +
				"      background: white;\n" +
				"      margin: .3em;\n" +
				"      padding: .3em;\n" +
				"      border-radius: 3px;\n" +
				"      outline:2px #125688 solid;\n" +
				"      flex: 1;\n" +
				"    }\n" +
				"  </style>\n" +
				"</head>\n" +
				"<body class=\"nonselectable\">\n" +
				"  <div class=\"wrapper nonselectable\">\n" +
				"    <div id = \"1\">Product name</div>\n" +
				"  </div>\n" +
				"  <div class=\"wrapper nonselectable\" style=\"min-height:100px;\">\n" +
				"    <div id = \"2\" style=\"min-width:60%;\">Image</div>\n" +
				"    <div id = \"3\">Price/purchase</div>\n" +
				"  </div>\n" +
				"  <div class=\"wrapper nonselectable\">\n" +
				"    <div id = \"4\">Product information</div>\n" +
				"  </div>\n" +
				"</body>\n" +
				"</html>");
		return product;
	}

	public XMLElement getGuideTemplate() {
		XMLElement template = new XMLParser().parse("<html>\n" +
				"<head>\n" +
				"  <style>\n" +
				"    .wrapper {\n" +
				"      display: flex;\n" +
				"    }\n" +
				"    .wrapper > div {\n" +
				"      font-size: 4vh;\n" +
				"      color: black;\n" +
				"      background: white;\n" +
				"      margin: .3em;\n" +
				"      padding: .3em;\n" +
				"      border-radius: 3px;\n" +
				"      outline:2px #125688 solid;\n" +
				"      flex: 1;\n" +
				"    }\n" +
				"  </style>\n" +
				"</head>\n" +
				"<body class=\"nonselectable\">\n" +
				"  <div class=\"wrapper nonselectable\">\n" +
				"    <div id = \"1\">Guidenavn</div>\n" +
				"  </div>\n" +
				"  <div class=\"wrapper nonselectable\">\n" +
				"    <div id = \"2\">Image</div>\n" +
				"  </div>\n" +
				"  <div class=\"wrapper nonselectable\" style=\"min-height:100px;\">\n" +
				"    <div id = \"3\" style=\"min-width:60%;\">Guidetekst</div>\n" +
				"    <div id = \"4\">Produktbeskrivelse</div>\n" +
				"  </div>\n" +
				"</body>\n" +
				"</html>");
		return template;
	}

	public XMLElement getLandingPageTemplate() {
		XMLElement landingPage = new XMLParser().parse("<html>\n" +
				"<head>\n" +
				"  <style>\n" +
				"    .wrapper {\n" +
				"      display: flex;\n" +
				"    }\n" +
				"    .wrapper > div {\n" +
				"      font-size: 4vh;\n" +
				"      color: black;\n" +
				"      background: white;\n" +
				"      margin: .3em;\n" +
				"      padding: .3em;\n" +
				"      border-radius: 3px;\n" +
				"      outline:2px #125688 solid;\n" +
				"      flex: 1;\n" +
				"    }\n" +
				"  </style>\n" +
				"</head>\n" +
				"<body class=\"nonselectable\">\n" +
				"  <div class=\"wrapper nonselectable\">\n" +
				"    <div id = \"1\">Header</div>\n" +
				"  </div>\n" +
				"  <div class=\"wrapper nonselectable\">\n" +
				"    <div id = \"2\" >Produkter</div>\n" +
				"  </div>\n" +
				"</body>\n" +
				"</html>");
		return landingPage;
	}


	public static void main(String[] args) {
		Template test = new Template();
		System.out.println(Arrays.toString(test.getIds().toArray()));
	}
}
