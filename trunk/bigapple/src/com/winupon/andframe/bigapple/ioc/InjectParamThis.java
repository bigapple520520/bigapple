/* 
 * @(#)InjectParamThis.java    Created on 2012-11-30
 * Copyright (c) 2012 ZDSoft Networks, Inc. All rights reserved.
 * $Id: InjectParamThis.java 32935 2012-11-30 06:17:17Z xuan $
 */
package com.winupon.andframe.bigapple.ioc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在实例化时需要传入this参数
 * 
 * @author xuan
 * @version $Revision: 32935 $, $Date: 2012-11-30 14:17:17 +0800 (周五, 30 十一月 2012) $
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InjectParamThis {

    /**
     * 需要实例化的类的class对象
     * 
     * @return
     */
    Class<?> value();

    /**
     * 注解
     * 
     * @return
     */
    String tag() default "";

}
