package util.condition;

public class GT extends BinaryNode {

	@Override
	protected String getOperator() {
		return Condition.GT;
	}

	public GT(Item left, Item right) {
		super(left, right);
	}

	public GT(Item left, String right) {
		super(left, right);
	}

	public GT(String left, Item right) {
		super(left, right);
	}

	public GT(String left, String right) {
		super(left, right);
	}

	public GT(Item left, Item right, String label) {
		super(left, right, label);
	}

	public GT(Item left, String right, String label) {
		super(left, right, label);
	}

	public GT(String left, Item right, String label) {
		super(left, right, label);
	}

	public GT(String left, String right, String label) {
		super(left, right, label);
	}

}
