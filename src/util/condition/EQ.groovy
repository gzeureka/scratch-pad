package util.condition;

public class EQ extends BinaryNode {

	@Override
	protected String getOperator() {
		return Condition.EQ;
	}

	public EQ(Item left, Item right) {
		super(left, right);
	}

	public EQ(Item left, String right) {
		super(left, right);
	}

	public EQ(String left, Item right) {
		super(left, right);
	}

	public EQ(String left, String right) {
		super(left, right);
	}

	public EQ(Item left, Item right, String label) {
		super(left, right, label);
	}

	public EQ(Item left, String right, String label) {
		super(left, right, label);
	}

	public EQ(String left, Item right, String label) {
		super(left, right, label);
	}

	public EQ(String left, String right, String label) {
		super(left, right, label);
	}

}
