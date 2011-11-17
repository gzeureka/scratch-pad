package util.condition;

public class LIKE extends BinaryNode {

	@Override
	protected String getOperator() {
		return Condition.LIKE;
	}

	public LIKE(Item left, Item right) {
		super(left, right);
	}

	public LIKE(Item left, String right) {
		super(left, right);
	}

	public LIKE(String left, Item right) {
		super(left, right);
	}

	public LIKE(String left, String right) {
		super(left, right);
	}

	public LIKE(Item left, Item right, String label) {
		super(left, right, label);
	}

	public LIKE(Item left, String right, String label) {
		super(left, right, label);
	}

	public LIKE(String left, Item right, String label) {
		super(left, right, label);
	}

	public LIKE(String left, String right, String label) {
		super(left, right, label);
	}

}
