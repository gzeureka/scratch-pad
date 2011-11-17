package util.dao;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {
	// 表名
	String table();

	// 主键的Property名称。 如果主键包括多个Property，用“,”分隔，例如 "p1, p2"
	String key() default "id";

	// insert时是否自动生成主键。如果主键包括多个Property，genKey不允许设为true
	boolean genKey() default false;
}
