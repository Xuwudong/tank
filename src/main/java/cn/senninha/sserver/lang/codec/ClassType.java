package cn.senninha.sserver.lang.codec;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ClassType {
	Class<?> clazz() default Object.class;
}
