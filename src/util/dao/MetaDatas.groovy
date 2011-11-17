package util.dao

import util.ReadWriteLockMap

class MetaDatas {
	
	private static ReadWriteLockMap<Class, MetaData> metaDatas = new ReadWriteLockMap<Class, MetaData>()
	
	public static MetaData get(Class clz) {
		MetaData metaData = metaDatas.get(clz)
		if (metaData==null) {
			metaData = MetaData.inspectMetaData(clz)
			metaDatas.set(clz, metaData)
		}
		return metaData
	}
}