package util.condition

import util.Bool 
import util.Utils 

class ConditionBuilder {
	
	private static def types = ['sf', 'nf', 'df', 'dtf', 'enum', 'bool']
	private static def operators = ['not':0,
		'is_null':0,
		'is_not_null':0,
		'eq':1,
		'ne':1,
		'gt':1,
		'ge':1,
		'lt':1,
		'le':1,
		'like':1,
		'not_like':1,
		'contains':1,
		'start_with':1,
		'end_with':1,
		'between':2,
		'not_between':2,
		'in':-1]
		
	static Condition build(Condition condition, String name, def ...args) {
		def pattern = ~/^(.+?)(\$([^$]+))?\$([^$]+)$/
		def matcher = name =~ pattern
		if(!matcher)
			return null
					
		String fieldName = matcher[0][1]
		String fieldType = matcher[0][-2]
		String operator = matcher[0][-1]
		
		// operator
		if (!operators.keySet().contains(operator))
			return null
		if (operator!='in' && operators[operator]!=args.size())
			return null
		if (operator=='contains') {
			operator='like'
			args[0]="%${args[0].trim()}%"
		}
		if (operator=='start_with') {
			operator='like'
			args[0]="${args[0].trim()}%"
		}
		if (operator=='end_with') {
			operator='like'
			args[0]="%${args[0].trim()}"
		}
		// field type
		if (fieldType!=null) {
			if (!types.contains(fieldType))
				return null
		}
		else {
			fieldType = inferFieldType(operator, args)
			if(fieldType==null)
				return null
		}
		
		Field field
		switch(fieldType)
		{
			case 'sf':
				field = C.SF(fieldName)
				break
			case 'nf':
				field = C.NF(fieldName)
				break
			case 'df':
				field = C.DF(fieldName)
				args = args.collect{
					if(it instanceof java.util.Date)
						Utils.date2DateString(it)
					else
						it
				}.toArray()
				break
			case 'dtf':
				field = C.DTF(fieldName)
				args = args.collect{
					if(it instanceof java.util.Date)
						Utils.date2DateTimeString(it)
					else
						it
				}.toArray()
				break
			case 'enum':
				field = C.SF(fieldName)
				if(operator=='in')
					args = args[0]*.toStr().toArray()
				else
					args = args.collect{it.toStr()}.toArray()
				break
			case 'bool':
				field = C.SF(fieldName)
				args = args.collect{Bool.fromBoolean(it).toStr()}.toArray()
				break
			default:
				return null
		}
		if(condition==null) {
			if(operator=='not')
				return null
			else
				return field."${operator.toUpperCase()}"(*(args.flatten()*.toString()))
		}
		else if(operator=='not') {
			return condition.NOT()
		}
		else {
			return condition.AND(field."${operator.toUpperCase()}"(*(args.flatten()*.toString())))
		}
	}

	private static String inferFieldType(String operator, def ...args) {
		if(args.size()==0)
			return 'sf'
			
		if(args[0]==null)
			return null
			
		switch(args[0])
		{
			case Number:
				return 'nf'
				break
			case Character:
			case String:
			case GString:
				return 'sf'
				break
			case Date:
				return 'df'
				break
			case Enum:
				return 'enum'
				break
			case Boolean:
				return 'bool'
			case List:
				if(args[0].isEmpty())
					return 'sf'
				else
					return inferFieldType(operator, args[0][0])
				break
			default:
				return null
		}
	}
}