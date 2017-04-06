package pim.business;

import java.util.ArrayList;



public class Product {
	private String name;
	private int id;
	private Category category;
	private ArrayList<Integer> images;

	public Product(String name){
		this.name = name;
		images = new ArrayList<>();
		category = null;
	}

	public Product(String name, Category category){
		this.name = name;
		this.category = category;
		images = new ArrayList<>();
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

	public Category getCategory() {
		return category;
	}

	public ArrayList<Integer> getImages() {
		return images;
	}
}
