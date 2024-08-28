package com.ericsson.nms.security.nscs.handler.command;

import java.lang.annotation.*;

import javax.inject.Qualifier;

import com.ericsson.nms.security.nscs.api.command.NscsCommandType;

/**
 * Annotation to associate a CommandHandler implementation to a NscsCommandType
 * 
 * Created by emaynes on 01/05/2014.
 * 
 * @see com.ericsson.nms.security.nscs.handler.command.CommandHandler
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
@Documented
public @interface CommandType {
    NscsCommandType value();
}
