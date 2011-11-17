package util.dao

import java.sql.ResultSet
import util.Bool
import util.Utils

class ResultSetSupport {
	
	private static def pattern = ~/^(.+?)\$([^$]+)$/
	
	private static types = [
	     'bool':{rs, name -> def str=rs.getString(name); return str?Bool.fromStr(str).toBoolean():false},
	     'int':{rs, name -> return rs.getInt(name)},
	     'long':{rs, name -> return rs.getLong(name)},
		 'dec':{rs, name, scale -> def ret=rs.getBigDecimal(name); if(scale!=null) ret=ret?.setScale(scale); return ret},
		 'str':{rs,name -> return rs.getString(name)?.trim()},
		 'date':{rs,name -> return rs.getDate(name)},
		 'time':{rs,name -> return rs.getTime(name)},
		 'datetime':{rs,name -> return rs.getTimestamp(name)},
		 'dateStr':{rs, name -> return Utils.date2DateString(rs.getDate(name))},
		 'timeStr':{rs, name -> return Utils.date2TimeString(rs.getTimestamp(name))},
		 'datetimeStr':{rs, name -> return Utils.date2DateTimeString(rs.getDate(name))}, 
		]

	ResultSet rs
	
	Map names = [:]
	
	ResultSetSupport(ResultSet rs) {
		this.rs = rs
	}

	static String getType(Class clz) {
		switch(clz) {
			case boolean:
			case Boolean:
				return 'bool'
				break
			case int:
			case Integer:
				return 'int'
				break
			case long:
			case Long:
				return 'long'
				break
			case BigDecimal:
				return 'dec'
				break
			case String:
				return 'str'
				break
			case Date:
				return 'date'
				break
			case Enum:
				return 'enum'
			default:
				return null
		}
	}
	
	protected def parse(String name) {
		def matcher = name =~ pattern
		if(!matcher)
			throw new MissingPropertyException(name, ResultSetSupport.class)
				
		String type = matcher[0][1]
		String field = matcher[0][2]
		def ret=['type':type, 'field':field]
 		if(type=~/dec(\d*)/) {
 			ret['type']='dec'
 			def scale=(type=~/dec(\d*)/)[0][1]
 			if(scale.isInteger())                                
 				ret['scale']=scale.toInteger()
 			else
 				ret['scale']=null
		}
		return ret
	}
	
	def propertyMissing(String name) {
		if(names[name]==null) {
			names[name]=parse(name)
		}
		return getData(names[name])
	}
	
	protected def getData(def tf) {
		if (types[tf.type]!=null) {
			if(tf.type=='dec')
				return types[tf.type].call(rs, tf.field, tf.scale)
			else
				return types[tf.type].call(rs, tf.field)
		}
		else
			throw new MissingPropertyException(tf.type, ResultSetSupport.class)				
	}
	
}