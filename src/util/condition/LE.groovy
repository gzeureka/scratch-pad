package util.condition;

public class LE extends BinaryNode {

	@Override
	protected String getOperator() {
		return Condition.LE;
	}

	public LE(Item left, Item right) {
		super(left, right);
	}

	public LE(Item left, String right) {
		super(left, right);
	}

	public LE(String left, Item right) {
		super(left, right);
	}

	public LE(String left, String right) {
		super(left, right);
	}

	public LE(Item left, Item right, String label) {
		super(left, right, label);
	}

	public LE(Item left, String right, String label) {
		super(left, right, label);
	}

	public LE(String left, Item right, String label) {
		super(left, right, label);
	}

	public LE(String left, String right, String label) {
		super(left, right, label);
	}

}
