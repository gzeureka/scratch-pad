package util.condition;

import java.util.Date;

import util.Utils;

public class NOT_BETWEEN extends Condition {
	private Field field;

	private Item first;

	private Item second;

	public NOT_BETWEEN(Field field, Item first, Item second) {
		super();
		this.field = field;
		this.first = first;
		this.second = second;
	}

	public NOT_BETWEEN(Field field, String first, String second) {
		super();
		this.field = field;
		this.first = new Literal(first);
		this.second = new Literal(second);
	}

	public NOT_BETWEEN(Field field, Item first, Item second, String label) {
		super(label);
		this.field = field;
		this.first = first;
		this.second = second;
	}

	public NOT_BETWEEN(Field field, String first, String second, String label) {
		super(label);
		this.field = field;
		this.first = new Literal(first);
		this.second = new Literal(second);
	}

	public static NOT_BETWEEN betweenDate(String fieldName, Date beginDate,
			Date endDate, boolean appendTime) {
		String beginDateString = Utils.date2DateString(beginDate);
		String endDateString = Utils.date2DateString(endDate);
		if (appendTime) {
			beginDateString += " 00:00:00";
			endDateString += " 23:59:59";
		}
		NOT_BETWEEN ret = new NOT_BETWEEN(Field.DateField(fieldName),
				beginDateString, endDateString);
		return ret;
	}

	public static NOT_BETWEEN betweenDate(String fieldName, Date beginDate,
			Date endDate, boolean appendTime, String label) {
		String beginDateString = Utils.date2DateString(beginDate);
		String endDateString = Utils.date2DateString(endDate);
		if (appendTime) {
			beginDateString += " 00:00:00";
			endDateString += " 23:59:59";
		}
		NOT_BETWEEN ret = new NOT_BETWEEN(Field.DateField(fieldName),
				beginDateString, endDateString, label);
		return ret;
	}

	@Override
	public String parseFieldMap(FieldMap fieldMap) {
		if (fieldMap == null)
			return toString();

		return field.parseFieldMap(fieldMap) + constructString();
	}

	@Override
	public String toString() {
		return field.toString() + constructString();
	}

	private String constructString() {
		String firstStr = first.toString();
		if (first instanceof Literal &&
				((field.getType() == TYPE.STRING || field.getType() == TYPE.DATE)))
			firstStr = Utils.quote(firstStr);

		String secondStr = second.toString();
		if (second instanceof Literal &&
				((field.getType() == TYPE.STRING || field.getType() == TYPE.DATE)))
			secondStr = Utils.quote(secondStr);

		String str = Condition.NOT_BETWEEN + Condition.SPACE + firstStr	+ 
				Condition.SPACE + Condition.AND + Condition.SPACE + secondStr + Condition.SPACE;
		return str;
	}

	public Field getField() {
		return field;
	}

	public Item getFirst() {
		return first;
	}

	public Item getSecond() {
		return second;
	}

}
