package cms.business;

import pim.business.Product;
import pim.business.Tag;
import shared.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The manager for loading, saving and editing dynamic pages.
 *
 * @author Kasper
 */
class PageManager {

	private final CMSPersistenceFacade persistence;
	private Template activeTemplate;
	private DynamicPage activePage;

	/**
	 * Constructs a new page manager.
	 *
	 * @param persistence the persistence facade
	 */
	public PageManager(CMSPersistenceFacade persistence) {
		this.persistence = persistence;
	}

	/**
	 * Get the ids of all products referenced in the specified page.
	 *
	 * @param pageID the id of the page to get product ids from
	 * @return the product ids in the specified page, may be empty
	 * @throws IOException if there was an error loading the page
	 */
	public List<Integer> getProducIDsFromPage(int pageID) throws IOException {
		ArrayList<Integer> listOfIDs = new ArrayList<>();
		Template template = persistence.getTemplateForPage(pageID);
		if (template != null) {
			DynamicPage page = persistence.getPage(pageID);
			if (page != null) {
				String html = template.enrichPage(page).toString();
				Pattern pattern = Pattern.compile("(\\[@ref=)([\\w]+)");
				Matcher matcher = pattern.matcher(html);
				while (matcher.find()) {
					listOfIDs.add(Integer.parseInt(matcher.group(2)));
				}
			}
		}
		return listOfIDs;
	}

	/**
	 * Construct the page with the specified id and make it ready for web use.
	 *
	 * @param pageID   the id of the page to get
	 * @param products the products to enrich the page with
	 * @return the html representation of the page, or null if no such page was found
	 * @throws IOException if there was an error loading the page
	 */
	public String constructPage(int pageID, Map<Integer, Product> products) throws IOException {
		//Read the page template
		Template template = persistence.getTemplateForPage(pageID);
		if (template != null) {

			//If this succeeded, read the page content
			DynamicPage page = persistence.getPage(pageID);
			if (page != null) {
				String html = template.enrichPage(page).toString();

				if (template.getType() == CMS.PageType.PRODUCT_PAGE) {
					return constructProductPage(html, products);
				} else if(template.getType() == CMS.PageType.LANDING_PAGE) {
					return constructLandingPage(html, products);
				} else {
					return constructRegularPage(html, products);
				}
			}
		}

		return null;
	}

	public String constructLandingPage(String baseHTML, Map<Integer, Product> popularProducts){
		for(Product product: popularProducts.values()){
			baseHTML = baseHTML.replaceFirst("(\\[@ref=)[?]([\\s\\w=]+])", "$1" + String.valueOf(product.getID()) + "$2");
		}
		return constructRegularPage(baseHTML, popularProducts);
	}

