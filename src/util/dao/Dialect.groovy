package util.dao

import util.condition.Condition;
import util.condition.FieldMap;
import util.condition.FieldOrders;

interface Dialect {
	int insert(DaoSupport daoSupport, MetaData metaData, Object obj, Map params)
	
	String where(String sql, Condition condition, FieldOrders fieldOrders, FieldMap fieldMap, int limit)
}