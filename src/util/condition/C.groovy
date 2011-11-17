package util.condition;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class C {
	
	static { 
		C.metaClass.static.methodMissing = { String name, args ->
			Condition condition = ConditionBuilder.build(null, name, args)
			if (condition==null)
				throw new MissingMethodException(name, C.class, args)
			return condition
		}
		
		C.metaClass.static.propertyMissing = { String name ->
			FieldOrders fieldOrders = FieldOrdersBuilder.build(null, name)
			if (fieldOrders==null)
				throw new MissingPropertyException(name, C.class)
			return fieldOrders
		}
	}
	
	// TRUE
	public static Condition TRUE() {
		return C.NF("1").EQ("1");
	}

	// FALSE
	public static Condition FALSE() {
		return C.NF("1").EQ("0");
	}

	// FIELD_MAP
	public static FieldMap FIELD_MAP() {
		return new FieldMap();
	}

	public static FieldMap FIELD_MAP(Map map) {
		return new FieldMap(map);
	}

	public static FieldMap FIELD_MAP(String key, String value) {
		FieldMap fieldMap = FIELD_MAP();
		fieldMap.map(key, value);
		return fieldMap;
	}

	// AND
	public static AND AND(Collection<Condition> nodes) {
		AND and = new AND(nodes);
		return and;
	}

	public static AND AND(Collection<Condition> nodes, String label) {
		AND and = new AND(nodes, label);
		return and;
	}

	public static AND AND(Condition... nodes) {
		AND and = new AND(Arrays.asList(nodes));
		return and;
	}

	public static AND AND(String label, Condition... nodes) {
		AND and = new AND(Arrays.asList(nodes), label);
		return and;
	}

	// OR
	public static OR OR(Collection<Condition> nodes) {
		OR or = new OR(nodes);
		return or;
	}

	public static OR OR(Collection<Condition> nodes, String label) {
		OR or = new OR(nodes, label);
		return or;
	}

	public static OR OR(Condition... nodes) {
		OR or = new OR(Arrays.asList(nodes));
		return or;
	}

	public static OR OR(String label, Condition... nodes) {
		OR or = new OR(Arrays.asList(nodes), label);
		return or;
	}

	// EQ
	public static EQ EQ(Item left, Item right) {
		return new EQ(left, right);
	}

	public static EQ EQ(Item left, Item right, String label) {
		return new EQ(left, right, label);
	}

	public static EQ EQ(String left, Item right) {
		return new EQ(left, right);
	}

	public static EQ EQ(String left, Item right, String label) {
		return new EQ(left, right, label);
	}

	public static EQ EQ(String left, String right) {
		return new EQ(left, right);
	}

	public static EQ EQ(String left, String right, String label) {
		return new EQ(left, right, label);
	}

	public static EQ EQ(Item left, String right) {
		return new EQ(left, right);
	}

	public static EQ EQ(Item left, String right, String label) {
		return new EQ(left, right, label);
	}

	// NE
	public static NE NE(Item left, Item right) {
		return new NE(left, right);
	}

	public static NE NE(Item left, Item right, String label) {
		return new NE(left, right, label);
	}

	public static NE NE(String left, Item right) {
		return new NE(left, right);
	}

	public static NE NE(String left, Item right, String label) {
		return new NE(left, right, label);
	}

	public static NE NE(String left, String right) {
		return new NE(left, right);
	}

	public static NE NE(String left, String right, String label) {
		return new NE(left, right, label);
	}

	public static NE NE(Item left, String right) {
		return new NE(left, right);
	}

	public static NE NE(Item left, String right, String label) {
		return new NE(left, right, label);
	}

	// GE
	public static GE GE(Item left, Item right) {
		return new GE(left, right);
	}

	public static GE GE(Item left, Item right, String label) {
		return new GE(left, right);
	}

	public static GE GE(String left, Item right) {
		return new GE(left, right);
	}

	public static GE GE(String left, Item right, String label) {
		return new GE(left, right, label);
	}

	public static GE GE(String left, String right) {
		return new GE(left, right);
	}

	public static GE GE(String left, String right, String label) {
		return new GE(left, right, label);
	}

	public static GE GE(Item left, String right) {
		return new GE(left, right);
	}

	public static GE GE(Item left, String right, String label) {
		return new GE(left, right, label);
	}

	// GT
	public static GT GT(Item left, Item right) {
		return new GT(left, right);
	}

	public static GT GT(Item left, Item right, String label) {
		return new GT(left, right, label);
	}

	public static GT GT(String left, Item right) {
		return new GT(left, right);
	}

	public static GT GT(String left, Item right, String label) {
		return new GT(left, right, label);
	}

	public static GT GT(String left, String right) {
		return new GT(left, right);
	}

	public static GT GT(String left, String right, String label) {
		return new GT(left, right, label);
	}

	public static GT GT(Item left, String right) {
		return new GT(left, right);
	}

	public static GT GT(Item left, String right, String label) {
		return new GT(left, right, label);
	}

	// LT
	public static LT LT(Item left, Item right) {
		return new LT(left, right);
	}

	public static LT LT(Item left, Item right, String label) {
		return new LT(left, right, label);
	}

	public static LT LT(String left, Item right) {
		return new LT(left, right);
	}

	public static LT LT(String left, Item right, String label) {
		return new LT(left, right, label);
	}

	public static LT LT(String left, String right) {
		return new LT(left, right);
	}

	public static LT LT(String left, String right, String label) {
		return new LT(left, right, label);
	}

	public static LT LT(Item left, String right) {
		return new LT(left, right);
	}

	public static LT LT(Item left, String right, String label) {
		return new LT(left, right, label);
	}

	// LE
	public static LE LE(Item left, Item right) {
		return new LE(left, right);
	}

	public static LE LE(Item left, Item right, String label) {
		return new LE(left, right, label);
	}

	public static LE LE(String left, Item right) {
		return new LE(left, right);
	}

	public static LE LE(String left, Item right, String label) {
		return new LE(left, right, label);
	}

	public static LE LE(String left, String right) {
		return new LE(left, right);
	}

	public static LE LE(String left, String right, String label) {
		return new LE(left, right, label);
	}

	public static LE LE(Item left, String right) {
		return new LE(left, right);
	}

	public static LE LE(Item left, String right, String label) {
		return new LE(left, right, label);
	}

	// NOT
	public static NOT NOT(Condition node) {
		return new NOT(node);
	}

	public static NOT NOT(Condition node, String label) {
		return new NOT(node, label);
	}

	// LIKE
	public static LIKE LIKE(Item left, Item right) {
		return new LIKE(left, right);
	}

	public static LIKE LIKE(Item left, Item right, String label) {
		return new LIKE(left, right, label);
	}

	public static LIKE LIKE(String left, Item right) {
		return new LIKE(left, right);
	}

	public static LIKE LIKE(String left, Item right, String label) {
		return new LIKE(left, right, label);
	}

	public static LIKE LIKE(String left, String right) {
		return new LIKE(left, right);
	}

	public static LIKE LIKE(String left, String right, String label) {
		return new LIKE(left, right, label);
	}

	public static LIKE LIKE(Item left, String right) {
		return new LIKE(left, right);
	}

	public static LIKE LIKE(Item left, String right, String label) {
		return new LIKE(left, right, label);
	}

	// NOT_LIKE
	public static NOT_LIKE NOT_LIKE(Item left, Item right) {
		return new NOT_LIKE(left, right);
	}

	public static NOT_LIKE NOT_LIKE(Item left, Item right, String label) {
		return new NOT_LIKE(left, right, label);
	}

	public static NOT_LIKE NOT_LIKE(String left, Item right) {
		return new NOT_LIKE(left, right);
	}

	public static NOT_LIKE NOT_LIKE(String left, Item right, String label) {
		return new NOT_LIKE(left, right, label);
	}

	public static NOT_LIKE NOT_LIKE(String left, String right) {
		return new NOT_LIKE(left, right);
	}

	public static NOT_LIKE NOT_LIKE(String left, String right, String label) {
		return new NOT_LIKE(left, right, label);
	}

	public static NOT_LIKE NOT_LIKE(Item left, String right) {
		return new NOT_LIKE(left, right);
	}

	public static NOT_LIKE NOT_LIKE(Item left, String right, String label) {
		return new NOT_LIKE(left, right, label);
	}

	// BETWEEN
	public static BETWEEN BETWEEN(Field field, Item first, Item second) {
		return new BETWEEN(field, first, second);
	}

	public static BETWEEN BETWEEN(Field field, Item first, Item second,
			String label) {
		return new BETWEEN(field, first, second, label);
	}

	public static BETWEEN BETWEEN(Field field, String first, String second) {
		return new BETWEEN(field, first, second);
	}

	public static BETWEEN BETWEEN(Field field, String first, String second,
			String label) {
		return new BETWEEN(field, first, second, label);
	}

	// IS_NULL
	public static IS_NULL IS_NULL(Item item) {
		return new IS_NULL(item);
	}

	public static IS_NULL IS_NULL(Item item, String label) {
		return new IS_NULL(item, label);
	}

	// IS_NOT_NULL
	public static IS_NOT_NULL IS_NOT_NULL(Item item) {
		return new IS_NOT_NULL(item);
	}

	public static IS_NOT_NULL IS_NOT_NULL(Item item, String label) {
		return new IS_NOT_NULL(item, label);
	}

	// Field
	public static Field F(String name, TYPE type) {
		return new Field(name, type);
	}

	public static Field F(String name, TYPE type, String label) {
		return new Field(name, type, label);
	}

	public static Field SF(String name) {
		return Field.StringField(name);
	}

	public static Field SF(String name, String label) {
		return Field.StringField(name, label);
	}

	public static Field NF(String name) {
		return Field.NumberField(name);
	}

	public static Field NF(String name, String label) {
		return Field.NumberField(name, label);
	}

	public static Field DF(String name) {
		return Field.DateField(name);
	}

	public static Field DF(String name, String label) {
		return Field.DateField(name, label);
	}

	public static Field DTF(String name) {
		return Field.DateTimeField(name);
	}
	
	public static Field DTF(String name, String label) {
		return Field.DateTimeField(name, label);
	}
	
	// IN
	public static IN IN(Field field, Collection<String> values) {
		return new IN(field, values);
	}

	public static IN IN(Field field, Collection<String> values, String label) {
		return new IN(field, values, label);
	}

	public static IN IN(Field field) {
		return new IN(field);
	}

	public static IN IN(Field field, String label) {
		return new IN(field, label);
	}

	// NOT_IN
	public static NOT_IN NOT_IN(Field field, Collection<String> values) {
		return new NOT_IN(field, values);
	}

	public static NOT_IN NOT_IN(Field field, Collection<String> values,
			String label) {
		return new NOT_IN(field, values, label);
	}

	public static NOT_IN NOT_IN(Field field) {
		return new NOT_IN(field);
	}

	public static NOT_IN NOT_IN(Field field, String label) {
		return new NOT_IN(field, label);
	}

	// Field Orders
	public static FieldOrders ORDER_BY(FieldOrder... fieldOrders) {
		return new FieldOrders(fieldOrders);
	}

	public static FieldOrder ASC(String fieldName) {
		return FieldOrder.AscFieldOrder(fieldName);
	}

	public static FieldOrder ASC(String fieldName, String label) {
		return FieldOrder.AscFieldOrder(fieldName, label);
	}

	public static FieldOrder DESC(String fieldName) {
		return FieldOrder.DescFieldOrder(fieldName);
	}

	public static FieldOrder DESC(String fieldName, String label) {
		return FieldOrder.DescFieldOrder(fieldName, label);
	}

	// Append clause
	public static boolean isBlank(Condition condition) {
		return (condition == null || condition.toString().trim().length() == 0);
	}

	public static boolean isBlank(FieldOrders fieldOrders) {
		return (fieldOrders == null || fieldOrders.toString().trim().length() == 0);
	}

	public static String WHERE(String sql, Condition condition,
			FieldMap fieldMap) {
		if (isBlank(condition))
			return sql;
		return sql + " where " + condition.parseFieldMap(fieldMap);
	}

	public static String WHERE(String sql, Condition condition) {
		return WHERE(sql, condition, (FieldMap) null);
	}

	public static String WHERE(String sql, Condition condition,
			FieldOrders fieldOrders, FieldMap fieldMap) {
		return ORDER_BY(WHERE(sql, condition, (FieldMap)fieldMap), fieldOrders, fieldMap);
	}

	public static String WHERE(String sql, Condition condition,
			FieldOrders fieldOrders) {
		return WHERE(sql, condition, fieldOrders, null);
	}

	public static String WHERE(String sql, Condition condition,
			FieldOrders fieldOrders, int offset, int count) {
		if (count > 0) {
			Pattern pattern = Pattern.compile("select ",
					Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(sql);
			if (matcher.matches()) {
				sql = matcher.replaceFirst(" select top " + (offset + count)
						+ " ");
			}
		}
		return WHERE(sql, condition, fieldOrders, null);
	}

	public static String AND(String sql, Condition condition, FieldMap fieldMap) {
		if (isBlank(condition))
			return sql;
		return sql + " and " + condition.parseFieldMap(fieldMap);
	}

	public static String AND(String sql, Condition condition) {
		return AND(sql, condition, null);
	}

	public static String OR(String sql, Condition condition, FieldMap fieldMap) {
		if (isBlank(condition))
			return sql;
		return sql + " or " + condition.parseFieldMap(fieldMap);
	}

	public static String OR(String sql, Condition condition) {
		return OR(sql, condition, null);
	}

	public static String ORDER_BY(String sql, FieldOrders fieldOrders,
			FieldMap fieldMap) {
		if (isBlank(fieldOrders))
			return sql;
		return sql + " order by " + fieldOrders.parseFieldMap(fieldMap);
	}

	public static String ORDER_BY(String sql, FieldOrders fieldOrders) {
		return ORDER_BY(sql, fieldOrders, null);
	}

	public static String ORDER_BY(String sql, FieldOrder... fieldOrders) {
		return ORDER_BY(sql, C.ORDER_BY(fieldOrders), null);
	}

	public static String LIMIT(String sql, int limit) {
		return sql + " limit " + limit;
	}
}
