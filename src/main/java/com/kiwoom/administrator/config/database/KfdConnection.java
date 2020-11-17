package com.kiwoom.administrator.config.database;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface KfdConnection {
    String value() default "";
}
