package com.ericsson.nms.security.nscs.data.workflow;

import java.io.Serializable;
import java.util.Date;

import com.ericsson.oss.services.wfs.api.query.progress.WorkflowProgressQueryAttributes;

/**
 *
 */
public class WorkflowStatus implements Serializable{

    private String workflowInstance;
    private String stepName;
    private Date eventTime;
    private String eventType;

    public WorkflowStatus(){}

    public WorkflowStatus(final String workflowInstance, final String stepName, final Date eventTime, final String eventType) {
        this.workflowInstance = workflowInstance;
        this.stepName = stepName;
        this.eventTime = eventTime;
        this.eventType = eventType;
    }

    public boolean isStarted(){
        return (stepName.toLowerCase().contains("start") && eventType.equalsIgnoreCase(WorkflowProgressQueryAttributes.EventType.END.toString()));
    }

    public boolean isCompleted() {
        return ((stepName.toLowerCase().contains("success")||stepName.toLowerCase().equalsIgnoreCase("fail"))&&eventType.equalsIgnoreCase(WorkflowProgressQueryAttributes.EventType.END.toString()));
    }

    public String getWorkflowInstance() {
        return workflowInstance;
    }

    public void setWorkflowInstance(final String workflowInstance) {
        this.workflowInstance = workflowInstance;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(final String stepName) {
        this.stepName = stepName;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(final Date eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(final String eventType) {
        this.eventType = eventType;
    }




    @Override
    public String toString() {
        return "WorkflowStatus{" +
                "workflowInstance='" + workflowInstance + '\'' +
                ", stepName='" + stepName + '\'' +
                ", eventTime=" + eventTime +
                ", eventType='" + eventType + '\'' +
                '}';
    }
}