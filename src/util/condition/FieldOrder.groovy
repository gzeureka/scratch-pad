package util.condition;

public class FieldOrder {

	protected String label;

	private String fieldName;

	private String order;

	public FieldOrder(String fieldName, Order order) {
		this.fieldName = fieldName;
		this.order = order.name();
	}

	public FieldOrder(String fieldName, Order order, String label) {
		this.fieldName = fieldName;
		this.order = order.name();
		this.label = label;
	}

	public static FieldOrder DescFieldOrder(String fieldName) {
		return new FieldOrder(fieldName, Order.DESC);
	}

	public static FieldOrder DescFieldOrder(String fieldName, String label) {
		return new FieldOrder(fieldName, Order.DESC, label);
	}

	public static FieldOrder AscFieldOrder(String fieldName) {
		return new FieldOrder(fieldName, Order.ASC);
	}

	public static FieldOrder AscFieldOrder(String fieldName, String label) {
		return new FieldOrder(fieldName, Order.ASC, label);
	}

	public String parseFieldMap(FieldMap fieldMap) {
		return Field.StringField(fieldName).parseFieldMap(fieldMap)	+ 
			Condition.SPACE + order;
	}

	@Override
	public String toString() {
		return parseFieldMap(null);
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
