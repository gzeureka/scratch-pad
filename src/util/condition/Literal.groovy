package util.condition;

public class Literal extends Item {

	private String value;

	public static String escape(String str) {
		return str?.replace('\'', '\'\'');
	}
	
	public Literal(String value, String label) {
		super(label);
		this.value = value;
	}

	public Literal(String value) {
		super();
		this.value = value;
	}

	@Override
	public String toString() {
		return escape(value)
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString(String operator, Item item) {
		if (item instanceof Field)
			return item.toString(operator, this);

		return this.toString() + operator + item.toString();
	}

	@Override
	public String parseFieldMap(String operator, Item item, FieldMap fieldMap) {
		if (item instanceof Field)
			return item.parseFieldMap(operator, this, fieldMap);

		return toString(operator, item);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null || obj.class!=Literal.class)
			return false
		return this.value==obj.value
	}

}
