package cn.senninha.sserver.lang;

/**
 * 扫描过滤器
 * @author senninha on 2017年11月3日
 * @param <T>
 *
 */
public interface ClassFilter<T> {

	boolean filter(Class<T> clazz);
}
