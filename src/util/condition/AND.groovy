package util.condition;

import java.util.Collection;

public class AND extends JointNode {

	public AND() {
		super();
	}

	public AND(String label) {
		super(label);
	}

	public AND(Collection<Condition> nodes) {
		super(nodes);
	}

	public AND(Collection<Condition> nodes, String label) {
		super(nodes, label);
	}

	@Override
	String getJointType() {
		return Condition.AND;
	}

}
