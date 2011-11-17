package util.condition;

import java.util.ArrayList;
import java.util.List;

public abstract class Condition {
	protected static final String SPACE = " ";
	
	protected static final String COMMA = ",";
	
	// unary
	protected static final String IS_NULL = " IS NULL";
	
	protected static final String IS_NOT_NULL = " IS NOT NULL";
	
	// relational binary
	protected static final String EQ = "=";
	
	protected static final String NE = "<>";
	
	protected static final String LT = "<";
	
	protected static final String GT = ">";
	
	protected static final String LE = "<=";
	
	protected static final String GE = ">=";
	
	protected static final String LIKE = " LIKE ";
	
	protected static final String NOT_LIKE = " NOT LIKE ";
	
	protected static final String IN = " IN ";
	
	protected static final String NOT_IN = " NOT IN ";
	
	protected static final String BETWEEN = " BETWEEN ";
	
	protected static final String NOT_BETWEEN = " NOT BETWEEN ";
	
	// logical
	protected static final String AND = "AND";
	
	protected static final String OR = "OR";
	
	protected static final String NOT = "NOT";
	
	public String parseFieldMap(FieldMap fieldMap) {
		return this.toString();
	}
	
	public AND and(Condition condition) {
		return AND(condition);
	}
	
	public OR or(Condition condition) {
		return OR(condition);
	}
	
	public AND AND(Condition condition) {
		if(this instanceof AND){
			this.append(condition)
			return this
		}
		else
			return C.AND(this, condition)
	}
	
	public OR OR(Condition condition) {
		if(this instanceof OR) {
			this.append(condition)
			return this
		}
		else
			return C.OR(this, condition)
	}
	
	public NOT not() {
		return C.NOT(this)
	}
	
	public NOT NOT() {
		return C.NOT(this)
	}
	
	def methodMissing(String name, def args) {
		Condition condition = ConditionBuilder.build(this, name, args)
		if(!condition)
			throw new MissingMethodException(name, this.class, args)
		return condition
	}
	
	protected String label;
	
	public Condition() {
		
	}
	
	public Condition(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
	
	public Condition setLabel(String label) {
		this.label = label;
		return this;
	}
	
	public List<Condition> find(String label) {
		List<Condition> nodes = new ArrayList<Condition>();
		findNodesWithLabel(label, nodes);
		return nodes;
	}
	
	public Condition findFirst(String label) {
		List<Condition> nodes = new ArrayList<Condition>();
		findNodesWithLabel(label, nodes);
		if (nodes.isEmpty())
			return null;
		return nodes.get(0);
	}
	
	protected List<Condition> findNodesWithLabel(String label,
	List<Condition> nodes) {
		if (this.label == label
		|| (this.label != null && this.label.equals(label)))
			nodes.add(this);
		return nodes;
	}
	
	public Condition remove(Condition node) {
		return this;
	}
	
	public Condition removeFirstNodeWithLabel(String label) {
		Condition node = this.findFirst(label);
		this.remove(node);
		return node;
	}
	
}
