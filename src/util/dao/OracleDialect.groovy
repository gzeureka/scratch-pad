package util.dao

import org.springframework.jdbc.support.incrementer.OracleSequenceMaxValueIncrementer

import util.condition.C
import util.condition.Condition
import util.condition.FieldMap
import util.condition.FieldOrders

class OracleDialect implements Dialect {
	
	int insert(DaoSupport daoSupport, MetaData metaData, Object obj, Map params) {
		if(metaData.genKey) {
			def oin = new OracleSequenceMaxValueIncrementer(daoSupport.dataSource, "seq_${metaData.table}_id")
			obj."${metaData.key}" = oin.nextIntValue()
			params[metaData.getDBFieldName(metaData.key)] = obj."${metaData.key}"
		}
		daoSupport.insert(metaData.table, params, false)
		if(metaData.genKey)
			return obj."${metaData.key}"
		else
			return 0
	}
	@Override
	public String where(String sql, Condition condition,
			FieldOrders fieldOrders, FieldMap fieldMap, int limit) {
		//println("OracleDialect limit: " + limit + "| condition: " +condition)
		if(limit>=0){
			if(fieldOrders!=null){
				return "select * from (${C.WHERE(sql, condition, fieldOrders, fieldMap)}) where rownum<=${limit}"
			}
			else
				condition=C.AND(C.rownum$le(limit), condition)				
				assert(limit!=0)
			}	
		return C.WHERE(sql, condition, fieldOrders, fieldMap)
	}
	
	
}