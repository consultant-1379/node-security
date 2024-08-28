package com.ericsson.oss.services.nscs.workflow.tasks.api.request;

import java.lang.annotation.*;

/**
 * This annotation indicates that a specific ActionTask should be ran
 * in parallel (asynchronously) with the workflow.
 * @author emaynes.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Documented
@Inherited
public @interface AsyncActionTask {
    /**
     * define the message that should be sent to workflow if the task
     * execution succeeds. If blank, no message will be sent to workflow.
     * @return message String
     */
    String successMessage() default "";

    /**
     * define the message that should be sent to workflow if the task
     * execution fails. If blank, no message will be sent to workflow.
     * @return message String
     */
    String errorMessage() default "";
}
