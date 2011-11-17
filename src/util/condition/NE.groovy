package util.condition;

public class NE extends BinaryNode {

	@Override
	protected String getOperator() {
		return Condition.NE;
	}

	public NE(Item left, Item right) {
		super(left, right);
	}

	public NE(Item left, String right) {
		super(left, right);
	}

	public NE(String left, Item right) {
		super(left, right);
	}

	public NE(String left, String right) {
		super(left, right);
	}

	public NE(Item left, Item right, String label) {
		super(left, right, label);
	}

	public NE(Item left, String right, String label) {
		super(left, right, label);
	}

	public NE(String left, Item right, String label) {
		super(left, right, label);
	}

	public NE(String left, String right, String label) {
		super(left, right, label);
	}

}
