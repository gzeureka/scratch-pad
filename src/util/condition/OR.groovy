package util.condition;

import java.util.Collection;

public class OR extends JointNode {

	@Override
	String getJointType() {
		return Condition.OR;
	}

	public OR() {
		super();
	}

	public OR(Collection<Condition> nodes) {
		super(nodes);
	}

	public OR(String label) {
		super(label);
	}

	public OR(Collection<Condition> nodes, String label) {
		super(nodes, label);
	}

}
