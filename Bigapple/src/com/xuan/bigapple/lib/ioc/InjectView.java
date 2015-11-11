package com.xuan.bigapple.lib.ioc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注入View注解
 * 
 * @author xuan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InjectView {

	/**
	 * view的id索引
	 * 
	 * @return
	 */
	int value() default -1;

	/**
	 * 备注
	 * 
	 * @return
	 */
	String tag() default "";

}
