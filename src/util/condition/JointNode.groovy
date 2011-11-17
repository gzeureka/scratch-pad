package util.condition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import util.Utils;

public abstract class JointNode extends Condition {
	protected ArrayList<Condition> nodes;
	
	boolean isEmpty(){
		return !nodes
	}

	public JointNode append(Condition node) {
		if (node != null)
			nodes.add(node);
		return this;
	}

	public JointNode() {
		this.nodes = new ArrayList<Condition>();
	}

	public JointNode(String label) {
		super(label);
	}

	public JointNode(Collection<Condition> nodes) {
		this.nodes = new ArrayList<Condition>(nodes);
	}

	public JointNode(Collection<Condition> nodes, String label) {
		super(label);
		this.nodes = new ArrayList<Condition>(nodes);
	}

	@Override
	public String toString() {
		return parseFieldMap(null);
	}

	abstract String getJointType();

	@Override
	public String parseFieldMap(FieldMap fieldMap) {
		if (nodes.size() == 0)
			return "";
		StringBuilder sb = new StringBuilder();
		for (Condition node : nodes) {
			if (node == null || node.toString().length() == 0)
				continue;
			if (sb.length() > 0)
				sb.append(SPACE).append(getJointType()).append(SPACE);
			sb.append(node.parseFieldMap(fieldMap));
		}
		String ret = sb.toString();
		if (ret.length() > 0)
			ret = Utils.bracket(ret);
		return ret;
	}

	@Override
	protected List<Condition> findNodesWithLabel(String label,
			List<Condition> nodes) {
		super.findNodesWithLabel(label, nodes);
		for (Condition node : this.nodes) {
			node.findNodesWithLabel(label, nodes);
		}
		return nodes;
	}

	@Override
	public Condition remove(Condition node) {
		for(Condition aNode:nodes){
			aNode.remove(node);
		}
		nodes.remove(node);
		return this;
	}

	public JointNode substitute(Condition originalNode, Condition newNode) {
		int index = nodes.indexOf(originalNode);
		if (index == -1)
			return this;
		nodes.remove(index);
		nodes.add(index, newNode);
		return this;
	}

	public JointNode substitute(String label, Condition newNode) {
		Condition originalNode = this.findFirst(label);
		return substitute(originalNode, newNode);
	}
}
