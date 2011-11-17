package util.condition;

public class NOT_LIKE extends BinaryNode {

	public NOT_LIKE(Item left, Item right) {
		super(left, right);
	}

	public NOT_LIKE(Item left, Item right, String label) {
		super(left, right, label);
	}

	@Override
	protected String getOperator() {
		return Condition.NOT_LIKE;
	}

	public NOT_LIKE(Item left, String right) {
		super(left, right);
	}

	public NOT_LIKE(String left, Item right) {
		super(left, right);
	}

	public NOT_LIKE(String left, String right) {
		super(left, right);
	}

	public NOT_LIKE(Item left, String right, String label) {
		super(left, right, label);
	}

	public NOT_LIKE(String left, Item right, String label) {
		super(left, right, label);
	}

	public NOT_LIKE(String left, String right, String label) {
		super(left, right, label);
	}

}
