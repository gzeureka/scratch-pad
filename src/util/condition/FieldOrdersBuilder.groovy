package util.condition

class FieldOrdersBuilder {
	
	private static def pattern = ~/^(.+?)\$(asc|desc)$/
		
	static FieldOrders build(FieldOrders fieldOrders, String name) {
		def matcher = name =~ pattern
		if(!matcher)
			return null
		
		String fieldName = matcher[0][1]
		String orderType = matcher[0][2]
		
		FieldOrder fieldOrder = new FieldOrder(fieldName, orderType=='asc'?Order.ASC:Order.DESC)
		if (fieldOrders==null)
			fieldOrders = new FieldOrders()		
		fieldOrders.add(fieldOrder)
		
		return fieldOrders
	}
}