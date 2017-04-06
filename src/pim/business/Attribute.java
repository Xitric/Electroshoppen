package pim.business;

import java.util.List;

/**
 * Created by Kasper on 06-04-2017.
 */
public class Attribute {

	private String name;
	private List<Object> legalValues;

	public Attribute(String name) {
		this.name = name;
	}

	public Attribute(String name, List<Object> legalValues) {
		this.name = name;
		this.legalValues = legalValues;
	}

	public AttributeValue createValue(Object o) {
		if (legalValues == null || legalValues.contains(o)) {
			return new AttributeValue(this, o);
		}
		throw new IllegalArgumentException("Value not allowed");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public class AttributeValue {

		private Attribute parent;
		private Object value;

		private AttributeValue(Attribute parent, Object value) {
			this.parent = parent;
			this.value = value;
		}
	}
}
