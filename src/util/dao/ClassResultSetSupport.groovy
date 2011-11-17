package util.dao

import java.sql.ResultSet
import util.ObjUtils
import util.StringUtils

class ClassResultSetSupport extends ResultSetSupport {
	
	Class clz
	
	Map resultMap = [:]
	
	ClassResultSetSupport(Class clz, ResultSet rs) {
		super(rs)
		this.clz = clz
	}
	
	Map getResults(MetaData metaData) {
		resultMap.clear()
		metaData.persistentFieldTypeNameMap.each { field, type ->
		    resultMap[metaData.persistentFieldNameMap[field]] = this."${type}\$${field}"
		}
		return resultMap
	}
	
	protected def getData(def tf) {
		if (tf.type=='enum') {
			def str = super.getData(['type':'str','field':tf.field])
			if(str==null)
				return null
			return ObjUtils.getClassOfProperty(clz, StringUtils.tinyCamelCase(tf.field)).fromStr(str)
		}
		else
			return super.getData(tf)
	}
		
}