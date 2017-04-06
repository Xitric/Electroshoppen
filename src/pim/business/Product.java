package pim.business;

import java.util.ArrayList;



public class Product {
	private String name;
	private int id;
	private ArrayList<Category> categories;
	private ArrayList<Integer> images;

	public Product(String name){
		this.name = name;
		images = new ArrayList<>();
		categories = new ArrayList<>();
	}

	public Product(String name, Category category){
		categories = new ArrayList<>();
		images = new ArrayList<>();
		this.name = name;
		categories.add(category);
	}

	public Product(String name, Category category, Integer image){
		categories = new ArrayList<>();
		images = new ArrayList<>();
		this.name = name;
		categories.add(category);
		images.add(image);
	}

	public void addImage(int image){
		images.add(image);
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
