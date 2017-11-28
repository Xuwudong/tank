package cn.senninha.sserver.lang.message;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MessageWrapperAnnotation {
	public int cmd() default 0;
}
