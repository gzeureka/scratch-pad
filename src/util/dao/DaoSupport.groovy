package util.dao

import java.sql.ResultSet;
import java.sql.ResultSetMetaData 

import javax.sql.DataSource
import org.slf4j.Logger 
import org.slf4j.LoggerFactory 
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport
import org.springframework.jdbc.core.RowCallbackHandler
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import util.Bool
import util.ObjUtils
import util.Utils
import util.ObjHelper
import util.condition.C
import util.condition.Condition
import util.condition.FieldMap
import util.condition.FieldOrders

class DaoSupport extends SimpleJdbcDaoSupport {
	
	private static final Logger logger = LoggerFactory.getLogger(DaoSupport)
	
	// 查询结果集大小，小于0不限制大小
	int limit = -1
	
	Dialect dialect
	
	DaoSupport() {
	}
	
	DaoSupport(DataSource dataSource) {
		//super((DataSource)dataSource)
		this.dataSource = dataSource
	}
	
	//=============================================================
	// insert
	//=============================================================
	private static String insertSql(String table, Collection<String> paramNames) {
		return "insert into ${table} (${paramNames.join(',')}) values (${paramNames.collect{':'+it}.join(',')})"
	}
			
	int insert(String table, Map params, boolean genKey=false) {
		String sql = insertSql(table, params.keySet())
		normalize(params)
		logger.debug('{}', sql)
		if(!genKey) {
			int r = simpleJdbcTemplate.update(sql, params)
			return 0
		}else {
			def keyHolder = new GeneratedKeyHolder()
			simpleJdbcTemplate.namedParameterJdbcOperations.update(sql, new MapSqlParameterSource(params), keyHolder)
			return keyHolder.key.intValue()
		}
	}
	
	int insert(Object obj) {
		return insert([obj])[0]
	}
	
	List<Integer> insert(List<Object> objs) {
		if(objs==null || objs.isEmpty())
			return []
			
		MetaData metaData = MetaDatas.get(objs[0].class)
		def ids=[]
		objs.each { obj ->
			def params = [:]
            metaData.persistentFieldNameMap.each { field, prop ->
            	if(metaData.getFieldType(field)=='date')
            		obj."${prop}" = Utils.truncDate(obj."${prop}")
            	params[field] = obj."${prop}"
			}
			int key = dialect.insert(this, metaData, obj, params)
			ids << key
			if(metaData.genKey)
				obj."${metaData.key}" = key
		}
		return ids
	}
	
	//=============================================================
	// delete
	//=============================================================
	void delete(Object obj) {
		if(obj==null)
			return
		MetaData metaData = MetaDatas.get(obj.class)
		Condition cond = C.AND()
		metaData.keys.each{cond.append(C."${metaData.getDBFieldName(it)}\$eq"(obj."${it}"))}
		String sql = C.WHERE("delete from ${metaData.table}", cond)
		logger.debug('{}', sql)
		simpleJdbcTemplate.update(sql, [:])
	}
	
	void delete(Class clz, int id) {
		delete(clz, [id])
	}
	
	void delete(Class clz, List<Integer> ids) {
		if(ids==null || ids.isEmpty())
			return
		MetaData metaData = MetaDatas.get(clz)
		String sql = C.WHERE("delete from ${metaData.table}", C."${metaData.keyDBFieldName}\$in"(ids))
		logger.debug('{}', sql)
		simpleJdbcTemplate.update(sql)
	}
	
	//根据外键删除表记录
	void delete(Class clz, String fkName, int fkId) {
		if(fkName==null || fkId==Utils.NULL_ID)
			return
		MetaData metaData = MetaDatas.get(clz)
		String sql = C.WHERE("delete from ${metaData.table}", C."${fkName}\$eq"(fkId))
		logger.debug('{}', sql)
		simpleJdbcTemplate.update(sql)
	}
	
	int delete(Class clz, Condition cond) {
		MetaData metaData = MetaDatas.get(clz)
		String sql = C.WHERE("delete from ${metaData.table}", cond)
		logger.debug('{}', sql)
		return simpleJdbcTemplate.update(sql)
	}
	
	int delete(String table, Condition cond) {
		String sql = C.WHERE("delete from ${table}", cond)
		logger.debug('{}', sql)
		return simpleJdbcTemplate.update(sql)
	}
	
	//=============================================================
	// select
	//=============================================================
	// rowMapper 是接受一个参数 ResultSetSupport 的闭包
	List select(String sql, Condition condition, FieldOrders orderBy, FieldMap fieldMap, def rowMapper) {
		return select(sql, condition, orderBy, fieldMap, this.limit, rowMapper)
	}
	
	List select(String sql, Condition condition, FieldOrders orderBy, FieldMap fieldMap, int limit, def rowMapper) {
		def rss = new ResultSetSupport(null)
		def list = []
   		if(limit>0)
   			list.ensureCapacity(limit)
   		String sqlStr=dialect.where(sql, condition, orderBy, fieldMap, limit)
		logger.debug('{}', sqlStr)
		simpleJdbcTemplate.namedParameterJdbcOperations.query(sqlStr, [:],
				{ rs ->
					rss.rs = rs
					list << rowMapper.call(rss)
				} as RowCallbackHandler)
				
		return list
	}
	
