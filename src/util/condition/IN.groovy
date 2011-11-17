package util.condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import util.GUtils;
import util.Utils;

public class IN extends Condition {
	
	private static ORACLE_SIZE_LIMIT_OF_LIST = 1000
	
	private Field field;

	private ArrayList<Literal> values = new ArrayList<Literal>();

	public IN(Field field) {
		super();
		this.field = field;
	}

	public IN(Field field, Collection<String> values) {
		super();
		this.field = field;
		this.add(values);
	}

	public IN(Field field, String label) {
		super(label);
		this.field = field;
	}

	public IN(Field field, Collection<String> values, String label) {
		super(label);
		this.field = field;
		this.add(values);
	}

	public IN add(Literal value) {
		return add(value.getValue());
	}

	public IN add(String value) {
		return add(Arrays.asList(value));
	}

	public IN add(Collection<String> values) {
		this.values = (this.values + values.collect{new Literal(it)}).asType(Set).asList()
		return this
	}

	public IN set(Collection<String> values) {
		this.values.clear();
		return this.add(values);
	}

	@Override
	public String parseFieldMap(FieldMap fieldMap) {
		if (values.size() == 0)
			return field.parseFieldMap(fieldMap) + Condition.IN + Utils.bracket("NULL");

		if (fieldMap == null)
			return toString();
		
		if (values.size <= ORACLE_SIZE_LIMIT_OF_LIST)
			return field.parseFieldMap(fieldMap) + Condition.IN	+ Utils.bracket(constructString());
		else
			return constructStringForOracle(fieldMap);
	}

	@Override
	public String toString() {
		if (values.size() == 0)
			return field.getName() + Condition.IN + Utils.bracket("NULL");
		
		if (values.size <= ORACLE_SIZE_LIMIT_OF_LIST)
			return field.getName() + Condition.IN + Utils.bracket(constructString());
		else
			return constructStringForOracle(null);
	}
	
	private String constructStringForOracle(FieldMap fieldMap) {
		// Oracle对list中元素个数有限制，将IN拆成多个IN的OR
		// ORA-01795: maximum number of expressions in a list is 1000
		def cond = Utils.divide(values, ORACLE_SIZE_LIMIT_OF_LIST).inject(C.OR(), { or, list ->
				or.append(field.IN(list*.toString()))
				return or
			})		
		return cond.parseFieldMap(fieldMap)
	}
	
	private String constructString(String name) {
		TYPE type=field.getType();
		switch(type){
		case TYPE.STRING:
		case TYPE.DATE:
		case TYPE.DATE_TIME:
			return Utils.quote(values.join(Utils.quote(Condition.COMMA)))
			break
		case TYPE.NUMBER:
			return Literal.escape(values*.value.join(Condition.COMMA))
			break
		default:
			assert false:"Unhandled field type ${type}"
			break
		}
	}

}
