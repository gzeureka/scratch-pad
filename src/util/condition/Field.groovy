package util.condition;

import java.util.Arrays;
import java.util.Collection;

import util.Utils;

public class Field extends Item {
	
	private static String ORACLE_DATE_FORMAT = 'yyyy-MM-dd'
		
	private static String ORACLE_DATE_TIME_FORMAT = 'yyyy-MM-dd HH24:mi:ss'
		
	private static String REG_EX_FIELD_NAME = '^[-_a-zA-Z0-9.+ ]+$';

	private static String REG_EX_FUNCTION_FIELD_NAME = '^\\$[_a-zA-Z0-9 ]+\\([-_a-zA-Z0-9.+ ]+\\)$';

	public static Field StringField(String name) {
		return new Field(name, TYPE.STRING);
	}

	public static Field NumberField(String name) {
		return new Field(name, TYPE.NUMBER);
	}

	public static Field DateField(String name) {
		return new Field(name, TYPE.DATE);
	}
	
	public static Field DateTimeField(String name) {
		return new Field(name, TYPE.DATE_TIME);
	}

	public static Field StringField(String name, String label) {
		return new Field(name, TYPE.STRING, label);
	}

	public static Field NumberField(String name, String label) {
		return new Field(name, TYPE.NUMBER, label);
	}

	public static Field DateField(String name, String label) {
		return new Field(name, TYPE.DATE, label);
	}

	public static Field DateTimeField(String name, String label) {
		return new Field(name, TYPE.DATE_TIME, label);
	}
	
	private String name;

	private String type;

	public Field(String name, TYPE type) {
		super();
		this.name = name;
		this.type = type.name();
		checkFieldName(name, type);
	}

	public Field(String name, TYPE type, String label) {
		super(label);
		this.name = name;
		this.type = type.name();
		checkFieldName(name, type);
	}

	private static void checkFieldName(String name, TYPE type) {
		if (name.matches(REG_EX_FIELD_NAME))
			return;
		if (name.matches(REG_EX_FUNCTION_FIELD_NAME))
			return;
		throw new IllegalArgumentException("Invalid characters in field name "
				+ name);
	}

	public String getName() {
		return name;
	}

	public TYPE getType() {
		return TYPE.fromStr(type);
	}

	@Override
	public String toString() {
		return convertFunction(name);
	}

	private boolean isFunction(String name) {
		return name.matches(REG_EX_FUNCTION_FIELD_NAME);
	}

	private String convertFunction(String name) {
		String fieldNameStr = name;
		if (isFunction(name)) {
			// TODO convert to db function
			String functionName = name.substring(1, name.indexOf("("));
			String param = name.substring(functionName.length() + 2, name.length() - 1);

			if (functionName=='lower')
				fieldNameStr = "lower(${param})";
			else if (functionName=='upper')
				fieldNameStr = "upper(${param})";
			else
				throw new IllegalArgumentException("Unknown function ${name}");
		}
		return fieldNameStr;
	}

	@Override
	public String parseFieldMap(FieldMap fieldMap) {
		if (fieldMap == null)
			return toString();
		String mappedField = fieldMap.get(name);
		if (mappedField == null)
			return toString();
		else
			return mappedField;
	}

	@Override
	public String toString(String operator, Item item) {
		return innerParseFieldMap(name, operator, item, null);
	}

