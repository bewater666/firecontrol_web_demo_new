package com.orient.firecontrol_web_demo.config.aop;

import java.lang.annotation.*;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/31 11:11
 * @func
 */
@Target({ElementType.PARAMETER,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyLog {
    String description() default "";
}
