package util.condition;

public class NOT extends Condition {
	private Condition node;

	public NOT(Condition node) {
		this.node = node;
	}

	public NOT(Condition node, String label) {
		super(label);
		this.node = node;
	}

	@Override
	public String toString() {
		return Condition.NOT + Condition.SPACE + node.toString();
	}

	@Override
	public String parseFieldMap(FieldMap fieldMap) {
		return Condition.NOT + Condition.SPACE + node.parseFieldMap(fieldMap);
	}

}
