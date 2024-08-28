package com.ericsson.nms.security.nscs.handler.validation;

import java.lang.annotation.*;

/**
 * Annotation to associate one or more CommandValidator to a CommandHandler implementation
 * 
 * Created by emaynes on 01/05/2014.
 * 
 * @see com.ericsson.nms.security.nscs.handler.validation.CommandValidator
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface UseValidator {
    Class<? extends CommandValidator>[] value();
}
