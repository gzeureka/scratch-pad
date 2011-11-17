package util.dao

import util.condition.C
import util.condition.Condition
import util.condition.FieldMap
import util.condition.FieldOrders

class MSSQLDBDialect implements Dialect {
	
	int insert(DaoSupport daoSupport, MetaData metaData, Object obj, Map params) {
		if(metaData.genKey)
			params.remove(metaData.key)
		int ret = daoSupport.insert(metaData.table, params, metaData.genKey)
		if(metaData.genKey)
			obj."${metaData.key}" = ret
		return ret
	}
	@Override
	public String where(String sql, Condition condition,
			FieldOrders fieldOrders, FieldMap fieldMap, int limit) {
		return C.WHERE(sql, condition, fieldOrders, fieldMap)
	}
	
}