	List select(String sql, Condition condition, FieldOrders orderBy, FieldMap fieldMap, int limit, List<Class> classes, def rowMapper) {
		def clz2MetaData = classes.collect{MetaDatas.get(it)}.groupBy{it.clz}
		def clz2Rss = classes.collect{clz -> new ClassResultSetSupport(clz, null)}.groupBy{it.clz}
		def rss=null
		if(rowMapper.parameterTypes.length>classes.size()) {
			// 如果rowMapper闭包接受的参数个数大于classes的元素个数，则将ResultSetSupport作为调用rowMapper的第一个参数
			rss=new ResultSetSupport(null)
		}
		def list = []		
		if(limit>0)
			list.ensureCapacity(limit)
		String sqlStr=dialect.where(sql, condition, orderBy, fieldMap, limit)
		logger.debug('{}', sqlStr)			
		simpleJdbcTemplate.namedParameterJdbcOperations.query(sqlStr, [:],
				{ rs ->
					clz2Rss.each{k,v->v*.rs=rs}
					if(rss) {
						rss.rs=rs
						list << rowMapper.call(rss, *(classes.collect{clz-> ObjUtils.construct(clz, clz2Rss[clz][0].getResults(clz2MetaData[clz][0]))}))
					}
					else
						list << rowMapper.call(*(classes.collect{clz-> ObjUtils.construct(clz, clz2Rss[clz][0].getResults(clz2MetaData[clz][0]))}))
				} as RowCallbackHandler)
				
		return list
	}
	
	List select(Class clz, Condition condition, FieldOrders orderBy) {
		return select(clz, condition, orderBy, this.limit)
	}
	
	List select(Class clz, Condition condition, FieldOrders orderBy, int limit) {
		MetaData metaData = MetaDatas.get(clz)
		String sql = dialect.where("select ${metaData.persistentFieldNameMap.keySet().join(',')} from ${metaData.table}", condition, orderBy, C.FIELD_MAP(), limit)
		def rss = new ClassResultSetSupport(clz, null)
		def list = []
		logger.debug('{}', sql)
		simpleJdbcTemplate.namedParameterJdbcOperations.query(sql, [:], { rs ->
			rss.rs = rs
			list << ObjUtils.construct(clz, rss.getResults(metaData))
		} as RowCallbackHandler)
		// hasOne
		
		// hasMany
		
		return list
	}
	
	List<Map> selectMap(String sql, Condition condition, FieldOrders orderBy, FieldMap fieldMap){
		return selectMap(sql, condition, orderBy, fieldMap, this.limit)
	}
	
	List<Map> selectMap(String sql, Condition condition, FieldOrders orderBy, FieldMap fieldMap, int limit){
		return select(sql, condition, orderBy, fieldMap, limit){ ResultSetSupport rss ->
					ResultSet rs = rss.rs
					ResultSetMetaData metaData = rs.metaData
					int colCount = metaData.columnCount
					
					def row = [:]
					(1..colCount).each { 
						row[metaData.getColumnName(it).toLowerCase()] = rs.getObject(it)
					}
					
					return row
				}
	}
	
	//=============================================================
	// update
	//=============================================================	
	private static String updateSql(String table, Collection<String> paramNames, Condition condition) {
		return C.WHERE("update ${table} set ${paramNames.collect{it+'=:'+it}.join(',')}", condition)
	}
	
	int update(String sql, Map params) {
		logger.debug('{}', sql)
		return simpleJdbcTemplate.update(sql, params)
	}
	
	int update(Class clz, Condition condition, Map params) {
		MetaData metaData = MetaDatas.get(clz)
		params.each {field, value ->
	    	if(metaData.getFieldType(field)=='date')
	    		params[field] = Utils.truncDate(value)
		}
		update(metaData.table, condition, params)
	}
	
	int update(Class clz, Map params, int id) {
		MetaData metaData = MetaDatas.get(clz)
		update(metaData.table, C.id$eq(id), params)
	}
	
	int update(String table, Condition condition, Map params) {
		String sql = updateSql(table, params.keySet(), condition)
		normalize(params)
		logger.debug('{}', sql)
		return simpleJdbcTemplate.update(sql, params)
	}
		
	int update(Object obj) {
		return update([obj])
	}
	
	int update(List<Object> objs) {
		assert objs!=null && !objs.any{it==null}
		if(objs.isEmpty())
			return 0
			
		MetaData metaData = MetaDatas.get(objs[0].class)
		assert metaData!=null
		return objs.sum(0) { obj ->
			def params = [:]
			metaData.persistentFieldNameMap.each { field, prop ->
				if(prop!=metaData.key) {
	            	if(metaData.getFieldType(field)=='date')
	            		obj."${prop}" = Utils.truncDate(obj."${prop}")
					params[field] = obj."${prop}"
				}
			}
			return update(metaData.table, C."${metaData.keyDBFieldName}\$eq"(obj."${metaData.key}"), params)			
		}
	}
	
	//=============================================================
	// other
	//=============================================================
	// 根据属性注入对象
	// 对objs里的每个对象，按fieldMap里指定的每对属性名，注入对象
	// 例如，fieldMap内容为[k1:v1, k2:v2]，
	// 对objs里的每个对象obj, 以obj.k1的值为id，找到对象o1，将o1设为obj.v1的值
	// 以obj.k2的值为id，找到对象o2，将o2设为obj.v2的值
	protected void injectObjs(List objs, Map fieldMap) {
		ObjHelper.injectObjs(objs, fieldMap)
	}
	
	private void normalize(Map params) {
		params.each{k,v ->
			switch(v) {
				case Boolean:
					params[k] = Bool.fromBoolean(v).toStr()
					break
				case Enum:
					params[k] = v.toStr()
					break
			}
		}
	}
	
}
