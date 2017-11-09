package cn.senninha.sserver.lang.dispatch;

import java.lang.reflect.Method;

/**
 * 封装消息映射 对象+对应的方法
 * @author senninha on 2017年11月8日
 *
 */
public class MessageInvokeWrapper {
	private Object obj;
	private Method method;
	
	public MessageInvokeWrapper(Object obj, Method method) {
		super();
		this.obj = obj;
		this.method = method;
	}
	public Object getObj() {
		return obj;
	}
	public Method getMethod() {
		return method;
	}
	public void setObj(Object obj) {
		this.obj = obj;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MessageInvokeWrapper [obj=");
		builder.append(obj);
		builder.append(", method=");
		builder.append(method);
		builder.append("]");
		return builder.toString();
	}
	
	
}
