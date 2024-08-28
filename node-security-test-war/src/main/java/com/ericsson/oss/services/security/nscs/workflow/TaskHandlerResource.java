package com.ericsson.oss.services.security.nscs.workflow;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.transaction.UserTransaction;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

import com.ericsson.nms.security.nscs.service.proxy.NSCSServiceBeanProxy;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkFlowNodeTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowActionTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowQueryTask;
import com.ericsson.oss.services.nscs.workflow.tasks.api.request.WorkflowTaskType;
import com.ericsson.oss.services.security.nscs.util.EServiceHolder;

/**
 * Created by emaynes on 01/07/2014.
 */
@Path("task/")
public class TaskHandlerResource {

    @Inject
    private Logger logger;

    @Inject
    EServiceHolder holder;

    @Inject
    NSCSServiceBeanProxy nscsServiceBeanProxy;

    @Inject
    UserTransaction userTransaction;

    @GET
    @Path("action/{task}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response executeAction(@PathParam("task") final String task, @QueryParam("node") final String node) {
        logger.info("Received request to execute WF Task {} at {}", task, node);
        final WorkflowTaskType workflowTaskType = WorkflowTaskType.valueOf(task.toUpperCase());

        final WorkflowActionTask actionTask = new WorkflowActionTask(workflowTaskType, node);

//        workflowTaskService.processTask(actionTask);
        nscsServiceBeanProxy.processWorkflowActionTask(actionTask);

        logger.info("Task executed successfully check the logs.");

        return Response.ok().build();
    }


    @GET
    @Path("query/{task}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response executeQuery(@PathParam("task") final String task, @QueryParam("node") final String node) {
        logger.info("Received request to execute WF Task {} at {}", task, node);
        final WorkflowTaskType workflowTaskType = WorkflowTaskType.valueOf(task.toUpperCase());

        final WorkflowQueryTask queryTask = new WorkflowQueryTask(workflowTaskType, node);

//        final String result = workflowTaskService.processTask(queryTask);
        final String result = nscsServiceBeanProxy.processWorkflowQueryTask(queryTask);


        logger.info("Task executed successfully, result is '{}'. Check the logs.", result);

        return Response.ok().entity(result).build();
    }

    /**
     * Invoke this REST to execute a particular @see {@link WorkflowActionTask}
     *
     * Note that the methods	are specified as Path Segment, whose format is:
	 * ids;method1=attribute1;method2=attribute2;...
	 *
	 * In this way, using reflection, each single method will be invoked on the task with the passed attribute
	 *
	 * Note the starting ids which
	 * is necessary or the first attribute will be lost!
	 *
     * @param task
     * 		The fully qualified name of the task to execute [eg com.ericsson.nms.Task]
     * @param node
     * 		The fdn of the node
     * @param pathSegment
     * 		The methods to be invoked on the task
     * @return
     */
    @GET
    @Path("action/{task}/{node}/{methods}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response executeAction(@PathParam("task") final String task,
    		@PathParam("node") final String node,
    		@PathParam("methods") final PathSegment pathSegment) {
        logger.info("Received request to execute WorkflowActionTask WF Task {} at {} and invoke methods", task, node);

        Object clsInstance = workflowTaskBuilder(task);

        if (clsInstance != null) {
            final WorkflowActionTask actionTask = (WorkflowActionTask) clsInstance;
            actionTask.setNodeFdn(node);

            Map<String, Object> restMethods = extractAttrs(pathSegment);

            invokeWorkflowTaskMethods(clsInstance, actionTask, restMethods);

            nscsServiceBeanProxy.processWorkflowActionTask(actionTask);

            logger.info("Task executed successfully check the logs.");
        }
        return Response.ok().build();
    }

