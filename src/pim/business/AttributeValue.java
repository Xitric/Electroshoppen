/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pim.business;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Emil
 */
public class AttributeValue extends Attribute {

    private final List<String> constraints;

    public AttributeValue(String name, String value) {
        super(name, value);
        constraints = new ArrayList<>();

    }

    public List<String> getConstraints() {
        return constraints;
    }

}
