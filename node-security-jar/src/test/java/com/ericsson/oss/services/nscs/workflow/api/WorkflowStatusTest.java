package com.ericsson.oss.services.nscs.workflow.api;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

import com.ericsson.nms.security.nscs.data.workflow.WorkflowStatus;
import com.ericsson.oss.services.wfs.api.query.progress.WorkflowProgressQueryAttributes;


public class WorkflowStatusTest {

    @Test
    public void testIsCompletedPositive(){
        final WorkflowStatus wfs = new WorkflowStatus("some id","Success",new Date(),WorkflowProgressQueryAttributes.EventType.END.toString());
        assertTrue(wfs.isCompleted());
    }

    @Test
    public void testIsCompletedNegative(){
        final WorkflowStatus wfs = new WorkflowStatus("some id","Success",new Date(),"non End Type");
        assertFalse(wfs.isCompleted());
    }

    @Test
    public void testIsCompletedNegative2(){
        final WorkflowStatus wfs = new WorkflowStatus("some id","Fail",new Date(),"non End Type");
        assertFalse(wfs.isCompleted());
    }

    @Test
    public void testIsCompletedPositive2(){
        final WorkflowStatus wfs = new WorkflowStatus("some id","Fail",new Date(),WorkflowProgressQueryAttributes.EventType.END);
        assertTrue(wfs.isCompleted());
    }


    @Test
    public void testIsStartedPositive(){
        final WorkflowStatus wfs = new WorkflowStatus("some id","Start__prg",new Date(),WorkflowProgressQueryAttributes.EventType.END.toString());
        assertTrue(wfs.isStarted());
    }

    @Test
    public void testIsStartedNegative(){
        final WorkflowStatus wfs = new WorkflowStatus("some id","start",new Date(),"any other end Type");
        assertFalse(wfs.isStarted());
    }

    @Test
    public void testIsStartedNegative2(){
        final WorkflowStatus wfs = new WorkflowStatus("some id","SomeName",new Date(),WorkflowProgressQueryAttributes.EventType.END);
        assertFalse(wfs.isStarted());
    }


    @Test
    public void testGettersAndSetters() throws Exception {
        final WorkflowStatus wfs = new WorkflowStatus();
        wfs.setEventTime(new Date(23111));
        assertEquals(wfs.getEventTime(),new Date(23111) );
        wfs.setEventType(WorkflowProgressQueryAttributes.EventType.END);
        assertEquals(wfs.getEventType(),WorkflowProgressQueryAttributes.EventType.END );
        wfs.setStepName("some step name");
        assertEquals(wfs.getStepName(), "some step name");
        wfs.setWorkflowInstance("some wfsInstance");
        assertEquals(wfs.getWorkflowInstance(),"some wfsInstance" );
    }


    @Test
    public void testToString(){
	    //TODO: make it more robust (does not work in different time zone
        final WorkflowStatus wfs = new WorkflowStatus("some id","Success",new Date(23342),WorkflowProgressQueryAttributes.EventType.END.toString());
        //assertEquals(wfs.toString(), "WorkflowStatus{workflowInstance='some id', stepName='Success', eventTime=Thu Jan 01 01:00:23 GMT 1970, eventType='end'}");
    }



}
