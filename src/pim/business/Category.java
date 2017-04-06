package pim.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * Created by Kasper on 06-04-2017.
 */
public class Category implements Iterator<Attribute>{
    
    private String categoryName;
    private List<Attribute> categoryAttributes;
    private List<Product> products;
    
    public Category(String categoryName) {
        this.categoryName = categoryName;
        this.categoryAttributes = new ArrayList<>();
        this.products = new ArrayList<>();
    }
    
    // iterate through categoryAttributes to find the attribute?
    public void removeAttributeFromCategory(Attribute attribute) {
        categoryAttributes.remove(attribute);
    }
    
    public void addAttributeToCategory(Attribute attribute) {
        categoryAttributes.add(attribute);
    }
    
    public void addProductToCategory(Product product) {
        products.add(product);
    }
    
    /**
     * @return the categoryName
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * @return the categoryAttributes
     */
    public List<Attribute> getCategoryAttributes() {
        return categoryAttributes;
    }

    /**
     * @return the products
     */
    public List<Product> getProducts() {
        return products;
    }

    /**
     * @param categoryName the categoryName to set
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public boolean hasNext() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void remove() {
        Iterator.super.remove(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Attribute next() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
