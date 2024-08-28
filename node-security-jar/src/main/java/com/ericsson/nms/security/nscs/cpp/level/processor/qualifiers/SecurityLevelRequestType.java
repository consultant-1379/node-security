package com.ericsson.nms.security.nscs.cpp.level.processor.qualifiers;

import java.lang.annotation.*;

import javax.inject.Qualifier;

import com.ericsson.nms.security.nscs.cpp.level.SecLevelRequestType;

/**
 * <p>Annotation to associate a SecLevelProcessor implementation to a
 * SecLevelRequestType</p>
 * Created by emaynes on 04/05/2014.
 * @see com.ericsson.nms.security.nscs.cpp.level.SecLevelProcessor
 * @see com.ericsson.nms.security.nscs.cpp.level.SecLevelRequestType
 */
@Qualifier
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = { ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER })
public @interface SecurityLevelRequestType {
    SecLevelRequestType value();
}
