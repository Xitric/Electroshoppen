package pim.business;

import java.util.Iterator;
import java.util.List;
import java.util.Iterator;

/**
 * Created by Kasper on 06-04-2017.
 */
public class Category implements Iterator<Attribute>{
    
    private String categoryName;
    private List<Attribute> categoryAttributes;
    private List<Product> products;
    
    public Category (String categoryName) {
        this.categoryName = categoryName;
    }
    
    public Category(String categoryName, List<Attribute> categoryAttributes, 
            List<Product> products) {
        this.categoryName = categoryName;
        this.categoryAttributes = categoryAttributes;
        this.products = products;
    }
    
    // iterate through categoryAttributes to find the attribute?
    public void removeAttribute(Attribute attribute) {
        categoryAttributes.remove(attribute);
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
