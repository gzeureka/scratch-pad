package util.condition;

public class LT extends BinaryNode {

	@Override
	protected String getOperator() {
		return Condition.LT;
	}

	public LT(Item left, Item right) {
		super(left, right);
	}

	public LT(Item left, String right) {
		super(left, right);
	}

	public LT(String left, Item right) {
		super(left, right);
	}

	public LT(String left, String right) {
		super(left, right);
	}

	public LT(Item left, Item right, String label) {
		super(left, right, label);
	}

	public LT(Item left, String right, String label) {
		super(left, right, label);
	}

	public LT(String left, Item right, String label) {
		super(left, right, label);
	}

	public LT(String left, String right, String label) {
		super(left, right, label);
	}

}
