package util.condition;

import java.util.ArrayList;
import java.util.List;

public class FieldOrders {
	
	def propertyMissing(String name) {
		FieldOrders fieldOrders = FieldOrdersBuilder.build(this, name)
		if (fieldOrders==null)
			throw new MissingPropertyException(name, this.class)
		return fieldOrders
	}
		
	private List<FieldOrder> fieldOrders = new ArrayList<FieldOrder>();

	public FieldOrders(FieldOrder... fieldOrders) {
		for (FieldOrder fieldOrder : fieldOrders) {
			this.fieldOrders.add(fieldOrder);
		}
	}

	public FieldOrders add(FieldOrder fieldOrder) {
		this.fieldOrders.add(fieldOrder);
		return this;
	}

	public String parseFieldMap(FieldMap fieldMap) {
		String str = "";
		for (FieldOrder fieldOrder : fieldOrders) {
			if (str.length() > 0)
				str += Condition.COMMA + Condition.SPACE;
			str += fieldOrder.parseFieldMap(fieldMap);
		}
		return str;
	}

	@Override
	public String toString() {
		return parseFieldMap(null);
	}

	public List<FieldOrder> find(String label) {
		List<FieldOrder> nodes = new ArrayList<FieldOrder>();
		findNodesWithLabel(label, nodes);
		return nodes;
	}

	public FieldOrder findFirst(String label) {
		List<FieldOrder> nodes = new ArrayList<FieldOrder>();
		findNodesWithLabel(label, nodes);
		if (nodes.isEmpty())
			return null;
		return nodes.get(0);
	}

	protected List<FieldOrder> findNodesWithLabel(String label,
			List<FieldOrder> nodes) {
		for (FieldOrder fieldOrder : fieldOrders) {
			if (fieldOrder.label == label
					|| (fieldOrder.label != null && fieldOrder.label
							.equals(label)))
				nodes.add(fieldOrder);
		}
		return nodes;
	}

}
