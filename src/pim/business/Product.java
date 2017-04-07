package pim.business;

import java.util.ArrayList;

/**
 * @author Niels
 * @author Kasper
 */
public class Product {
	private String name;
	private int id;
	private ArrayList<Category> categories;
	private ArrayList<Integer> images;
	private ArrayList<Attribute> attributtes;

	public Product(String name) {
		this.name = name;
		images = new ArrayList<>();
		categories = new ArrayList<>();
		attributtes = new ArrayList<>();
	}

	public Product(String name, Category category) {
		categories = new ArrayList<>();
		images = new ArrayList<>();
		attributtes = new ArrayList<>();
		this.name = name;
		categories.add(category);
	}

	public Product(String name, Category category, Integer image) {
		categories = new ArrayList<>();
		images = new ArrayList<>();
		attributtes = new ArrayList<>();
		this.name = name;
		categories.add(category);
		images.add(image);
	}

	public void addImage(int image) {
		images.add(image);
	}

	public void addCategori(Category categori) {
		categories.add(categori);
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public ArrayList<Category> getCategories() {
		return categories;
	}

	public ArrayList<Integer> getImages() {
		return images;
	}
}