    /**
     * Invoke this REST to execute a particular @see {@link WorkflowQueryTask}
     *
     * Note that the methods	are specified as Path Segment, whose format is:
	 * ids;method1=attribute1;method2=attribute2;...
	 *
	 * In this way, using reflection, each single method will be invoked on the task with the passed attribute
	 *
	 * Note the starting ids which
	 * is necessary or the first attribute will be lost!
	 *
     * @param task
     * 		The fully qualified name of the task to execute [eg com.ericsson.nms.Task]
     * @param node
     * 		The fdn of the node
     * @param pathSegment
     * 		The methods to be invoked on the task
     * @return
     */
    @GET
    @Path("query/{task}/{node}/{methods}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response executeQuery(@PathParam("task") final String task,
            @PathParam("node") final String node,
            @PathParam("methods") final PathSegment pathSegment) {
        logger.info("Received request to execute WorkflowQueryTask WF Task {} at {} and invoke methods", task, node);

        Object clsInstance = workflowTaskBuilder(task);

        final WorkflowQueryTask queryTask = (WorkflowQueryTask) clsInstance;
        queryTask.setNodeFdn(node);

        Map<String, Object> restMethods = extractAttrs(pathSegment);

        invokeWorkflowTaskMethods(clsInstance, queryTask, restMethods);

        final String result = nscsServiceBeanProxy.processWorkflowQueryTask(queryTask);

        logger.info("Task executed successfully, result is '{}'. Check the logs.", result);

        return Response.ok().entity(result).build();
    }

    /**
     * @param clsInstance Instance of task
     * @param workflowTask Specific instance to invoke the methods on
     * @param restMethods List of methods to be invoked and their parameters
     */
    private void invokeWorkflowTaskMethods(Object clsInstance,
            final WorkFlowNodeTask workflowTask, Map<String, Object> restMethods) {
        for (Map.Entry<String, Object> entry : restMethods.entrySet()) {
            logger.info("Looping over method name [{}] value [{}]", entry.getKey(), entry.getValue());

            for (Method method : clsInstance.getClass().getMethods()) {
                logger.info("Searching on methods of instance class, method name [{}]", method.getName());

                if (method.getName().equalsIgnoreCase(entry.getKey())) {
                    //method found!
                    logger.info("Method [{}] invoked with attribute [{}] for Task [{}]",
                            method.getName(),
                            entry.getValue(),
                            workflowTask
                    );
                    try {
                        method.invoke(workflowTask, entry.getValue());
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        logger.error("Exception while invoking Method [{}] with attribute [{}] for Task [{}]",
                                method.getName(),
                                entry.getValue(),
                                workflowTask
                        );
                    }
                    break;
                }
            }
        }
    }

	/**
	 * @param taskFullyQualifiedName
	 *            Fully qualified nameof task to be instantiated
	 * @return The instance of task
	 */
        private Object workflowTaskBuilder(final String taskFullyQualifiedName) {
            String clsName = taskFullyQualifiedName;  // use fully qualified name
            Class<?> cls = null;
            try {
                cls = Class.forName(clsName);
            } catch (ClassNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            Object clsInstance = null;
            if (cls != null) {
                try {
                    clsInstance = cls.newInstance();
                } catch (InstantiationException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
            return clsInstance;
        }


	/**
	 * Extract attributes, present once, from a path segment
	 *
	 * @param pathSegment
	 *            the path segment.
	 * @return the map associating attribute name with its value
	 */
	private Map<String, Object> extractAttrs(PathSegment pathSegment) {
		Map<String, Object> attributes = new HashMap<String, Object>();
		MultivaluedMap<String, String> attrs = pathSegment
				.getMatrixParameters();
		if (attrs != null) {
			logger.debug("keys[{}]", attrs.keySet());
			for (Map.Entry<String, List<String>> entry : attrs.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue().get(0);
				if ("null".equals(value)) {
					value = null;
				}
				logger.debug("key[{}] value[{}]", key, value);
				attributes.put(key, value);
			}
		}
		return attributes;
	}

}