	private String innerParseFieldMap(String name, String operator, Item item,
			FieldMap fieldMap) {

		String fieldNameStr = convertFunction(name);

		if (item instanceof Field)
			return fieldNameStr + operator + item.parseFieldMap(fieldMap);

		if (getType() == TYPE.NUMBER) {
			String str = item.toString();
			if (!Utils.isNumber(str))
				throw new IllegalArgumentException("Number expected but " + str	+ " found");
			return fieldNameStr + operator + str;
			} else if (getType() == TYPE.STRING) {
				return fieldNameStr + operator + Utils.quote(item.toString());
			} else if (getType() == TYPE.DATE) {
				if(operator in [Condition.GT])
					return fieldNameStr + operator + "to_date('${item} 23:59:59', '${ORACLE_DATE_TIME_FORMAT}')"
				if(operator in [Condition.GE])
					return fieldNameStr + operator + "to_date('${item} 00:00:00', '${ORACLE_DATE_TIME_FORMAT}')"
				else if(operator in [Condition.LT])
					return fieldNameStr + operator + "to_date('${item} 00:00:00', '${ORACLE_DATE_TIME_FORMAT}')"
				else if(operator in [Condition.LE])
					return fieldNameStr + operator + "to_date('${item} 23:59:59', '${ORACLE_DATE_TIME_FORMAT}')"
				else if(operator in [Condition.EQ])
					return fieldNameStr + " between to_date('${item} 00:00:00', '${ORACLE_DATE_TIME_FORMAT}') and to_date('${item} 23:59:59', '${ORACLE_DATE_TIME_FORMAT}')"
				else if(operator in [Condition.NE])
					return fieldNameStr + " not between to_date('${item} 00:00:00', '${ORACLE_DATE_TIME_FORMAT}') and to_date('${item} 23:59:59', '${ORACLE_DATE_TIME_FORMAT}')"
				else
					return fieldNameStr + operator + "to_date('${item}', '${ORACLE_DATE_FORMAT}')"
//				return "to_char(${fieldNameStr}, '${ORACLE_DATE_FORMAT}')" + operator + "'${item}'"
			} else if (getType() == TYPE.DATE_TIME) {
				return fieldNameStr + operator + "to_date('${item}', '${ORACLE_DATE_TIME_FORMAT}')"
//				return "to_char(${fieldNameStr}, '${ORACLE_DATE_TIME_FORMAT}')" + operator + "'${item}'"
			}
			// we should not get here
			throw new IllegalArgumentException("Unhandled field type: " + type);
	}

	@Override
	public String parseFieldMap(String operator, Item item, FieldMap fieldMap) {
		return innerParseFieldMap(parseFieldMap(fieldMap), operator, item, fieldMap);
	}

	/***************************************************************************
	 * convenient methods for creating condition
	 **************************************************************************/
	public IN IN(Collection<String> values) {
		return new IN(this, values);
	}

	public IN IN(Collection<String> values, String label) {
		return new IN(this, values, label);
	}

	public IN IN(String... values) {
		return new IN(this, Arrays.asList(values));
	}

	public NOT_IN NOT_IN(Collection<String> values) {
		return new NOT_IN(this, values);
	}

	public NOT_IN NOT_IN(Collection<String> values, String label) {
		return new NOT_IN(this, values, label);
	}

	public NOT_IN NOT_IN(String... values) {
		return new NOT_IN(this, Arrays.asList(values));
	}

	public EQ EQ(Item item) {
		return new EQ(this, item);
	}

	public EQ EQ(Item item, String label) {
		return new EQ(this, item, label);
	}

	public EQ EQ(String value) {
		return new EQ(this, value);
	}

	public EQ EQ(int value) {
		return new EQ(this, '' + value);
	}

	public EQ EQ(String value, String label) {
		return new EQ(this, value, label);
	}

	public NE NE(Item item) {
		return new NE(this, item);
	}

	public NE NE(Item item, String label) {
		return new NE(this, item, label);
	}

	public NE NE(String value) {
		return new NE(this, value);
	}

	public NE NE(int value) {
		return new NE(this, '' + value);
	}

	public NE NE(String value, String label) {
		return new NE(this, value, label);
	}

	public GE GE(Item item) {
		return new GE(this, item);
	}

	public GE GE(Item item, String label) {
		return new GE(this, item, label);
	}

	public GE GE(String value) {
		return new GE(this, value);
	}

	public GE GE(int value) {
		return new GE(this,'' + value);
	}
	
