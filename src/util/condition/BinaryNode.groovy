package util.condition;

import java.util.List;

public abstract class BinaryNode extends Condition {

	protected Item left;

	protected Item right;

	protected abstract String getOperator();

	@Override
	public String toString() {
		return left.toString(getOperator(), right);
	}

	@Override
	public String parseFieldMap(FieldMap fieldMap) {
		return left.parseFieldMap(getOperator(), right, fieldMap);
	}

	public BinaryNode(Item left, Item right) {
		super();
		this.left = left;
		this.right = right;
	}

	public BinaryNode(String left, String right) {
		super();
		this.left = new Literal(left);
		this.right = new Literal(right);
	}

	public BinaryNode(Item left, String right) {
		super();
		this.left = left;
		this.right = new Literal(right);
	}

	public BinaryNode(String left, Item right) {
		super();
		this.left = new Literal(left);
		this.right = right;
	}

	public BinaryNode(Item left, Item right, String label) {
		super(label);
		this.left = left;
		this.right = right;
	}

	public BinaryNode(String left, String right, String label) {
		super(label);
		this.left = new Literal(left);
		this.right = new Literal(right);
	}

	public BinaryNode(Item left, String right, String label) {
		super(label);
		this.left = left;
		this.right = new Literal(right);
	}

	public BinaryNode(String left, Item right, String label) {
		super(label);
		this.left = new Literal(left);
		this.right = right;
	}

	@Override
	protected List<Condition> findNodesWithLabel(String label,
			List<Condition> nodes) {
		super.findNodesWithLabel(label, nodes);
		left.findNodesWithLabel(label, nodes);
		right.findNodesWithLabel(label, nodes);
		return nodes;
	}

	public Item getLeft() {
		return left;
	}

	public Item getRight() {
		return right;
	}

}
