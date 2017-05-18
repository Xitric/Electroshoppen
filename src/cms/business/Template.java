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
				"  <div>Title</div>\n" +
				"</div>\n" +
				"<div class=\"wrapper nonselectable\">\n" +
				"  <div>1 Column</div>\n" +
				"</div>\n" +
				"<div class=\"wrapper nonselectable\">\n" +
				"  <div>2 Columns</div>\n" +
				"  <div>2 Columns</div>\n" +
				"</div>\n" +
				"<div class=\"wrapper nonselectable\">\n" +
				"  <div>3 Columns</div>\n" +
				"  <div>3 Columns</div>\n" +
				"  <div>3 Columns</div>\n" +
				"</div>\n" +
				"<div class=\"wrapper nonselectable\">\n" +
				"  <div>Footer</div>\n" +
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
				"    <div>Product name</div>\n" +
				"  </div>\n" +
				"  <div class=\"wrapper nonselectable\" style=\"min-height:100px;\">\n" +
				"    <div style=\"min-width:60%;\">Image</div>\n" +
				"    <div>Price/purchase</div>\n" +
				"  </div>\n" +
				"  <div class=\"wrapper nonselectable\">\n" +
				"    <div>Product information</div>\n" +
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
				"    <div>Guidenavn</div>\n" +
				"  </div>\n" +
				"  <div class=\"wrapper nonselectable\">\n" +
				"    <div>Image</div>\n" +
				"  </div>\n" +
				"  <div class=\"wrapper nonselectable\" style=\"min-height:100px;\">\n" +
				"    <div style=\"min-width:60%;\">Guidetekst</div>\n" +
				"    <div>Produktbeskrivelse</div>\n" +
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
				"    <div>Header</div>\n" +
				"  </div>\n" +
				"  <div class=\"wrapper nonselectable\">\n" +
				"    <div>Produkter</div>\n" +
				"  </div>\n" +
				"</body>\n" +
				"</html>");
		return landingPage;
	}

	public static void main(String[] args) {
	}
}
