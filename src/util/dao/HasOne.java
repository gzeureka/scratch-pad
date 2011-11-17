package util.dao;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface HasOne {
	// 子对象指向父对象ID的属性名
	String ref();
}
