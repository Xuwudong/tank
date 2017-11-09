package cn.senninha.sserver.lang.dispatch;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 标记此注解后系统在初始化的时候将会扫描被标记的Handler
 * @author senninha on 2017年11月7日
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageHandler {

}
