package util.dao

import java.lang.reflect.Modifier
import util.StringUtils

class MetaData {
	
	static String KEY_TYPE = 'type' 
	static String KEY_REF = 'ref' 
	
	Class clz
	
	// database table name
	String table
	
	// key property name
	String[] keys
	
	// true: generate key for object when insert,
	// false: don't generate key for object when insert
	boolean genKey
	
	// database field name -> class property name
	Map<String, String> persistentFieldNameMap = [:]
	
	// database field name -> property type
    Map<String, String> persistentFieldTypeNameMap = [:]
	
	// class property name -> class property type
	private Map<String, Class> propTypeMap = [:]
	
	// class property name -> Map[KEY_TYPE:Class, KEY_REF:String]
	Map<String, Map<String, Object>> hasOneMap = [:]
                                                                                            
	// class property name -> Map[KEY_TYPE:Class, KEY_REF:String]
	Map<String, Map<String, Object>> hasManyMap = [:]

	String getKey() {
		assert keys.length==1
		return keys[0]
	}
	                                               
	String getDBFieldName(String propName) {
		return (persistentFieldNameMap.find {k,v -> v==propName})?.key
	}
	
	String getKeyDBFieldName() {
		assert keys.length==1
		return getDBFieldName(key)
	}
	
	String[] getKeyDBFieldNames() {
		return keys.collect{getDBFieldName(it)}.toArray()
	}
	
	String getPropertyName(String dbFieldName) {
		return persistentFieldNameMap[dbFieldName]
	}
	
	List<String> getPropNamesOfType(Class clz) {
		return (propTypeMap.findAll {k,v -> v==clz})*.key
	}
	
	Class getPropType(String propName) {
		return propTypeMap[propName]
	}
	
	String getFieldType(String fieldName) {
		return persistentFieldTypeNameMap[fieldName]
	}
	
	static MetaData inspectMetaData(Class clz) {
		def entity = clz.getAnnotation(Entity.class)
		if (entity==null || entity.table().trim()=='')
			throw new IllegalArgumentException("@Entity annotation not specified correctly on ${clz}")

		MetaData metaData = new MetaData()
		metaData.clz = clz
		metaData.table = entity.table().trim()
		metaData.keys = entity.key().split(',')*.trim().toArray()
		metaData.genKey = entity.genKey()
		
		def fields = clz.declaredFields.findAll { field ->
			!Modifier.isStatic(field.modifiers) &&
			!Modifier.isTransient(field.modifiers) &&
			!Modifier.isVolatile(field.modifiers) 
		}
		fields.each { field ->
			metaData.propTypeMap[field.name] = field.type
		}
		
		// 找出需要持久化的Field
		def persistentFields = fields.findAll { field ->
			return field.getAnnotation(HasOne.class)==null &&
			field.getAnnotation(HasMany.class)==null &&
			field.getAnnotation(Transient.class)==null &&
			!Collection.isAssignableFrom(field.type) &&
			!Map.isAssignableFrom(field.type)
		}

		// 找出HasOne关系
		fields.findAll{field->field.getAnnotation(HasOne.class)!=null}.each{ field ->
			def map=[:]
			metaData.hasOneMap[field.name] = map
			map[KEY_TYPE] = field.type
			map[KEY_REF] = field.getAnnotation(HasOne.class).ref().trim()
		}

		// 找出HasMany关系
		fields.findAll{field->field.getAnnotation(HasMany.class)!=null}.each { field ->
			def map=[:]
			metaData.hasManyMap[field.name] = map
			def annotation = field.getAnnotation(HasMany.class) 
			map[KEY_TYPE] = annotation.type()
			map[KEY_REF] = annotation.ref().trim()
		}

		persistentFields.each { field ->
			def fieldAnnotation = field.getAnnotation(Field.class)
			
			String dbFieldName = (fieldAnnotation?.name()?:StringUtils.uncamelCase(field.name)).trim()
			if (dbFieldName=='')
				throw new IllegalArgumentException("@Field annotation's name cannot be empty on ${field}")
			
			String fieldType
			if(fieldAnnotation==null || fieldAnnotation.type().trim()=='')
				fieldType = getFieldTypeStr(field)
			else
				fieldType = fieldAnnotation.type().trim()
				
			metaData.persistentFieldNameMap[dbFieldName] = field.name
			metaData.persistentFieldTypeNameMap[dbFieldName] = fieldType
		}
		return metaData
	}

	private static String getFieldTypeStr(java.lang.reflect.Field field) {
		String type = ResultSetSupport.getType(field.type)
		if (type==null)
			throw new IllegalArgumentException("field type '${field.type}' not support on @Field ${field}")
		return type
	}	
}