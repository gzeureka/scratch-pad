package util.condition;

public abstract class UnaryNode extends Condition {

	protected Item item;

	public UnaryNode(Item item) {
		super();
		this.item = item;
	}

	public UnaryNode(Item item, String label) {
		super(label);
		this.item = item;
	}

}