	public GE GE(String value, String label) {
		return new GE(this, value, label);
	}

	public GT GT(Item item) {
		return new GT(this, item);
	}

	public GT GT(Item item, String label) {
		return new GT(this, item, label);
	}

	public GT GT(String value) {
		return new GT(this, value);
	}

	public GT GT(int value) {
		return new GT(this,'' + value);
	}
	
	public GT GT(String value, String label) {
		return new GT(this, value, label);
	}

	public LE LE(Item item) {
		return new LE(this, item);
	}

	public LE LE(Item item, String label) {
		return new LE(this, item, label);
	}

	public LE LE(String value) {
		return new LE(this, value);
	}

	public LE LE(int value) {
		return new LE(this, '' + value);
	}
	
	public LE LE(String value, String label) {
		return new LE(this, value, label);
	}

	public LT LT(Item item) {
		return new LT(this, item);
	}

	public LT LT(Item item, String label) {
		return new LT(this, item, label);
	}

	public LT LT(String value) {
		return new LT(this, value);
	}
	
	public LT LT(int value) {
		return new LT(this, '' + value);
	}

	public LT LT(String value, String label) {
		return new LT(this, value, label);
	}

	public BETWEEN BETWEEN(Item first, Item second) {
		return new BETWEEN(this, first, second);
	}

	public BETWEEN BETWEEN(Item first, Item second, String label) {
		return new BETWEEN(this, first, second, label);
	}

	public BETWEEN BETWEEN(String first, String second) {
		return new BETWEEN(this, first, second);
	}

	public BETWEEN BETWEEN(String first, String second, String label) {
		return new BETWEEN(this, first, second, label);
	}

	public IS_NULL IS_NULL() {
		return new IS_NULL(this);
	}

	public IS_NULL IS_NULL(String label) {
		return new IS_NULL(this, label);
	}

	public IS_NOT_NULL IS_NOT_NULL() {
		return new IS_NOT_NULL(this);
	}

	public IS_NOT_NULL IS_NOT_NULL(String label) {
		return new IS_NOT_NULL(this, label);
	}

	public LIKE LIKE(Item item) {
		return new LIKE(this, item);
	}

	public LIKE LIKE(Item item, String label) {
		return new LIKE(this, item, label);
	}

	public LIKE LIKE(String value) {
		return new LIKE(this, value);
	}

	public LIKE LIKE(String value, String label) {
		return new LIKE(this, value, label);
	}

	public LIKE CONTAINS(String value) {
		return new LIKE(this, "%" + value + "%");
	}

	public LIKE CONTAINS(String value, String label) {
		return new LIKE(this, "%" + value + "%", label);
	}

	public LIKE START_WITH(String value) {
		return new LIKE(this, value + "%");
	}

	public LIKE START_WITH(String value, String label) {
		return new LIKE(this, value + "%", label);
	}

	public LIKE END_WITH(String value) {
		return new LIKE(this, "%" + value);
	}

	public LIKE END_WITH(String value, String label) {
		return new LIKE(this, "%" + value, label);
	}

	public NOT_LIKE NOT_LIKE(Item item) {
		return new NOT_LIKE(this, item);
	}

	public NOT_LIKE NOT_LIKE(Item item, String label) {
		return new NOT_LIKE(this, item, label);
	}

	public NOT_LIKE NOT_LIKE(String value) {
		return new NOT_LIKE(this, value);
	}

	public NOT_LIKE NOT_LIKE(String value, String label) {
		return new NOT_LIKE(this, value, label);
	}

	public FieldOrder ASC() {
		return FieldOrder.AscFieldOrder(this.name);
	}

	public FieldOrder ASC(String label) {
		return FieldOrder.AscFieldOrder(this.name, label);
	}

	public FieldOrder DESC() {
		return FieldOrder.DescFieldOrder(this.name);
	}

	public FieldOrder DESC(String label) {
		return FieldOrder.DescFieldOrder(this.name, label);
	}
}
