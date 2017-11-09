package cn.senninha.sserver.lang.dispatch;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

/**
 * 不同Message对应不同的处理方法
 * @author senninha on 2017年11月7日
 *
 */
@Retention(RUNTIME)
public @interface MessageInvoke {
	public int cmd() default 0;
}
