package util.dao;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Field {
	// 数据库字段名
	String name();

	// 从数据库查询结果集读取数据时使用的数据类型
	// 如果不指明则从属性类型中推断
	String type() default "";
}
