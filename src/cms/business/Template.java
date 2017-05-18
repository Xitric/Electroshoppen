package cms.business;

import com.sun.org.apache.xpath.internal.SourceTree;

/**
 * Created by Bruger on 18-05-2017.
 */
public class Template {

	public Template(){

	}

	public XMLElement getArticleTemplate(){
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

	public XMLElement getProductTemplate(){
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
				"    <div> id = \"3\"Price/purchase</div>\n" +
				"  </div>\n" +
				"  <div class=\"wrapper nonselectable\">\n" +
				"    <div id = \"4\">Product information</div>\n" +
				"  </div>\n" +
				"</body>\n" +
				"</html>");
		return product;
	}

	public XMLElement getGuideTemplate(){
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

	public XMLElement getLandingPageTemplate(){
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
	}
}