	/**
	 * Construct a regular page (not a product page) with the specified products.
	 *
	 * @param baseHTML the plain html of the page
	 * @param products the products to enrich the page with
	 * @return the html representation of the page, or null if no such page was found
	 */
	private String constructRegularPage(String baseHTML, Map<Integer, Product> products) {
		//Compile page links
		baseHTML = baseHTML.replaceAll("(\\[@link=(\\w+)])", "<a href=\"$2\">");
		baseHTML = baseHTML.replaceAll("(\\[@link])", "</a>");
		try {
			//Compile product references
			//Matcher for pulling out the reference ids and types
			Matcher refMatcher = Pattern.compile("(\\[@ref=)([\\w]+)( type=)([\\w]+)(])").matcher(baseHTML);
			//Loop through all matches
			while (refMatcher.find()) {
				int productID = Integer.parseInt(refMatcher.group(2));
				CMS.ReferenceType refType = CMS.ReferenceType.valueOf(refMatcher.group(4));
				Product product = products.get(productID);
				String replacement = "!REFERENCE ERROR!";

				//Generate the string to insert into the reference
				if (product != null) {
					switch (refType) {
						case NAME:
							replacement = product.getName();
							break;
						case PRICE:
							replacement = String.valueOf(product.getPrice());
							break;
						case IMAGE:
							StringBuilder builderImg = new StringBuilder();
							for (Image img: product.getImages()) {
								String imageData = encodeToByte64(img.getImage());
								builderImg.append("<img src=\"").append(imageData).append("\"/>");
							}
							replacement = builderImg.toString();
							break;
						case DESCRIPTION:
							replacement = product.getDescription();
							break;
						case TAGS:
							Set<Tag> tags = product.getTags();
							StringBuilder builder = new StringBuilder();
							for (Tag t : tags) {
								builder.append(t.toString());
							}
							replacement = builder.toString();
							break;
					}
				}

				//Inform matcher of the new html string
				baseHTML = refMatcher.replaceFirst(replacement);
				refMatcher.reset(baseHTML);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return baseHTML;
	}

	/**
	 * Construct a product page for the product in the map. It is expected that this map contains exactly one product.
	 *
	 * @param baseHTML the plain html of the page
	 * @param products a map containing only the product to make a product page for
	 * @return the html representation of the page, or null if no such page was found
	 */
	private String constructProductPage(String baseHTML, Map<Integer, Product> products) {
		//Only valid if exactly one product was specified
		if (products.values().size() == 1) {
			Product product = products.values().toArray(new Product[0])[0];

			baseHTML = baseHTML.replaceAll("(\\[@ref=)[?]([\\s\\w=]+])", "$1" + String.valueOf(product.getID()) + "$2");
			return constructRegularPage(baseHTML, products);
		}

		return null;
	}

	/**
	 * Create a new page with the specified name, page type, and template. This page will be set as active, discarding
	 * current, unsaved data.
	 *
	 * @param name       the name of the page to create
	 * @param pageType   the type of page
	 * @param templateID the id of the template to use
	 * @return the representation of the newly loaded page, or null if no such page exists
	 * @throws IllegalArgumentException if the specified template does not support the specified page type, or if the
	 *                                  template is unknown
	 */
	public XMLElement createNewPage(String name, CMS.PageType pageType, int templateID) {
		try {
			//Read template and ensure compatibility
			Template template = persistence.getTemplate(templateID);
			if (template.getType() != pageType)
				throw new IllegalArgumentException("The specified template with id " + templateID + " does not support the page type " + pageType);

			//Construct a new dynamic page with no id (created upon saving in database), and the default content of the
			//template
			Map<String, String> defaultContent = new HashMap<>();
			for (String id : template.getElementIDs()) {
				//We must wrap the default content in a new div with a new id
				XMLElement content = XMLElement.createRoot("div");
				content.setID(id + 'c');
				content.addChildren(template.getDefaultContentForElement(id));
				defaultContent.put(id, content.toString());
			}

			activePage = new DynamicPageImpl(-1, name, defaultContent);
			activeTemplate = template;
			return template.enrichPage(activePage);
		} catch (IOException e) {
			throw new IllegalArgumentException("Unable to read template with id " + templateID);
		}
	}

	/**
	 * Start editing the page with the specified id. This will load the page information from the persistence layer and
	 * activate the loaded page. If no such page exists, this method will deactivate the current page regardless, losing
	 * all unsaved data.
	 *
	 * @param id the id of the page to edit
	 * @return the representation of the newly loaded page, or null if no such page exists
	 * @throws IOException if there was an error loading the page
	 */
	public XMLElement editPage(int id) throws IOException {
		//Read the page template
		Template template = persistence.getTemplateForPage(id);
		if (template != null) {

			//If this succeeded, read the page content
			DynamicPage page = persistence.getPage(id);
			if (page != null) {

				//If this succeeded too, activate the page and return the xml representation
				this.activeTemplate = template;
				this.activePage = page;

				return template.enrichPage(page);
			}
		}

		//Something went wrong, so deactivate current page and template and return null
		this.activeTemplate = null;
		this.activePage = null;
		return null;
	}

	/**
	 * Get the text of the specified element in the currently active page. If the element does not contain text, null
	 * will be returned.
	 *
	 * @param id the id of the element
	 * @return the text of the element, or null if it contains no text
	 * @throws IllegalStateException if there is no active page
	 */
	public String getElementText(String id) {
		if (activePage == null)
			throw new IllegalStateException("No active page to get text from!");

		try {
			return activePage.getTextFromElement(id);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	/**
	 * Set the text of the element with the specified id in the currently active page.
	 *
	 * @param id   the id of the element
	 * @param text the new text to set
	 * @return the representation of the active page after the operation
	 * @throws IllegalStateException if there is no active page
	 */
	public XMLElement editElementText(String id, String text) {
		if (activePage == null)
			throw new IllegalStateException("No active page to edit text in!");

		try {
			activePage.setText(id, text);
		} catch (IllegalArgumentException e) {
			//We simply ignore this
		}

		return activeTemplate.enrichPage(activePage);
	}

	/**
	 * Insert the specified html markup at the location specified by the {@link DocumentMarker} in the currently active
	 * page.
	 *
	 * @param marker the location to insert the markup into
	 * @param html   the markup to insert
	 * @return the representation of the active page after the operation
	 * @throws IllegalStateException if there is no active page
	 */
	public XMLElement insertHTML(DocumentMarker marker, String html) {
		if (activePage == null)
			throw new IllegalStateException("No active page to insert into!");

		activePage.insertHTML(marker, html);

		return activeTemplate.enrichPage(activePage);
	}

	/**
	 * Insert the specified text at the location specified by the {@link DocumentMarker} in the currently active page.
	 *
	 * @param marker the location to insert the text into
	 * @param text   the text to insert
	 * @return the representation of the active page after the operation
	 * @throws IllegalStateException if there is no active page
	 */
	public XMLElement insertText(DocumentMarker marker, String text) {
		if (activePage == null)
			throw new IllegalStateException("No active page to insert into!");

		activePage.insertText(marker, text);

		return activeTemplate.enrichPage(activePage);
	}

	/**
	 * Insert the specified image at the location specified by the {@link DocumentMarker} in the currently active page.
	 *
	 * @param marker the location to insert the image into
	 * @param image  the image to insert
	 * @return the representation of the active page after the operation
	 * @throws IllegalStateException if there is no active page
	 */
	public XMLElement insertImage(DocumentMarker marker, Image image) {
		if (activePage == null)
			throw new IllegalStateException("No active page to insert into!");

		//Attempt to transform the buffered image to a format supported in the web view
		try {
			activePage.insertImage(marker, encodeToByte64(image.getImage()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return activeTemplate.enrichPage(activePage);
	}

	/**
	 * Create a link to the page with the specified id. The link will be inserted at the location specified by the
	 * {@link DocumentMarker} in the currently active page.
	 *
	 * @param marker the location to insert the link into
	 * @param pageID the id of the page to link to
	 * @return the representation of the active page after the operation
	 * @throws IllegalStateException if there is no active page
	 */
	public XMLElement createLink(DocumentMarker marker, int pageID) {
		if (activePage == null)
			throw new IllegalStateException("No active page to remove from!");

		activePage.setTextLink(marker, pageID);

		return activeTemplate.enrichPage(activePage);
	}

	/**
	 * Create a reference of the specified type to the specified product. The reference will be inserted at the location
	 * specified by the {@link DocumentMarker} in the currently active page.
	 *
	 * @param marker    the location to insert the reference into
	 * @param productID the id of the product to reference
	 * @param type      the type of reference to create
	 * @return the representation of the active page after the operation
	 * @throws IllegalStateException if there is no active page
	 */
	public XMLElement createReference(DocumentMarker marker, int productID, CMS.ReferenceType type) {
		if (activePage == null)
			throw new IllegalStateException("No active page to remove from!");

		activePage.setReference(marker, productID, type.toString());

		return activeTemplate.enrichPage(activePage);
	}

	/**
	 * Remove the element at the location specified by the {@link DocumentMarker} in the currently active page.
	 *
	 * @param marker the location to remove the element from
	 * @return the representation of the active page after the operation
	 * @throws IllegalStateException if there is no active page
	 */
	public XMLElement removeElement(DocumentMarker marker) {
		if (activePage == null)
			throw new IllegalStateException("No active page to remove from!");

		activePage.removeElement(marker);

		return activeTemplate.enrichPage(activePage);
	}

	/**
	 * Save the currently active page. If no page is active, this method will do nothing.
	 *
	 * @throws IOException if the operation failed
	 */
	public void savePage() throws IOException {
		if (activePage != null && activeTemplate != null) {
			persistence.savePage(activePage, activeTemplate);
		}
	}

	/**
	 * Encode the specified image to a format supported by the web view.
	 *
	 * @param img the image to encode
	 * @return the encoded image as a string
	 */
	private String encodeToByte64(BufferedImage img) throws IOException {
		//Attempt to transform the buffered image to a format supported in the web view
		//Source: http://stackoverflow.com/questions/22984430/javafx2-webview-and-in-memory-images#answer-37215917
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ImageIO.write(img, "PNG", output);
		return "data:image/png;base64," + Base64.getMimeEncoder().encodeToString(output.toByteArray());
	}
}
