package util.condition;

public class IS_NOT_NULL extends UnaryNode {

	public IS_NOT_NULL(Item item) {
		super(item);
	}

	public IS_NOT_NULL(Item item, String label) {
		super(item, label);
	}

	@Override
	public String toString() {
		return item.toString() + Condition.IS_NOT_NULL;
	}

	@Override
	public String parseFieldMap(FieldMap fieldMap) {
		return item.parseFieldMap(fieldMap) + Condition.IS_NOT_NULL;
	}

}
