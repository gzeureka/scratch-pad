package util.dao;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// 用于不需要持久化的字段
@Retention(RetentionPolicy.RUNTIME)
public @interface Transient {
}
