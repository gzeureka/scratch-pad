package util.condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import util.GUtils;
import util.Utils;

public class NOT_IN extends Condition {

	private Field field;

	private ArrayList<Literal> values = new ArrayList<Literal>();

	public NOT_IN(Field field) {
		super();
		this.field = field;
	}

	public NOT_IN(Field field, Collection<String> values) {
		super();
		this.field = field;
		this.add(values);
	}

	public NOT_IN(Field field, String label) {
		super(label);
		this.field = field;
	}

	public NOT_IN(Field field, Collection<String> values, String label) {
		super(label);
		this.field = field;
		this.add(values);
	}

	public NOT_IN add(Literal value) {
		return add(value.getValue());
	}

	public NOT_IN add(String value) {
		return add(Arrays.asList(value));
	}

	public NOT_IN add(Collection<String> values) {
		Set<String> set = new HashSet<String>(GUtils.collect(this.values,
			{ Literal literal ->
				return literal.getValue();
			} as GUtils.Converter<Literal, String>));
		set.addAll(values);

		this.values.clear();
		this.values.addAll(GUtils.collect(set,
			{ String str ->
				return new Literal(str);
			} as GUtils.Converter<String, Literal>));
		return this;
	}

	public NOT_IN set(Collection<String> values) {
		this.values.clear();
		return this.add(values);
	}

	@Override
	public String parseFieldMap(FieldMap fieldMap) {
		if (values.size() == 0)
			return field.parseFieldMap(fieldMap) + Condition.NOT_IN + Utils.bracket("NULL");

		if (fieldMap == null)
			return toString();

		return field.parseFieldMap(fieldMap) + Condition.NOT_IN	+ Utils.bracket(constructString());
	}

	@Override
	public String toString() {
		if (values.size() == 0)
			return field.getName() + Condition.NOT_IN + Utils.bracket("NULL");
		return field.getName() + Condition.NOT_IN + Utils.bracket(constructString());
	}

	private String constructString() {
		StringBuilder sb = new StringBuilder();
		for (Literal value : values) {
			if (sb.length() > 0)
				sb.append(COMMA).append(SPACE);
			if (field.getType() == TYPE.STRING	|| field.getType() == TYPE.DATE) {
				sb.append(Utils.quote(value.toString()));
			} else if (field.getType() == TYPE.NUMBER) {
				if (!Utils.isNumber(value.toString()))
					throw new IllegalArgumentException("Number expected but " + value.toString() + " found");
				sb.append(value.toString());
			} else
				// we should not get here
				throw new RuntimeException("Unhandled field type: "	+ field.getType());
		}
		return sb.toString();
	}

}
