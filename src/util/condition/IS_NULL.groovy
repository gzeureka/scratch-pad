package util.condition;

public class IS_NULL extends UnaryNode {

	public IS_NULL(Item item) {
		super(item);
	}

	public IS_NULL(Item item, String label) {
		super(item, label);
	}

	@Override
	public String toString() {
		return item.toString() + Condition.IS_NULL;
	}

	@Override
	public String parseFieldMap(FieldMap fieldMap) {
		return item.parseFieldMap(fieldMap) + Condition.IS_NULL;
	}

}
