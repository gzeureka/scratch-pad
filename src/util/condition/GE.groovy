package util.condition;

public class GE extends BinaryNode {

	@Override
	protected String getOperator() {
		return Condition.GE;
	}

	public GE(Item left, Item right) {
		super(left, right);
	}

	public GE(Item left, String right) {
		super(left, right);
	}

	public GE(String left, Item right) {
		super(left, right);
	}

	public GE(String left, String right) {
		super(left, right);
	}

	public GE(Item left, Item right, String label) {
		super(left, right, label);
	}

	public GE(Item left, String right, String label) {
		super(left, right, label);
	}

	public GE(String left, Item right, String label) {
		super(left, right, label);
	}

	public GE(String left, String right, String label) {
		super(left, right, label);
	}

}
