package util.condition;

public abstract class Item extends Condition {

	public Item() {
	}

	public Item(String label) {
		super(label);
	}

	public abstract String toString(String operator, Item item);

	public abstract String parseFieldMap(String operator, Item item,
			FieldMap fieldMap);
}
